package com.bbrustol.core.infrastructure.di

import com.bbrustol.core.infrastructure.network.IOSNetworkChecker
import com.bbrustol.core.infrastructure.network.NetworkChecker
import org.koin.dsl.module

actual val platformModule = module {
    single<NetworkChecker> { IOSNetworkChecker() }
}
