package com.example.qrguard.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppPreferences(private val context: Context) {

    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val VIBRATION_KEY = booleanPreferencesKey("vibration")
        private val SOUND_KEY = booleanPreferencesKey("sound")
        private val AUTO_COPY_KEY = booleanPreferencesKey("auto_copy")
        private val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch")
    }

    val language: Flow<AppLanguage> = context.dataStore.data.map { preferences ->
        when (preferences[LANGUAGE_KEY]) {
            "tr" -> AppLanguage.TURKISH
            "en" -> AppLanguage.ENGLISH
            else -> AppLanguage.SYSTEM
        }
    }

    val vibrationEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[VIBRATION_KEY] ?: true
    }

    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SOUND_KEY] ?: true
    }

    val autoCopyEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_COPY_KEY] ?: false
    }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH_KEY] ?: true
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = when (language) {
                AppLanguage.TURKISH -> "tr"
                AppLanguage.ENGLISH -> "en"
                AppLanguage.SYSTEM -> "system"
            }
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VIBRATION_KEY] = enabled
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_KEY] = enabled
        }
    }

    suspend fun setAutoCopyEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_COPY_KEY] = enabled
        }
    }

    suspend fun setFirstLaunchCompleted() {
        context.dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH_KEY] = false
        }
    }
}

enum class AppLanguage {
    SYSTEM, TURKISH, ENGLISH
}