package com.bbrustol.feature.animelist.presentation.mock

import kotlinx.serialization.json.Json

internal val previewJson = Json { ignoreUnknownKeys = true }

internal val animeListMock = """
{
  "pagination": { "last_visible_page": 10, "has_next_page": true, "current_page": 1 },
  "data": [
    { "mal_id": 1, "title": "Fullmetal Alchemist: Brotherhood", "title_english": "Fullmetal Alchemist: Brotherhood", "images": { "jpg": { "image_url": "", "large_image_url": "" } }, "episodes": 64, "score": 9.08, "rank": 1, "synopsis": "After a horrific alchemy experiment goes wrong, brothers Edward and Alphonse are left in a catastrophic new reality.", "status": "Finished Airing", "type": "TV", "season": "spring", "year": 2009, "genres": [{ "mal_id": 1, "name": "Action" }, { "mal_id": 8, "name": "Drama" }] },
    { "mal_id": 2, "title": "Steins;Gate", "title_english": "Steins;Gate", "images": { "jpg": { "image_url": "", "large_image_url": "" } }, "episodes": 24, "score": 9.07, "rank": 2, "synopsis": "A self-proclaimed mad scientist discovers time travel.", "status": "Finished Airing", "type": "TV", "season": "spring", "year": 2011, "genres": [{ "mal_id": 8, "name": "Drama" }, { "mal_id": 24, "name": "Sci-Fi" }] },
    { "mal_id": 3, "title": "Gintama°", "title_english": "Gintama Season 4", "images": { "jpg": { "image_url": "", "large_image_url": "" } }, "episodes": 51, "score": 9.06, "rank": 3, "synopsis": "Gintoki and friends continue their freelance work.", "status": "Finished Airing", "type": "TV", "season": "spring", "year": 2015, "genres": [{ "mal_id": 1, "name": "Action" }, { "mal_id": 4, "name": "Comedy" }] },
    { "mal_id": 4, "title": "Attack on Titan Season 3 Part 2", "title_english": "Attack on Titan Season 3 Part 2", "images": { "jpg": { "image_url": "", "large_image_url": "" } }, "episodes": 10, "score": 9.05, "rank": 4, "synopsis": "The Survey Corps march toward Wall Maria.", "status": "Finished Airing", "type": "TV", "season": "spring", "year": 2019, "genres": [{ "mal_id": 1, "name": "Action" }, { "mal_id": 10, "name": "Fantasy" }] }
  ]
}
""".trimIndent()
