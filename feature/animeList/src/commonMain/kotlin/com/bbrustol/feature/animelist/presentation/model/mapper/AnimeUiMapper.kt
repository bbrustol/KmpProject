package com.bbrustol.feature.animelist.presentation.model.mapper

import com.bbrustol.feature.animelist.domain.model.AnimeListDomainModel
import com.bbrustol.feature.animelist.domain.model.AnimeDomainModel
import com.bbrustol.feature.animelist.presentation.model.AnimeUiModel

fun AnimeListDomainModel.toUiModels(): List<AnimeUiModel> = items.map { it.toUiModel() }

fun AnimeDomainModel.toUiModel(): AnimeUiModel = AnimeUiModel(
    malId = malId,
    title = title,
    titleEnglish = titleEnglish ?: title,
    imageUrl = imageUrl,
    largeImageUrl = largeImageUrl,
    episodes = episodes?.toString() ?: "?",
    score = score?.toString() ?: "N/A",
    rank = rank?.let { "#$it" } ?: "N/A",
    synopsis = synopsis ?: "",
    status = status ?: "",
    type = type ?: "",
    season = when {
        season != null && year != null -> "${season.replaceFirstChar { it.uppercase() }} $year"
        year != null -> year.toString()
        else -> ""
    },
    genres = genres,
)
