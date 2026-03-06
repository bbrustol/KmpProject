package com.bbrustol.core.infrastructure.network

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.SystemConfiguration.SCNetworkReachabilityCreateWithName
import platform.SystemConfiguration.SCNetworkReachabilityFlagsVar
import platform.SystemConfiguration.SCNetworkReachabilityGetFlags
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsConnectionRequired
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsReachable

@OptIn(ExperimentalForeignApi::class)
class IOSNetworkChecker : NetworkChecker {
    override fun isNetworkAvailable(): Boolean {
        val reachability = SCNetworkReachabilityCreateWithName(null, "8.8.8.8") ?: return false
        return memScoped {
            val flags = alloc<SCNetworkReachabilityFlagsVar>()
            val gotFlags = SCNetworkReachabilityGetFlags(reachability, flags.ptr)
            if (gotFlags) {
                val isReachable = flags.value and kSCNetworkReachabilityFlagsReachable != 0u
                val needsConnection = flags.value and kSCNetworkReachabilityFlagsConnectionRequired != 0u
                isReachable && !needsConnection
            } else {
                false
            }
        }
    }
}
