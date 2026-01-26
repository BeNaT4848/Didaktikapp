package com.example.errenteriaapp.progress

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.errenteriaapp.navigation.Routes

/**
 * Contrato (simple):
 * - Gestiona el desbloqueo secuencial de ubicaciones (kokapenak) y persiste el progreso.
 * - Cada "paso" es un minijuego (route) y al completarlo desbloquea el siguiente.
 * - Soporta pasos dobles: 2 minijuegos consecutivos, pero solo 1 marcador.
 */
class KokapenaProgressRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Orden de desbloqueo por pasos (routes)
     *  1) Bertso
     *  2) Arramendi iturria: SanMarkos + Crucigrama (doble)
     *  3) Ordenatu
     *  4) Papresa
     *  5) Herriko Plaza: TaulaArrastrar + Sopa letra (doble)
     */
    val tourSteps: List<String> = listOf(
        Routes.BERTSOJOLASA_SCREEN,

        // Arramendi iturria (doble)
        Routes.SANMARKOS_SCREEN,
        Routes.CRUCIGRAMA_SCREEN,

        Routes.ORDENATUJOLASA_SCREEN,
        Routes.BASURA_SCREEN,

        // Herriko Plaza (doble)
        Routes.TAULAARRASTRAR_SCRENN,
        Routes.SOPALETRA_SCREEN,
    )

    /** Índice del paso actual desbloqueado. Todo lo anterior se considera completado. */
    fun getUnlockedStepIndex(): Int = prefs.getInt(KEY_UNLOCKED_STEP_INDEX, 0)

    fun setUnlockedStepIndex(index: Int) {
        prefs.edit { putInt(KEY_UNLOCKED_STEP_INDEX, index.coerceIn(0, tourSteps.lastIndex.coerceAtLeast(0))) }
    }

    fun reset() {
        prefs.edit { remove(KEY_UNLOCKED_STEP_INDEX) }
    }

    /**
     * Devuelve true si esta ruta está desbloqueada (puedes abrir su modal/juego).
     */
    fun isRouteUnlocked(route: String?): Boolean {
        if (route.isNullOrBlank()) return false
        val idx = tourSteps.indexOf(route)
        if (idx == -1) return true // si no lo controlamos, no lo bloqueamos
        return idx <= getUnlockedStepIndex()
    }

    /**
     * Marca una ruta como completada.
     * - Si coincide con el paso desbloqueado actual, avanza al siguiente.
     * - Si es una ruta anterior, no hace nada.
     */
    fun markCompleted(route: String?) {
        if (route.isNullOrBlank()) return
        val idx = tourSteps.indexOf(route)
        if (idx == -1) return

        val unlocked = getUnlockedStepIndex()
        if (idx == unlocked) {
            // desbloquea el siguiente paso
            val next = (unlocked + 1).coerceAtMost(tourSteps.size - 1)
            setUnlockedStepIndex(next)
        }
    }

    companion object {
        private const val PREFS_NAME = "kokapena_progress"
        private const val KEY_UNLOCKED_STEP_INDEX = "unlocked_step_index"
    }
}
