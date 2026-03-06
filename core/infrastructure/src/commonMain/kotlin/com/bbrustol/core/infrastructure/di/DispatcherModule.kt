package com.bbrustol.core.infrastructure.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.module

object DispatcherQualifier {
    val Default = StringQualifier("DefaultDispatcher")
    val IO = StringQualifier("IoDispatcher")
    val Main = StringQualifier("MainDispatcher")
    val MainImmediate = StringQualifier("MainImmediateDispatcher")
    val Unconfined = StringQualifier("Unconfined")
}

val coroutinesDispatchersModule = module {
    single(qualifier = DispatcherQualifier.Default) { Dispatchers.Default }
    single(qualifier = DispatcherQualifier.IO) { Dispatchers.IO }
    single(qualifier = DispatcherQualifier.Main) { Dispatchers.Main }
    single(qualifier = DispatcherQualifier.MainImmediate) { Dispatchers.Main.immediate }
    single(qualifier = DispatcherQualifier.Unconfined) { Dispatchers.Unconfined }
}
