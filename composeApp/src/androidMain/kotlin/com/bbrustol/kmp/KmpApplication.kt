package com.bbrustol.kmp

import android.app.Application
import com.bbrustol.core.infrastructure.di.coroutinesDispatchersModule
import com.bbrustol.core.infrastructure.di.networkModule
import com.bbrustol.core.infrastructure.di.platformModule
import com.bbrustol.core.infrastructure.network.NetworkConfig
import com.bbrustol.feature.animelist.di.animeModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class KmpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KmpApplication)
            modules(
                module { single { NetworkConfig(baseUrl = "api.jikan.moe", apiToken = "", isDebug = BuildConfig.DEBUG) } },
                networkModule,
                coroutinesDispatchersModule,
                platformModule,
                animeModule,
            )
        }
    }
}
