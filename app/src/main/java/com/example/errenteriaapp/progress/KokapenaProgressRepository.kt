package com.example.errenteriaapp.progress

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.errenteriaapp.navigation.Routes

/**
 * Kokapenen aurrerapena kudeatzen duen errepositorioa.
 * Kokapenak (kokapenak) sekuentzialki desblokeatzea eta aurrerapena gordetzea.
 * "Pauso" bakoitza minijoko bat da (route) eta amaitzean hurrengoa desblokeatzen da.
 * Pauso bikoitzak onartzen ditu: 2 minijoko jarraian, baina markagailu bakarra.
 *
 * OHARRA: Aurrerapena erabiltzaile bakoitzeko da.
 *
 * @see Routes
 */
class KokapenaProgressRepository(
    context: Context,
    private val userId: String = DEFAULT_USER_ID
) {

    // Hobespen partekatuak erabiltzailearen aurrerapenentzat
    private val prefs: SharedPreferences =
        context.getSharedPreferences(prefsNameFor(userId), Context.MODE_PRIVATE)

    /** Desblokeo ordena pausoka (routes)
     *  1) Bertso
     *  2) Arramendi iturria: SanMarkos + Crucigrama (bikoitza)
     *  3) Ordenatu
     *  4) Papresa
     *  5) Herriko Plaza: TaulaArrastrar + Sopa letra (bikoitza)
     */
    val tourSteps: List<String> = listOf(
        Routes.BERTSOJOLASA_SCREEN,
        Routes.BERTSOJOLASA2_SCREEN,

        // Arramendi iturria (bikoitza)
        Routes.SANMARKOS_SCREEN,
        Routes.CRUCIGRAMA_SCREEN,

        Routes.ORDENATUJOLASA_SCREEN,
        Routes.BASURA_SCREEN,

        // Herriko Plaza (bikoitza)
        Routes.TAULAARRASTRAR_SCRENN,
        Routes.SOPALETRA_SCREEN,
    )

    /** Uneko pauso desblokeatuaren indizea. Aurreko guztiak amaitutzat jotzen dira. */
    fun getUnlockedStepIndex(): Int = prefs.getInt(keyUnlockedStepIndex(userId), 0)

    /** Desblokeatutako pausoaren indizea ezarri. */
    fun setUnlockedStepIndex(index: Int) {
        prefs.edit {
            putInt(
                keyUnlockedStepIndex(userId),
                index.coerceIn(0, tourSteps.lastIndex.coerceAtLeast(0))
            )
        }
    }

    /** Aurrerapena berrabiarazi (0 posiziora itzuli). */
    fun reset() {
        prefs.edit { remove(keyUnlockedStepIndex(userId)) }
    }

    /**
     * Bide hau desblokeatuta dagoen egiaztatu (bere modala/jokoa ireki daiteke).
     *
     * @param route Egiaztatu nahi den bidea
     * @return true bidea desblokeatuta badago
     */
    fun isRouteUnlocked(route: String?): Boolean {
        if (route.isNullOrBlank()) return false
        val idx = tourSteps.indexOf(route)
        if (idx == -1) return true // kudeatzen ez badugu, ez dugu blokeatzen
        return idx <= getUnlockedStepIndex()
    }

    /**
     * Bide hau dagoeneko amaituta dagoen egiaztatu.
     * (Hau da, uneko desblokeatutako pausoaren aurretik dago).
     *
     * @param route Egiaztatu nahi den bidea
     * @return true bidea amaituta badago
     */
    fun isRouteCompleted(route: String?): Boolean {
        if (route.isNullOrBlank()) return false
        val idx = tourSteps.indexOf(route)
        if (idx == -1) return false
        return idx < getUnlockedStepIndex()
    }

    /**
     * Bide hau unekoa den egiaztatu (oraintxe egin behar dena).
     *
     * @param route Egiaztatu nahi den bidea
     * @return true bidea unekoa bada
     */
    fun isRouteCurrent(route: String?): Boolean {
        if (route.isNullOrBlank()) return false
        val idx = tourSteps.indexOf(route)
        if (idx == -1) return false
        return idx == getUnlockedStepIndex()
    }

    /**
     * Bide bat amaitutzat markatu.
     * - Uneko desblokeatutako pausoarekin bat badator, hurrengora aurreratu.
     * - Aurreko bidea bada, ez du ezer egiten.
     *
     * @param route Amaitutzat markatu nahi den bidea
     */
    fun markCompleted(route: String?) {
        if (route.isNullOrBlank()) return
        val idx = tourSteps.indexOf(route)
        if (idx == -1) return

        val unlocked = getUnlockedStepIndex()
        if (idx == unlocked) {
            // Hurrengo pausoa desblokeatu
            val next = (unlocked + 1).coerceAtMost(tourSteps.size - 1)
            setUnlockedStepIndex(next)
        }
    }

    /**
     * Bide hau unekoa edo bigarren mailakoa den egiaztatu (Bertso 2 kasurako).
     *
     * @param route Egiaztatu nahi den bidea
     * @return true bidea unekoa edo bigarren mailakoa bada
     */
    fun isRouteCurrentOrSecondary(route: String?): Boolean {
        if (route.isNullOrBlank()) return false
        if (route == Routes.BERTSOJOLASA_SCREEN) {
            return isRouteCurrent(Routes.BERTSOJOLASA_SCREEN) || isRouteCurrent(Routes.BERTSOJOLASA2_SCREEN)
        }
        if (route == Routes.SANMARKOS_SCREEN) {
            return isRouteCurrent(Routes.SANMARKOS_SCREEN) || isRouteCurrent(Routes.CRUCIGRAMA_SCREEN)
        }
        return isRouteCurrent(route)
    }

    /** Laguntzaile objektua konstante eta funtzio estatikoentzat. */
    companion object {
        private const val PREFS_NAME_BASE = "kokapena_progress"
        private const val DEFAULT_USER_ID = "default"
        private const val KEY_UNLOCKED_STEP_INDEX_BASE = "unlocked_step_index"

        /** Erabiltzaile IDa garbitu karaktere baliogabeak kenduz. */
        private fun sanitize(id: String): String =
            id.trim().ifBlank { DEFAULT_USER_ID }.replace(Regex("[^a-zA-Z0-9._-]"), "_")

        /** Hobespen partekatuen izena lortu erabiltzaile IDrako. */
        private fun prefsNameFor(userId: String): String =
            "${PREFS_NAME_BASE}_${sanitize(userId)}"

        /** Desblokeatutako pausoaren indizearen gakoa lortu erabiltzaile IDrako. */
        private fun keyUnlockedStepIndex(userId: String): String =
            "${KEY_UNLOCKED_STEP_INDEX_BASE}_${sanitize(userId)}"
    }
}