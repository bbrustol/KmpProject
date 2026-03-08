package com.bbrustol.feature.animelist.data.service

import com.bbrustol.core.infrastructure.network.ApiResult
import com.bbrustol.feature.animelist.data.remote.response.AnimeListResponse

interface AnimeService {
    suspend fun getTopAnime(page: Int): ApiResult<AnimeListResponse>
}
