package com.bbrustol.feature.animelist.domain.model.mapper

import com.bbrustol.feature.animelist.data.remote.response.AnimeListResponse
import com.bbrustol.feature.animelist.data.remote.response.AnimeResponse
import com.bbrustol.feature.animelist.domain.model.AnimeListDomainModel
import com.bbrustol.feature.animelist.domain.model.AnimeDomainModel

fun AnimeListResponse.toDomainModel(): AnimeListDomainModel = AnimeListDomainModel(
    items = data.map { it.toDomainModel() },
    hasNextPage = pagination.hasNextPage,
    nextPage = pagination.currentPage + 1,
)

fun AnimeResponse.toDomainModel(): AnimeDomainModel = AnimeDomainModel(
    malId = malId,
    title = title,
    titleEnglish = titleEnglish,
    imageUrl = images.jpg.imageUrl.orEmpty(),
    largeImageUrl = images.jpg.largeImageUrl ?: images.jpg.imageUrl.orEmpty(),
    episodes = episodes,
    score = score,
    rank = rank,
    synopsis = synopsis,
    status = status,
    type = type,
    season = season,
    year = year,
    genres = genres.map { it.name },
)
