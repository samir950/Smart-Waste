package com.qbrain.smartwaste

import android.app.Application
import com.qbrain.smartwaste.utils.SharedPrefsManager

class SmartWasteApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize SharedPrefsManager
        SharedPrefsManager.init(this)
    }
}