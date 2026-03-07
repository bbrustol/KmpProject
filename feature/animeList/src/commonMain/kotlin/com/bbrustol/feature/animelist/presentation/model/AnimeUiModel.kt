package com.bbrustol.feature.animelist.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class AnimeUiModel(
    val malId: Int,
    val title: String,
    val titleEnglish: String,
    val imageUrl: String,
    val largeImageUrl: String,
    val episodes: String,
    val score: String,
    val rank: String,
    val synopsis: String,
    val status: String,
    val type: String,
    val season: String,
    val genres: List<String>,
)
