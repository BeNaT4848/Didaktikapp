// app/src/main/java/com/example/errenteriaapp/database/viewModel/PapresaViewModel.kt
package com.example.errenteriaapp.database.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.R
import com.example.errenteriaapp.classes.WasteCategory
import com.example.errenteriaapp.classes.WasteItem
import com.example.errenteriaapp.database.Puntuazioa
import com.example.errenteriaapp.database.PuntuazioaDao
import kotlinx.coroutines.launch
import kotlin.math.ceil

/**
 * ViewModela erabili hondakinen sailkapen-jokoaren (Papresa) egoera kudeatzeko
 * @see ViewModel
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 * @param configJuego Jokoaren konfigurazioa, ConfigJuego.DEFAULT_PAPRESA balioa erabiltzen du berez
 */
class PapresaViewModel(
    private val puntuazioaDao: PuntuazioaDao?,
    private val configJuego: ConfigJuego = ConfigJuego.DEFAULT_PAPRESA
) : ViewModel() {

    /**
     * Jokoaren konfigurazioaren datu-klasea
     * @property successThreshold Arrakastarako gutxieneko proportzioa (0.0-1.0)
     * @property minCorrectosRequeridos Onargarriak diren erantzun kopuru minimoa
     * @property puntosPorCorrecto Erantzun zuzen bakoitzeko puntuak
     * @property puntosExtraPerfecto Partida perfektuarentzako puntu gehigarriak
     */
    data class ConfigJuego(
        val successThreshold: Double = 0.8,
        val minCorrectosRequeridos: Int = 12,
        val puntosPorCorrecto: Int = 1,
        val puntosExtraPerfecto: Int = 10
    ) {
        companion object {
            /**
             * Papresa jokoarentzako konfigurazio lehenetsia
             */
            val DEFAULT_PAPRESA = ConfigJuego(
                successThreshold = 0.8,
                minCorrectosRequeridos = 12,
                puntosPorCorrecto = 1,
                puntosExtraPerfecto = 5
            )
        }
    }

    /**
     * Oraingo erabiltzailearen izena gordetzeko
     */
    var currentUserName: String? = null

    /**
     * Jokoan erabiliko diren hondakin-elementuen zerrenda
     */
    val wasteItems = mutableStateListOf<WasteItem>()

    /**
     * Unean erakusten ari den elementuaren indizea
     */
    var currentIndex by mutableStateOf(0)
        private set

    /**
     * Erabiltzailearen erantzunak: elementu-id -> hautatutako kategoria
     */
    val userAnswers = mutableMapOf<Int, WasteCategory>()

    /**
     * Emaitzen bistaratzea kontrolatzeko
     */
    var showResults by mutableStateOf(false)
        private set

    /**
     * Arrakasta elkarrizketaren bistaratzea kontrolatzeko
     */
    var showSuccessDialog by mutableStateOf(false)
        private set

    /**
     * Errore elkarrizketaren bistaratzea kontrolatzeko
     */
    var showWrongDialog by mutableStateOf(false)
        private set

    /**
     * Jokoa gainditu duen ala ez
     */
    var hasPassed by mutableStateOf(false)
        private set

    /**
     * Lortutako puntuazioa
     */
    var score: Int? by mutableStateOf(null)
        private set

    /**
     * Elementu guztiei erantzun zaien egiaztatzen du
     * @return Elementu guztiei erantzun zaien ala ez
     */
    val allAnswered: Boolean
        get() = wasteItems.all { userAnswers.containsKey(it.id) }

    /**
     * Erantzundako elementu kopurua kalkulatzen du
     * @return Erantzundako elementu kopurua
     */
    val answeredCount: Int
        get() = userAnswers.size

    /**
     * Elementu guztien kopurua kalkulatzen du
     * @return Elementu guztien kopurua
     */
    val totalCount: Int
        get() = wasteItems.size

    /**
     * Unean erakusten ari den elementua lortzen du
     * @return Uneko elementua edo null
     */
    val currentItem: WasteItem?
        get() = wasteItems.getOrNull(currentIndex)

    init {
        generarOrdenAleatorio()
    }

    /**
     * Elementuen ordena aleatorioa sortzen du
     */
    private fun generarOrdenAleatorio() {
        val itemsOriginales = listOf(
            WasteItem(1, R.string.papresa_item_water_bottle, WasteCategory.YELLOW, R.drawable.botella_agua),
            WasteItem(2, R.string.papresa_item_soda_can, WasteCategory.YELLOW, R.drawable.lata_refresco),
            WasteItem(3, R.string.papresa_item_chips_bag, WasteCategory.YELLOW, R.drawable.bolsa_patatas),
            WasteItem(4, R.string.papresa_item_plastic_cap, WasteCategory.YELLOW, R.drawable.tapones_plastico),
            WasteItem(5, R.string.papresa_item_yogurt, WasteCategory.YELLOW, R.drawable.yogurt),
            WasteItem(6, R.string.papresa_item_cereal_box, WasteCategory.BLUE, R.drawable.caja_cereales),
            WasteItem(7, R.string.papresa_item_newspaper, WasteCategory.BLUE, R.drawable.periodico),
            WasteItem(8, R.string.papresa_item_notebook, WasteCategory.BLUE, R.drawable.cuaderno_papel),
            WasteItem(9, R.string.papresa_item_cardboard_tube, WasteCategory.BLUE, R.drawable.tubo_carton),
            WasteItem(10, R.string.papresa_item_envelope, WasteCategory.BLUE, R.drawable.sobre),
            WasteItem(11, R.string.papresa_item_fruit_peel, WasteCategory.BROWN, R.drawable.piel_fruta),
            WasteItem(12, R.string.papresa_item_veg_scraps, WasteCategory.BROWN, R.drawable.restos_verduras),
            WasteItem(13, R.string.papresa_item_bread, WasteCategory.BROWN, R.drawable.pan),
            WasteItem(14, R.string.papresa_item_bones, WasteCategory.BROWN, R.drawable.huesos),
            WasteItem(15, R.string.papresa_item_food_scraps, WasteCategory.BROWN, R.drawable.sobras),
            WasteItem(16, R.string.papresa_item_gum, WasteCategory.BLACK, R.drawable.chicle),
            WasteItem(17, R.string.papresa_item_cigarette_butts, WasteCategory.BLACK, R.drawable.colillas),
            WasteItem(18, R.string.papresa_item_pad, WasteCategory.BLACK, R.drawable.gorro_sanitario),
            WasteItem(19, R.string.papresa_item_bandages, WasteCategory.BLACK, R.drawable.tiritas),
            WasteItem(20, R.string.papresa_item_diaper, WasteCategory.BLACK, R.drawable.panal)
        )

        wasteItems.clear()
        wasteItems.addAll(itemsOriginales.shuffled())
    }

    /**
     * Edukiontzi bat klikatzerakoan deitzen da
     * @param category Hautatutako hondakin-kategoria
     */
    fun onContainerClick(category: WasteCategory) {
        currentItem?.let {
            userAnswers[it.id] = category
            currentIndex = (currentIndex + 1) % wasteItems.size
        }
    }

    /**
     * Aurreko botoia klikatzerakoan deitzen da
     */
    fun onPreviousClick() {
        currentIndex = (currentIndex - 1 + wasteItems.size) % wasteItems.size
    }

    /**
     * Hurrengo botoia klikatzerakoan deitzen da
     */
    fun onNextClick() {
        currentIndex = (currentIndex + 1) % wasteItems.size
    }

    /**
     * Egiaztatu botoia klikatzerakoan deitzen da
     */
    fun onVerifyClick() {
        if (!allAnswered) return

        val correctAnswers = calcularPuntos()
        val requiredCorrect = configJuego.minCorrectosRequeridos

        score = correctAnswers
        hasPassed = correctAnswers >= requiredCorrect
        showResults = true

        if (hasPassed) {
            // Puntuazioa datu-basean gordetzen du
            guardarPuntuacion(correctAnswers)
        } else {
            showWrongDialog = true
        }
    }

    /**
     * Erantzun zuzenak zenbatzen ditu
     * @return Erantzun zuzen kopurua
     */
    private fun calcularPuntos(): Int {
        return wasteItems.count { userAnswers[it.id] == it.correctCategory }
    }

    /**
     * Puntuazio finala kalkulatzen du
     * @param correctos Erantzun zuzen kopurua
     * @return Puntuazio totala
     */
    private fun calcularPuntuacionFinal(correctos: Int): Int {
        var puntos = correctos * configJuego.puntosPorCorrecto
        // Bonus erantzun perfektuarentzat (guztiak zuzenak)
        if (correctos == wasteItems.size && configJuego.puntosExtraPerfecto > 0) {
            puntos += configJuego.puntosExtraPerfecto
        }
        return puntos
    }

    /**
     * Puntuazioa datu-basean gordetzen du
     * @param correctos Erantzun zuzen kopurua
     */
    fun guardarPuntuacion(correctos: Int) {
        viewModelScope.launch {
            currentUserName?.let { nombreUsuario ->
                puntuazioaDao?.let { dao ->
                    val puntuazioActual = dao.getByName(nombreUsuario)
                    val puntosFinales = calcularPuntuacionFinal(correctos)

                    if (puntuazioActual != null) {
                        val nuevaPuntuazio = puntuazioActual.copy(
                            puntuazioaPapresa = puntosFinales
                        )
                        dao.insert(nuevaPuntuazio)
                    } else {
                        val nuevaPuntuazio = Puntuazioa(
                            izenaAbizena = nombreUsuario,
                            puntuazioaBertso = 0,
                            puntuazioaGalderak = 0,
                            puntuazioaGurutzegrama = 0,
                            puntuazioaArropaBuruHandiak = 0,
                            puntuazioaPapresa = puntosFinales,
                            puntuazioaArrastrar = 0,
                            puntuazioaSopaLetra = 0
                        )
                        dao.insert(nuevaPuntuazio)
                    }
                }
            }
        }
    }

    /**
     * Bideoa ikusi ondoren deitzen da
     */
    fun onVideoWatched() {
        showSuccessDialog = true
    }

    /**
     * Errore elkarrizketaren "Saiatu berriro" botoia klikatzerakoan deitzen da
     */
    fun onWrongDialogRetry() {
        resetGame()
    }

    /**
     * Jokoa berrabiarazten du
     */
    fun resetGame() {
        generarOrdenAleatorio()
        currentIndex = 0
        userAnswers.clear()
        showResults = false
        showSuccessDialog = false
        showWrongDialog = false
        hasPassed = false
        score = null
    }

    /**
     * Arrakasta elkarrizketa ixteko
     */
    fun dismissSuccessDialog() {
        showSuccessDialog = false
    }

    /**
     * Erabiltzailea ezartzen du
     * @param nombre Erabiltzailearen izena
     */
    fun setUsuario(nombre: String) {
        currentUserName = nombre
    }
}