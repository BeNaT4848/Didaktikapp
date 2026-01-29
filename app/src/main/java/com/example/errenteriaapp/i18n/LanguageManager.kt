package com.example.errenteriaapp.i18n

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

/**
 * Gestión simple del idioma de la app.
 *
 * - Base: castellano ("es")
 * - Extras: euskara ("eu"), inglés ("en")
 */
object LanguageManager {
    private const val PREFS = "settings"
    private const val KEY_LANG = "app_lang" // "es", "eu", "en" o "" (sistema)

    fun getSavedLanguageTag(context: Context): String {
        // Base: castellano
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LANG, "es")
            ?: "es"
    }

    fun applySavedLanguage(context: Context) {
        applyLanguageTag(getSavedLanguageTag(context))
    }

    fun saveAndApply(context: Context, languageTag: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANG, languageTag)
            .apply()

        applyLanguageTag(languageTag)
        updateResources(context, languageTag)
    }

    fun wrapContext(base: Context): Context {
        val tag = getSavedLanguageTag(base)
        return wrapContext(base, tag)
    }

    fun wrapContext(base: Context, languageTag: String): Context {
        if (languageTag.isBlank()) return base
        val locale = Locale.forLanguageTag(languageTag)
        Locale.setDefault(locale)

        val config = Configuration(base.resources.configuration)
        config.setLocales(LocaleList(locale))
        return base.createConfigurationContext(config)
    }

    private fun applyLanguageTag(languageTag: String) {
        val locales = when (languageTag) {
            "es" -> LocaleListCompat.forLanguageTags("es")
            "eu" -> LocaleListCompat.forLanguageTags("eu")
            "en" -> LocaleListCompat.forLanguageTags("en")
            else -> LocaleListCompat.getEmptyLocaleList()
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }

    private fun updateResources(context: Context, languageTag: String) {
        if (languageTag.isBlank()) return
        val locale = Locale.forLanguageTag(languageTag)
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        config.setLocales(LocaleList(locale))
        res.updateConfiguration(config, res.displayMetrics)
    }
}
