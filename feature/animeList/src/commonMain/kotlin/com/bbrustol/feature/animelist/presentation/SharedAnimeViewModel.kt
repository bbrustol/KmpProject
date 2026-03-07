package com.bbrustol.feature.animelist.presentation

import androidx.lifecycle.ViewModel
import com.bbrustol.feature.animelist.presentation.model.AnimeUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedAnimeViewModel : ViewModel() {
    private val _selectedAnime = MutableStateFlow<AnimeUiModel?>(null)
    val selectedAnime: StateFlow<AnimeUiModel?> = _selectedAnime.asStateFlow()

    fun selectAnime(anime: AnimeUiModel) {
        _selectedAnime.value = anime
    }

    fun clearSelection() {
        _selectedAnime.value = null
    }
}
