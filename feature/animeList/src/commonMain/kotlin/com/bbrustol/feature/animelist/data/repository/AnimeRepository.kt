package com.bbrustol.feature.animelist.data.repository

import com.bbrustol.core.infrastructure.network.ApiError
import com.bbrustol.core.infrastructure.network.ApiException
import com.bbrustol.core.infrastructure.network.ApiResult
import com.bbrustol.core.infrastructure.network.ApiSuccess
import com.bbrustol.feature.animelist.data.service.AnimeService
import com.bbrustol.feature.animelist.domain.model.AnimeListDomainModel
import com.bbrustol.feature.animelist.domain.model.mapper.toDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AnimeRepository(
    private val animeService: AnimeService,
    private val dispatcher: CoroutineDispatcher,
) {
    fun getTopAnime(page: Int): Flow<ApiResult<AnimeListDomainModel>> = flow {
        when (val result = animeService.getTopAnime(page)) {
            is ApiSuccess -> emit(ApiSuccess(result.data.toDomainModel()))
            is ApiError -> emit(ApiError(result.code, result.message, result.serviceStatusType))
            is ApiException -> emit(ApiException(result.throwable, result.serviceStatusType))
        }
    }.flowOn(dispatcher)
}
