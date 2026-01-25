package com.example.qrguard.util

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.qrguard.data.preferences.AppLanguage
import java.util.Locale

object LanguageHelper {
    
    fun setAppLanguage(context: Context, language: AppLanguage) {
        val localeList = when (language) {
            AppLanguage.TURKISH -> LocaleListCompat.forLanguageTags("tr")
            AppLanguage.ENGLISH -> LocaleListCompat.forLanguageTags("en")
            AppLanguage.SYSTEM -> LocaleListCompat.getEmptyLocaleList()
        }
        
        AppCompatDelegate.setApplicationLocales(localeList)
    }
    
    fun getCurrentLanguage(): AppLanguage {
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        
        if (currentLocales.isEmpty) {
            return AppLanguage.SYSTEM
        }
        
        val locale = currentLocales[0]
        return when (locale?.language) {
            "tr" -> AppLanguage.TURKISH
            "en" -> AppLanguage.ENGLISH
            else -> AppLanguage.SYSTEM
        }
    }
    
    fun getLanguageDisplayName(language: AppLanguage, context: Context): String {
        return when (language) {
            AppLanguage.TURKISH -> "Türkçe"
            AppLanguage.ENGLISH -> "English"
            AppLanguage.SYSTEM -> {
                val systemLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    context.resources.configuration.locales[0]
                } else {
                    @Suppress("DEPRECATION")
                    context.resources.configuration.locale
                }
                systemLocale.displayLanguage.replaceFirstChar { it.uppercase() }
            }
        }
    }
}
