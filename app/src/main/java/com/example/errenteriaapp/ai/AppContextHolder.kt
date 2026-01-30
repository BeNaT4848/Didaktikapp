package com.example.errenteriaapp.ai

import android.app.Application
import android.content.Context

// Simple access to app context for reading session prefs in ViewModel.
object AppContextHolder {
    @Volatile
    var appContext: Context? = null
        private set

    fun init(application: Application) {
        appContext = application.applicationContext
    }
}

