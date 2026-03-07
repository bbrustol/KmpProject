package com.bbrustol.feature.animelist.presentation

import androidx.lifecycle.viewModelScope
import com.bbrustol.core.infrastructure.BasePresenter
import com.bbrustol.core.infrastructure.network.ApiError
import com.bbrustol.core.infrastructure.network.ApiException
import com.bbrustol.core.infrastructure.network.ApiSuccess
import com.bbrustol.core.infrastructure.network.ServerStatusType
import com.bbrustol.feature.animelist.data.repository.AnimeRepository
import com.bbrustol.feature.animelist.domain.model.AnimeListDomainModel
import com.bbrustol.feature.animelist.presentation.model.AnimeUiModel
import com.bbrustol.feature.animelist.presentation.model.mapper.toUiModels
import com.bbrustol.feature.animelist.presentation.AnimeListEvent.*
import com.bbrustol.feature.animelist.presentation.AnimeListSideEffect.*
import com.bbrustol.feature.animelist.presentation.AnimeListUiState.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

internal sealed interface AnimeListUiState {
    data object Idle : AnimeListUiState
    data class AnimeList(
        val list: List<AnimeUiModel> = emptyList(),
        val isLoading: Boolean = false,
        val hasNextPage: Boolean = true,
        val nextPage: Int = 1,
    ) : AnimeListUiState
    data class ShowError(val code: Int, val message: String?) : AnimeListUiState
    data class ShowException(val throwable: Throwable?) : AnimeListUiState
    data object ShowNoInternet : AnimeListUiState
}

internal sealed interface AnimeListEvent {
    data object LoadTopAnime : AnimeListEvent
    data object LoadMore : AnimeListEvent
    data class GetDetails(val anime: AnimeUiModel) : AnimeListEvent
}

internal sealed interface AnimeListSideEffect {
    data object GotoDetails : AnimeListSideEffect
}

internal class AnimeListPresenter(
    private val animeRepository: AnimeRepository,
    private val sharedAnimeViewModel: SharedAnimeViewModel,
) : BasePresenter<AnimeListEvent, AnimeListUiState, AnimeListSideEffect>() {

    override fun setInitialState(): AnimeListUiState = Idle

    override fun process(event: AnimeListEvent) {
        when (event) {
            LoadTopAnime -> fetchAnime(page = 1, isNewLoad = true)
            LoadMore -> {
                val current = uiState.value as? AnimeList ?: return
                if (!current.isLoading && current.hasNextPage) {
                    fetchAnime(page = current.nextPage, isNewLoad = false)
                }
            }
            is GetDetails -> {
                sharedAnimeViewModel.selectAnime(event.anime)
                sendSideEffect { GotoDetails }
            }
        }
    }

    private fun fetchAnime(page: Int, isNewLoad: Boolean) {
        viewModelScope.launch {
            animeRepository.getTopAnime(page)
                .onStart { setLoading() }
                .catch { updateState { ShowException(it) } }
                .collect { result ->
                    when (result) {
                        is ApiSuccess -> {
                            if (isNewLoad) initList(result.data)
                            else appendList(result.data)
                        }
                        is ApiError -> updateState { ShowError(result.code, result.message) }
                        is ApiException -> updateState {
                            when (result.serviceStatusType) {
                                ServerStatusType.ServiceUnavailable,
                                ServerStatusType.InternetConnectionProblems -> ShowNoInternet
                                else -> ShowException(result.throwable)
                            }
                        }
                    }
                }
        }
    }

    private fun setLoading() {
        val current = uiState.value as? AnimeList
        updateState { current?.copy(isLoading = true) ?: AnimeList(isLoading = true) }
    }

    private fun initList(data: AnimeListDomainModel) {
        updateState {
            AnimeList(
                list = data.toUiModels(),
                isLoading = false,
                hasNextPage = data.hasNextPage,
                nextPage = data.nextPage,
            )
        }
    }

    private fun appendList(data: AnimeListDomainModel) {
        val current = uiState.value as? AnimeList ?: return
        updateState {
            current.copy(
                list = current.list + data.toUiModels(),
                isLoading = false,
                hasNextPage = data.hasNextPage,
                nextPage = data.nextPage,
            )
        }
    }
}
