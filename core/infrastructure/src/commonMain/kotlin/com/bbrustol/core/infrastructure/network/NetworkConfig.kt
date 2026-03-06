package com.bbrustol.core.infrastructure.network

data class NetworkConfig(
    val baseUrl: String,
    val apiToken: String,
    val isDebug: Boolean = false,
)
