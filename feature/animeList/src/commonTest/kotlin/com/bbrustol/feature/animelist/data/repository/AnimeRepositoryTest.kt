package com.bbrustol.feature.animelist.data.repository

import app.cash.turbine.test
import com.bbrustol.core.infrastructure.network.ApiError
import com.bbrustol.core.infrastructure.network.ApiException
import com.bbrustol.core.infrastructure.network.ApiSuccess
import com.bbrustol.core.infrastructure.network.ServerStatusType
import com.bbrustol.feature.animelist.data.remote.response.AnimeListResponse
import com.bbrustol.feature.animelist.data.service.AnimeService
import com.bbrustol.feature.animelist.domain.model.AnimeListDomainModel
import com.bbrustol.feature.animelist.presentation.mock.animeListMock
import com.bbrustol.feature.animelist.presentation.mock.previewJson
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class AnimeRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val animeService = mock<AnimeService>()
    private val repository = AnimeRepositoryImpl(animeService, testDispatcher)

    private val mockResponse = previewJson.decodeFromString<AnimeListResponse>(animeListMock)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `emits mapped domain model on success`() = runTest {
        everySuspend { animeService.getTopAnime(1) } returns ApiSuccess(mockResponse)

        repository.getTopAnime(1).test {
            val result = awaitItem()
            assertIs<ApiSuccess<AnimeListDomainModel>>(result)
            assertEquals(mockResponse.data.size, result.data.items.size)
            assertEquals(mockResponse.data.first().title, result.data.items.first().title)
            assertEquals(mockResponse.pagination.currentPage + 1, result.data.nextPage)
            awaitComplete()
        }
    }

    @Test
    fun `emits ApiError preserving code and message`() = runTest {
        everySuspend { animeService.getTopAnime(1) } returns ApiError(
            code = 429,
            message = "Too Many Requests",
            serviceStatusType = ServerStatusType.UnknownError,
        )

        repository.getTopAnime(1).test {
            val result = awaitItem()
            assertIs<ApiError<AnimeListDomainModel>>(result)
            assertEquals(429, result.code)
            assertEquals("Too Many Requests", result.message)
            awaitComplete()
        }
    }

    @Test
    fun `emits ApiException preserving service status type`() = runTest {
        everySuspend { animeService.getTopAnime(1) } returns ApiException(
            throwable = RuntimeException("Timeout"),
            serviceStatusType = ServerStatusType.InternetConnectionProblems,
        )

        repository.getTopAnime(1).test {
            val result = awaitItem()
            assertIs<ApiException<AnimeListDomainModel>>(result)
            assertEquals(ServerStatusType.InternetConnectionProblems, result.serviceStatusType)
            awaitComplete()
        }
    }
}
