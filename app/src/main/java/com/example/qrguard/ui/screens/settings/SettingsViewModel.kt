package com.example.qrguard.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrguard.data.preferences.AppLanguage
import com.example.qrguard.data.preferences.AppPreferences
import com.example.qrguard.data.preferences.AppTheme
import com.example.qrguard.util.LanguageHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val preferences = AppPreferences(application)
    
    val language: StateFlow<AppLanguage> = preferences.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.SYSTEM)
    
    val theme: StateFlow<AppTheme> = preferences.theme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.SYSTEM)
    
    val vibrationEnabled: StateFlow<Boolean> = preferences.vibrationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    
    val soundEnabled: StateFlow<Boolean> = preferences.soundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    
    val autoCopyEnabled: StateFlow<Boolean> = preferences.autoCopyEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            preferences.setLanguage(language)
            LanguageHelper.setAppLanguage(getApplication(), language)
        }
    }
    
    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferences.setTheme(theme)
        }
    }
    
    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setVibrationEnabled(enabled)
        }
    }
    
    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setSoundEnabled(enabled)
        }
    }
    
    fun setAutoCopyEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setAutoCopyEnabled(enabled)
        }
    }
}
