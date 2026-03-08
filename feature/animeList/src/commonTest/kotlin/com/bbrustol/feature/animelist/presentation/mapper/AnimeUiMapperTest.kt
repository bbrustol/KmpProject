package com.bbrustol.feature.animelist.presentation.mapper

import com.bbrustol.feature.animelist.data.remote.response.AnimeListResponse
import com.bbrustol.feature.animelist.domain.model.mapper.toDomainModel
import com.bbrustol.feature.animelist.presentation.mock.animeListMock
import com.bbrustol.feature.animelist.presentation.mock.previewJson
import com.bbrustol.feature.animelist.presentation.model.mapper.toUiModel
import com.bbrustol.feature.animelist.presentation.model.mapper.toUiModels
import kotlin.test.Test
import kotlin.test.assertEquals

class AnimeUiMapperTest {

    private val domainList = previewJson.decodeFromString<AnimeListResponse>(animeListMock).toDomainModel()
    private val firstAnime = domainList.items.first()

    @Test
    fun `maps domain fields to ui model correctly`() {
        val ui = firstAnime.toUiModel()

        assertEquals(1, ui.malId)
        assertEquals("Fullmetal Alchemist: Brotherhood", ui.title)
        assertEquals("Fullmetal Alchemist: Brotherhood", ui.titleEnglish)
        assertEquals("64", ui.episodes)
        assertEquals("9.08", ui.score)
        assertEquals("#1", ui.rank)
        assertEquals("Spring 2009", ui.season)
        assertEquals("Finished Airing", ui.status)
        assertEquals("TV", ui.type)
        assertEquals(listOf("Action", "Drama"), ui.genres)
    }

    @Test
    fun `uses title as titleEnglish when titleEnglish is null`() {
        val ui = firstAnime.copy(titleEnglish = null).toUiModel()
        assertEquals(firstAnime.title, ui.titleEnglish)
    }

    @Test
    fun `formats episodes as question mark when null`() {
        assertEquals("?", firstAnime.copy(episodes = null).toUiModel().episodes)
    }

    @Test
    fun `formats score as NA when null`() {
        assertEquals("N/A", firstAnime.copy(score = null).toUiModel().score)
    }

    @Test
    fun `formats rank as NA when null`() {
        assertEquals("N/A", firstAnime.copy(rank = null).toUiModel().rank)
    }

    @Test
    fun `capitalizes season and appends year`() {
        assertEquals("Winter 2022", firstAnime.copy(season = "winter", year = 2022).toUiModel().season)
    }

    @Test
    fun `shows only year when season is null`() {
        assertEquals("2009", firstAnime.copy(season = null).toUiModel().season)
    }

    @Test
    fun `shows empty string when both season and year are null`() {
        assertEquals("", firstAnime.copy(season = null, year = null).toUiModel().season)
    }

    @Test
    fun `maps all items preserving order`() {
        val uiList = domainList.toUiModels()

        assertEquals(4, uiList.size)
        assertEquals(1, uiList[0].malId)
        assertEquals(2, uiList[1].malId)
        assertEquals(3, uiList[2].malId)
        assertEquals(4, uiList[3].malId)
    }
}
