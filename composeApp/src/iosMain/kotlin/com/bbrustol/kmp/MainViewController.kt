package com.bbrustol.kmp

import androidx.compose.ui.window.ComposeUIViewController
import com.bbrustol.core.infrastructure.di.coroutinesDispatchersModule
import com.bbrustol.core.infrastructure.di.networkModule
import com.bbrustol.core.infrastructure.di.platformModule
import com.bbrustol.core.infrastructure.network.NetworkConfig
import com.bbrustol.feature.animelist.di.animeModule
import org.koin.compose.KoinApplication
import org.koin.dsl.module

fun MainViewController() = ComposeUIViewController {
    KoinApplication(application = {
        modules(
            module { single { NetworkConfig(baseUrl = "api.jikan.moe", apiToken = "", isDebug = false) } },
            networkModule,
            coroutinesDispatchersModule,
            platformModule,
            animeModule,
        )
    }) {
        App()
    }
}
