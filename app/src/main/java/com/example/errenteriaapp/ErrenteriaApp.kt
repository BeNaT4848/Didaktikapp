package com.example.errenteriaapp

import android.app.Application
import com.example.errenteriaapp.ai.AppContextHolder

class ErrenteriaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContextHolder.init(this)
    }
}

