package com.bbrustol.core.infrastructure.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_BLUETOOTH
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI

class AndroidNetworkChecker(private val context: Context) : NetworkChecker {
    override fun isNetworkAvailable(): Boolean = context.isNetworkAvailable()
}

@SuppressLint("MissingPermission")
private fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        actNw.hasTransport(TRANSPORT_WIFI) -> true
        actNw.hasTransport(TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(TRANSPORT_ETHERNET) -> true
        actNw.hasTransport(TRANSPORT_BLUETOOTH) -> true
        else -> false
    }
}
