package com.example.qrguard

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.qrguard.data.preferences.AppPreferences
import com.example.qrguard.data.preferences.AppTheme
import com.example.qrguard.ui.navigation.QrGuardNavHost
import com.example.qrguard.ui.theme.QRGuardTheme
import com.example.qrguard.util.LanguageHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var preferences: AppPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        preferences = AppPreferences(this)
        
        // Dil ayarını uygula
        lifecycleScope.launch {
            val language = preferences.language.first()
            LanguageHelper.setAppLanguage(this@MainActivity, language)
        }
        
        enableEdgeToEdge()
        
        setContent {
            // Lifecycle-aware state collection for better performance and safety.
            val theme by preferences.theme.collectAsStateWithLifecycle(initialValue = AppTheme.SYSTEM)
            
            val isDarkTheme = when (theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                // Use system setting instead of forcing dark theme.
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }
            
            QRGuardTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    QrGuardNavHost()
                }
            }
        }
    }
}
