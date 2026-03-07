package com.bbrustol.feature.animelist.presentation

import androidx.lifecycle.viewModelScope
import com.bbrustol.core.infrastructure.BasePresenter
import com.bbrustol.feature.animelist.presentation.model.AnimeUiModel
import com.bbrustol.feature.animelist.presentation.AnimeDetailsEvent.*
import com.bbrustol.feature.animelist.presentation.AnimeDetailsSideEffect.*
import com.bbrustol.feature.animelist.presentation.AnimeDetailsUiState.*
import kotlinx.coroutines.launch

internal sealed interface AnimeDetailsUiState {
    data object Idle : AnimeDetailsUiState
    data class AnimeDetails(val anime: AnimeUiModel) : AnimeDetailsUiState
}

internal sealed interface AnimeDetailsEvent {
    data object LoadAnime : AnimeDetailsEvent
    data object NavigateBack : AnimeDetailsEvent
}

internal sealed interface AnimeDetailsSideEffect {
    data object GoBack : AnimeDetailsSideEffect
}

internal class AnimeDetailsPresenter(
    private val sharedAnimeViewModel: SharedAnimeViewModel,
) : BasePresenter<AnimeDetailsEvent, AnimeDetailsUiState, AnimeDetailsSideEffect>() {

    override fun setInitialState(): AnimeDetailsUiState = Idle

    override fun process(event: AnimeDetailsEvent) {
        when (event) {
            LoadAnime -> loadAnime()
            NavigateBack -> {
                sharedAnimeViewModel.clearSelection()
                sendSideEffect { GoBack }
            }
        }
    }

    private fun loadAnime() {
        viewModelScope.launch {
            sharedAnimeViewModel.selectedAnime.collect { anime ->
                if (anime != null) {
                    updateState { AnimeDetails(anime) }
                }
            }
        }
    }
}
