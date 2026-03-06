package com.bbrustol.kmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform