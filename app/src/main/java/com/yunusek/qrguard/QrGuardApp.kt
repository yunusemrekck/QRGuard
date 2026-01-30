package com.yunusek.qrguard

import android.app.Application
import com.yunusek.qrguard.data.local.QrGuardDatabase

class QrGuardApp : Application() {
    
    val database: QrGuardDatabase by lazy {
        QrGuardDatabase.getInstance(this)
    }
}
