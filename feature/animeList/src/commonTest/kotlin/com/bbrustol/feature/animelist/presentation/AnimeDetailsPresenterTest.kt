package com.bbrustol.feature.animelist.presentation

import app.cash.turbine.test
import com.bbrustol.feature.animelist.data.remote.response.AnimeListResponse
import com.bbrustol.feature.animelist.domain.model.mapper.toDomainModel
import com.bbrustol.feature.animelist.presentation.AnimeDetailsEvent.LoadAnime
import com.bbrustol.feature.animelist.presentation.AnimeDetailsEvent.NavigateBack
import com.bbrustol.feature.animelist.presentation.AnimeDetailsSideEffect.GoBack
import com.bbrustol.feature.animelist.presentation.AnimeDetailsUiState.AnimeDetails
import com.bbrustol.feature.animelist.presentation.AnimeDetailsUiState.Idle
import com.bbrustol.feature.animelist.presentation.mock.animeListMock
import com.bbrustol.feature.animelist.presentation.mock.previewJson
import com.bbrustol.feature.animelist.presentation.model.mapper.toUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class AnimeDetailsPresenterTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private lateinit var sharedViewModel: SharedAnimeViewModel
    private lateinit var presenter: AnimeDetailsPresenter

    private val firstAnime = previewJson.decodeFromString<AnimeListResponse>(animeListMock)
        .toDomainModel().items.first().toUiModel()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        sharedViewModel = SharedAnimeViewModel()
        presenter = AnimeDetailsPresenter(sharedViewModel)
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
    fun `LoadAnime emits AnimeDetails state when anime is selected`() = runTest(testDispatcher) {
        sharedViewModel.selectAnime(firstAnime)

        presenter.uiState.test {
            assertEquals(Idle, awaitItem())
            presenter.dispatch(LoadAnime)
            val state = awaitItem()
            assertIs<AnimeDetails>(state)
            assertEquals(firstAnime, state.anime)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `NavigateBack sends GoBack side effect`() = runTest(testDispatcher) {
        presenter.sideEffect.test {
            presenter.dispatch(NavigateBack)
            assertEquals(GoBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `NavigateBack clears anime selection from shared view model`() = runTest(testDispatcher) {
        sharedViewModel.selectAnime(firstAnime)
        presenter.dispatch(NavigateBack)
        advanceUntilIdle()
        assertNull(sharedViewModel.selectedAnime.value)
    }
}
