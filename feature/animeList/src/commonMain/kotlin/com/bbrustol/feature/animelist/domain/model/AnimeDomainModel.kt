package com.bbrustol.feature.animelist.domain.model

data class AnimeListDomainModel(
    val items: List<AnimeDomainModel>,
    val hasNextPage: Boolean,
    val nextPage: Int,
)

data class AnimeDomainModel(
    val malId: Int,
    val title: String,
    val titleEnglish: String?,
    val imageUrl: String,
    val largeImageUrl: String,
    val episodes: Int?,
    val score: Double?,
    val rank: Int?,
    val synopsis: String?,
    val status: String?,
    val type: String?,
    val season: String?,
    val year: Int?,
    val genres: List<String>,
)
