package com.example.qrguard

import android.app.Application
import com.example.qrguard.data.local.QrGuardDatabase

class QrGuardApp : Application() {
    
    val database: QrGuardDatabase by lazy {
        QrGuardDatabase.getInstance(this)
    }
}
