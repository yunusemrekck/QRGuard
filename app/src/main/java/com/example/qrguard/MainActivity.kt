package com.example.qrguard

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.qrguard.data.preferences.AppPreferences
import com.example.qrguard.ui.navigation.QrGuardNavHost
import com.example.qrguard.ui.theme.QRGuardTheme
import com.example.qrguard.util.LanguageHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        preferences = AppPreferences(this)

        // Dil ayarını uygula
        lifecycleScope.launch {
            val language = preferences.language.first()
            LanguageHelper.setAppLanguage(this@MainActivity, language)
        }

        enableEdgeToEdge()

        setContent {
            // Always use dark theme
            QRGuardTheme(darkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    QrGuardNavHost()
                }
            }
        }
    }
}