package com.example.errenteriaapp.ai

import android.app.Application
import android.content.Context

/**
 * Aplikazioaren testuingura eskuratzeko objektu sinplea.
 * ViewModel-en saioaren hobespenak irakurtzeko erabil daiteke.
 *
 * @see [android.app.Application]
 * @see [android.content.Context]
 * @since 1.0
 */
// Aplikazioaren testuingura eskuratzeko objektu sinplea
// ViewModel-en saioaren hobespenak irakurtzeko erabilgarria
object AppContextHolder {
    /**
     * Aplikazioaren testuingurua gordetzeko aldagai bolatila.
     *
     * @property appContext Aplikazioaren testuingurua edo null
     */
    @Volatile
    var appContext: Context? = null
        private set // Kanpoko aldaketak ekiditeko

    /**
     * AppContextHolder hasieratzen du aplikazioaren testuinguruarekin.
     *
     * @param application Aplikazioaren instantzia
     * @throws IllegalStateException Aplikazioa null bada
     *
     * @see [android.app.Application.applicationContext]
     */
    // AppContextHolder hasieratzen du aplikazioaren testuinguruarekin
    fun init(application: Application) {
        appContext = application.applicationContext
    }
}