package com.bbrustol.core.infrastructure.network

interface NetworkChecker {
    fun isNetworkAvailable(): Boolean
}
