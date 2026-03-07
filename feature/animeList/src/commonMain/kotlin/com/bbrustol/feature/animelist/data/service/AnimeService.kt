package com.bbrustol.feature.animelist.data.service

import com.bbrustol.core.infrastructure.network.ApiHandler.handleApi
import com.bbrustol.core.infrastructure.network.ApiResult
import com.bbrustol.feature.animelist.data.remote.response.AnimeListResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.path

class AnimeService(private val httpClient: HttpClient) {

    suspend fun getTopAnime(page: Int): ApiResult<AnimeListResponse> = handleApi {
        httpClient.get {
            url {
                path("v4", "top", "anime")
                parameters.append(PARAM_PAGE, page.toString())
            }
        }
    }

    companion object {
        private const val PARAM_PAGE = "page"
    }
}
