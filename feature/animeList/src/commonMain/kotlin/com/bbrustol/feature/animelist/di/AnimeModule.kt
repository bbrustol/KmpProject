package com.bbrustol.feature.animelist.di

import com.bbrustol.feature.animelist.data.service.AnimeService
import com.bbrustol.feature.animelist.data.repository.AnimeRepository
import com.bbrustol.feature.animelist.presentation.AnimeDetailsPresenter
import com.bbrustol.feature.animelist.presentation.AnimeListPresenter
import com.bbrustol.feature.animelist.presentation.SharedAnimeViewModel
import com.bbrustol.core.infrastructure.di.DispatcherQualifier
import io.ktor.client.HttpClient
import org.koin.dsl.module

val animeModule = module {
    factory { AnimeListPresenter(get(), get()) }
    factory { AnimeDetailsPresenter(get()) }
    single { SharedAnimeViewModel() }

    single {
        AnimeRepository(
            animeService = get(),
            dispatcher = get(DispatcherQualifier.IO),
        )
    }

    factory { AnimeService(httpClient = get<HttpClient>()) }
}
