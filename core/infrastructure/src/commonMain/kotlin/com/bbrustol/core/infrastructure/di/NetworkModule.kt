package com.bbrustol.core.infrastructure.di

import com.bbrustol.core.infrastructure.network.NetworkConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single { provideHttpClient(get()) }
}

fun provideHttpClient(config: NetworkConfig): HttpClient =
    HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
                explicitNulls = false
            })
        }

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = config.baseUrl
                header("Content-Type", "application/json")
                header(HttpHeaders.Authorization, "Bearer ${config.apiToken}")
            }
        }

        install(Logging) {
            level = if (config.isDebug) LogLevel.ALL else LogLevel.INFO
            logger = Logger.DEFAULT
            sanitizeHeader { headerName -> headerName == HttpHeaders.Authorization }
        }

        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 30_000
        }
    }
