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
 *
 * IMPORTANTE: el progreso es por-usuario.
 */
class KokapenaProgressRepository(
    context: Context,
    private val userId: String = DEFAULT_USER_ID
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(prefsNameFor(userId), Context.MODE_PRIVATE)

    /** Orden de desbloqueo por pasos (routes)
     *  1) Bertso
     *  2) Arramendi iturria: SanMarkos + Crucigrama (doble)
     *  3) Ordenatu
     *  4) Papresa
     *  5) Herriko Plaza: TaulaArrastrar + Sopa letra (doble)
     */
    val tourSteps: List<String> = listOf(
        Routes.BERTSOJOLASA_SCREEN,
        Routes.BERTSOJOLASA2_SCREEN,

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
    fun getUnlockedStepIndex(): Int = prefs.getInt(keyUnlockedStepIndex(userId), 0)

    fun setUnlockedStepIndex(index: Int) {
        prefs.edit {
            putInt(
                keyUnlockedStepIndex(userId),
                index.coerceIn(0, tourSteps.lastIndex.coerceAtLeast(0))
            )
        }
    }

    fun reset() {
        prefs.edit { remove(keyUnlockedStepIndex(userId)) }
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

    /** True si la ruta ya está completada (es decir, va antes del paso actual desbloqueado). */
    fun isRouteCompleted(route: String?): Boolean {
        if (route.isNullOrBlank()) return false
        val idx = tourSteps.indexOf(route)
        if (idx == -1) return false
        return idx < getUnlockedStepIndex()
    }

    /** True si la ruta es la actual (la que toca hacer ahora mismo). */
    fun isRouteCurrent(route: String?): Boolean {
        if (route.isNullOrBlank()) return false
        val idx = tourSteps.indexOf(route)
        if (idx == -1) return false
        return idx == getUnlockedStepIndex()
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

    /** True si la ruta es la actual o secundaria (Bertso 2). */
    fun isRouteCurrentOrSecondary(route: String?): Boolean {
        if (route.isNullOrBlank()) return false
        if (route == Routes.BERTSOJOLASA_SCREEN) {
            return isRouteCurrent(Routes.BERTSOJOLASA_SCREEN) || isRouteCurrent(Routes.BERTSOJOLASA2_SCREEN)
        }
        return isRouteCurrent(route)
    }

    companion object {
        private const val PREFS_NAME_BASE = "kokapena_progress"
        private const val DEFAULT_USER_ID = "default"
        private const val KEY_UNLOCKED_STEP_INDEX_BASE = "unlocked_step_index"

        private fun sanitize(id: String): String =
            id.trim().ifBlank { DEFAULT_USER_ID }.replace(Regex("[^a-zA-Z0-9._-]"), "_")

        private fun prefsNameFor(userId: String): String =
            "${PREFS_NAME_BASE}_${sanitize(userId)}"

        private fun keyUnlockedStepIndex(userId: String): String =
            "${KEY_UNLOCKED_STEP_INDEX_BASE}_${sanitize(userId)}"
    }
}
