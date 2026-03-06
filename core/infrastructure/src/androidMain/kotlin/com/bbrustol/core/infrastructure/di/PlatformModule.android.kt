package com.bbrustol.core.infrastructure.di

import com.bbrustol.core.infrastructure.network.AndroidNetworkChecker
import com.bbrustol.core.infrastructure.network.NetworkChecker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<NetworkChecker> { AndroidNetworkChecker(context = androidContext()) }
}
