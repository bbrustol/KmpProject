package com.bbrustol.feature.animelist.domain.repository

import com.bbrustol.core.infrastructure.network.ApiResult
import com.bbrustol.feature.animelist.domain.model.AnimeListDomainModel
import kotlinx.coroutines.flow.Flow

interface AnimeRepository {
    fun getTopAnime(page: Int): Flow<ApiResult<AnimeListDomainModel>>
}
