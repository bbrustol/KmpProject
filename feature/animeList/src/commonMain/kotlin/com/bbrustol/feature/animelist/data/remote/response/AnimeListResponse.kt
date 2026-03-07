package com.bbrustol.feature.animelist.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeListResponse(
    @SerialName("data") val data: List<AnimeResponse>,
    @SerialName("pagination") val pagination: PaginationResponse,
)

@Serializable
data class PaginationResponse(
    @SerialName("last_visible_page") val lastVisiblePage: Int,
    @SerialName("has_next_page") val hasNextPage: Boolean,
    @SerialName("current_page") val currentPage: Int,
)

@Serializable
data class AnimeResponse(
    @SerialName("mal_id") val malId: Int,
    @SerialName("title") val title: String,
    @SerialName("title_english") val titleEnglish: String? = null,
    @SerialName("images") val images: AnimeImagesResponse,
    @SerialName("episodes") val episodes: Int? = null,
    @SerialName("score") val score: Double? = null,
    @SerialName("rank") val rank: Int? = null,
    @SerialName("synopsis") val synopsis: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("season") val season: String? = null,
    @SerialName("year") val year: Int? = null,
    @SerialName("genres") val genres: List<GenreResponse> = emptyList(),
)

@Serializable
data class AnimeImagesResponse(
    @SerialName("jpg") val jpg: AnimeImageResponse,
)

@Serializable
data class AnimeImageResponse(
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("large_image_url") val largeImageUrl: String? = null,
)

@Serializable
data class GenreResponse(
    @SerialName("mal_id") val malId: Int,
    @SerialName("name") val name: String,
)
