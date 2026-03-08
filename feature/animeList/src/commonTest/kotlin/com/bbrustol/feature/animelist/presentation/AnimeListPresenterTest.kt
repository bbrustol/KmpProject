package com.bbrustol.feature.animelist.presentation

import app.cash.turbine.test
import com.bbrustol.core.infrastructure.network.ApiError
import com.bbrustol.core.infrastructure.network.ApiException
import com.bbrustol.core.infrastructure.network.ApiSuccess
import com.bbrustol.core.infrastructure.network.ServerStatusType
import com.bbrustol.feature.animelist.data.remote.response.AnimeListResponse
import com.bbrustol.feature.animelist.domain.model.mapper.toDomainModel
import com.bbrustol.feature.animelist.domain.repository.AnimeRepository
import com.bbrustol.feature.animelist.presentation.AnimeListEvent.GetDetails
import com.bbrustol.feature.animelist.presentation.AnimeListEvent.LoadMore
import com.bbrustol.feature.animelist.presentation.AnimeListEvent.LoadTopAnime
import com.bbrustol.feature.animelist.presentation.AnimeListSideEffect.GotoDetails
import com.bbrustol.feature.animelist.presentation.AnimeListUiState.AnimeList
import com.bbrustol.feature.animelist.presentation.AnimeListUiState.Idle
import com.bbrustol.feature.animelist.presentation.AnimeListUiState.ShowError
import com.bbrustol.feature.animelist.presentation.AnimeListUiState.ShowNoInternet
import com.bbrustol.feature.animelist.presentation.mock.animeListMock
import com.bbrustol.feature.animelist.presentation.mock.previewJson
import com.bbrustol.feature.animelist.presentation.model.mapper.toUiModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AnimeListPresenterTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private lateinit var repository: AnimeRepository
    private lateinit var sharedViewModel: SharedAnimeViewModel
    private lateinit var presenter: AnimeListPresenter

    private val mockDomain = previewJson.decodeFromString<AnimeListResponse>(animeListMock).toDomainModel()
    private val firstAnime = mockDomain.items.first().toUiModel()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock<AnimeRepository>()
        sharedViewModel = SharedAnimeViewModel()
        presenter = AnimeListPresenter(repository, sharedViewModel)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        assertEquals(Idle, presenter.uiState.value)
    }

    @Test
    fun `LoadTopAnime transitions through loading to success`() = runTest(testDispatcher) {
        every { repository.getTopAnime(1) } returns flowOf(ApiSuccess(mockDomain.copy(nextPage = 2)))

        presenter.uiState.test {
            assertEquals(Idle, awaitItem())
            presenter.dispatch(LoadTopAnime)
            assertTrue((awaitItem() as AnimeList).isLoading)
            val success = awaitItem() as AnimeList
            assertFalse(success.isLoading)
            assertEquals(mockDomain.items.size, success.list.size)
            assertEquals(2, success.nextPage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadTopAnime shows error state on ApiError`() = runTest(testDispatcher) {
        every { repository.getTopAnime(1) } returns flowOf(
            ApiError(code = 503, message = "Service Unavailable", serviceStatusType = ServerStatusType.ServiceUnavailable)
        )

        presenter.uiState.test {
            assertEquals(Idle, awaitItem())
            presenter.dispatch(LoadTopAnime)
            awaitItem() // loading
            val error = awaitItem()
            assertIs<ShowError>(error)
            assertEquals(503, error.code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadTopAnime shows no internet on internet connection exception`() = runTest(testDispatcher) {
        every { repository.getTopAnime(1) } returns flowOf(
            ApiException(throwable = null, serviceStatusType = ServerStatusType.InternetConnectionProblems)
        )

        presenter.uiState.test {
            assertEquals(Idle, awaitItem())
            presenter.dispatch(LoadTopAnime)
            awaitItem() // loading
            assertIs<ShowNoInternet>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadMore appends items from next page`() = runTest(testDispatcher) {
        every { repository.getTopAnime(1) } returns flowOf(ApiSuccess(mockDomain.copy(nextPage = 2)))
        every { repository.getTopAnime(2) } returns flowOf(ApiSuccess(mockDomain.copy(nextPage = 3)))

        presenter.uiState.test {
            assertEquals(Idle, awaitItem())
            presenter.dispatch(LoadTopAnime)
            awaitItem() // loading
            val firstPage = awaitItem() as AnimeList
            assertEquals(mockDomain.items.size, firstPage.list.size)

            presenter.dispatch(LoadMore)
            awaitItem() // loading
            val secondPage = awaitItem() as AnimeList
            assertEquals(mockDomain.items.size * 2, secondPage.list.size)
            assertEquals(3, secondPage.nextPage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadMore does nothing when hasNextPage is false`() = runTest(testDispatcher) {
        every { repository.getTopAnime(any()) } returns flowOf(ApiSuccess(mockDomain.copy(hasNextPage = false)))

        presenter.uiState.test {
            assertEquals(Idle, awaitItem())
            presenter.dispatch(LoadTopAnime)
            awaitItem() // loading
            assertFalse((awaitItem() as AnimeList).hasNextPage)

            presenter.dispatch(LoadMore)
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetDetails sends GotoDetails side effect`() = runTest(testDispatcher) {
        presenter.sideEffect.test {
            presenter.dispatch(GetDetails(firstAnime))
            assertEquals(GotoDetails, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GetDetails stores anime in shared view model`() = runTest(testDispatcher) {
        presenter.sideEffect.test {
            presenter.dispatch(GetDetails(firstAnime))
            awaitItem() // GotoDetails — confirms process() ran including selectAnime
            assertEquals(firstAnime, sharedViewModel.selectedAnime.value)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
