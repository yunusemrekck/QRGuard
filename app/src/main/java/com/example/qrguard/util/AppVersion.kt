package com.example.qrguard.util

import com.example.qrguard.BuildConfig

object AppVersion {
    val versionName: String = BuildConfig.VERSION_NAME
    val versionCode: Int = BuildConfig.VERSION_CODE
    
    fun getFullVersion(): String = "$versionName ($versionCode)"
}
