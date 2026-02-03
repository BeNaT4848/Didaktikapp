package com.example.errenteriaapp.i18n

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

/**
 * Aplikazioaren hizkuntza kudeaketa sinplea.
 *
 * - Oinarria: gaztelera ("es")
 * - Gehigarriak: euskara ("eu"), ingelesa ("en")
 */
object LanguageManager {
    private const val PREFS = "settings"
    private const val KEY_LANG = "app_lang" // "es", "eu", "en" edo "" (sistemarena)

    /**
     * Gordetako hizkuntza-etiketa lortzen du
     * @param context Aplikazioaren testuingurua
     * @return Gordetako hizkuntza-etiketa ("es", "eu", "en") edo "es" lehenetsia
     */
    fun getSavedLanguageTag(context: Context): String {
        // Oinarria: gaztelera
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_LANG, "es")
            ?: "es"
    }

    /**
     * Gordetako hizkuntza aplikatzen du
     * @param context Aplikazioaren testuingurua
     */
    fun applySavedLanguage(context: Context) {
        applyLanguageTag(getSavedLanguageTag(context))
    }

    /**
     * Hizkuntza gordetzen eta aplikatzen du
     * @param context Aplikazioaren testuingurua
     * @param languageTag Gordetzeko hizkuntza-etiketa ("es", "eu", "en")
     */
    fun saveAndApply(context: Context, languageTag: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANG, languageTag)
            .apply()

        applyLanguageTag(languageTag)
        updateResources(context, languageTag)
    }

    /**
     * Testuingurua hizkuntza gordetarekin biltzen du
     * @param base Oinarrizko testuingurua
     * @return Hizkuntza egokitua duen testuinguru berria
     */
    fun wrapContext(base: Context): Context {
        val tag = getSavedLanguageTag(base)
        return wrapContext(base, tag)
    }

    /**
     * Testuingurua zehaztutako hizkuntza-arekin biltzen du
     * @param base Oinarrizko testuingurua
     * @param languageTag Erabiliko den hizkuntza-etiketa
     * @return Hizkuntza zehaztua duen testuinguru berria
     */
    fun wrapContext(base: Context, languageTag: String): Context {
        if (languageTag.isBlank()) return base
        val locale = Locale.forLanguageTag(languageTag)
        Locale.setDefault(locale)

        val config = Configuration(base.resources.configuration)
        config.setLocales(LocaleList(locale))
        return base.createConfigurationContext(config)
    }

    /**
     * Hizkuntza-etiketa aplikatzen du AppCompat-ren bidez
     * @param languageTag Aplikatzeko hizkuntza-etiketa
     */
    private fun applyLanguageTag(languageTag: String) {
        val locales = when (languageTag) {
            "es" -> LocaleListCompat.forLanguageTags("es")
            "eu" -> LocaleListCompat.forLanguageTags("eu")
            "en" -> LocaleListCompat.forLanguageTags("en")
            else -> LocaleListCompat.getEmptyLocaleList()
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }

    /**
     * Baliabideak eguneratzen ditu zehaztutako hizkuntzarako
     * @param context Aplikazioaren testuingurua
     * @param languageTag Eguneratzeko hizkuntza-etiketa
     */
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