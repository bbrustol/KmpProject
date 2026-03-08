package com.bbrustol.feature.animelist.data.mapper

import com.bbrustol.feature.animelist.data.remote.response.AnimeListResponse
import com.bbrustol.feature.animelist.domain.model.mapper.toDomainModel
import com.bbrustol.feature.animelist.presentation.mock.animeListMock
import com.bbrustol.feature.animelist.presentation.mock.previewJson
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AnimeDomainMapperTest {

    private val listResponse = previewJson.decodeFromString<AnimeListResponse>(animeListMock)
    private val firstAnime = listResponse.data.first()

    @Test
    fun `maps all AnimeResponse fields correctly`() {
        val domain = firstAnime.toDomainModel()

        assertEquals(1, domain.malId)
        assertEquals("Fullmetal Alchemist: Brotherhood", domain.title)
        assertEquals("Fullmetal Alchemist: Brotherhood", domain.titleEnglish)
        assertEquals(64, domain.episodes)
        assertEquals(9.08, domain.score)
        assertEquals(1, domain.rank)
        assertEquals("Finished Airing", domain.status)
        assertEquals("TV", domain.type)
        assertEquals("spring", domain.season)
        assertEquals(2009, domain.year)
        assertEquals(listOf("Action", "Drama"), domain.genres)
    }

    @Test
    fun `uses imageUrl as fallback for largeImageUrl when large is null`() {
        val response = firstAnime.copy(
            images = firstAnime.images.copy(
                jpg = firstAnime.images.jpg.copy(largeImageUrl = null)
            )
        )
        val domain = response.toDomainModel()

        assertEquals(domain.imageUrl, domain.largeImageUrl)
    }

    @Test
    fun `maps nullable fields as null`() {
        val domain = firstAnime.copy(
            titleEnglish = null, episodes = null, score = null, rank = null,
            synopsis = null, status = null, type = null, season = null, year = null,
        ).toDomainModel()

        assertNull(domain.titleEnglish)
        assertNull(domain.episodes)
        assertNull(domain.score)
        assertNull(domain.rank)
        assertNull(domain.synopsis)
        assertNull(domain.status)
        assertNull(domain.type)
        assertNull(domain.season)
        assertNull(domain.year)
    }

    @Test
    fun `maps empty genres list`() {
        val domain = firstAnime.copy(genres = emptyList()).toDomainModel()
        assertEquals(emptyList(), domain.genres)
    }

    @Test
    fun `calculates nextPage as currentPage plus one`() {
        val domain = listResponse.toDomainModel()

        assertEquals(listResponse.pagination.currentPage + 1, domain.nextPage)
        assertEquals(listResponse.pagination.hasNextPage, domain.hasNextPage)
    }

    @Test
    fun `maps all items in list preserving order`() {
        val domain = listResponse.toDomainModel()

        assertEquals(listResponse.data.size, domain.items.size)
        listResponse.data.forEachIndexed { index, response ->
            assertEquals(response.malId, domain.items[index].malId)
        }
    }
}
