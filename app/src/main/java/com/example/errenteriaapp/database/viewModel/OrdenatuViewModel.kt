package com.example.errenteriaapp.database.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.R
import com.example.errenteriaapp.database.Puntuazioa
import com.example.errenteriaapp.database.PuntuazioaDao
import kotlinx.coroutines.launch

/**
 * ViewModela erabili irudiak ordenatzeko jokoaren egoera kudeatzeko
 * @see ViewModel
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 */
class OrdenatuJolasaViewModel(private val puntuazioaDao: PuntuazioaDao?) : ViewModel() {

    /**
     * Irudien eta haien zenbaki zuzenen arteko mapeoa
     * @property key Irudiaren baliabide-identifikadorea (R.drawable.*)
     * @property value Irudiaren zenbaki zuzena (1-6)
     */
    val photoNumberMap = mapOf(
        R.drawable.errota_prozesua_1 to 1,
        R.drawable.errota_prozesua_2 to 2,
        R.drawable.errota_prozesua_3 to 3,
        R.drawable.errota_prozesua_4 to 4,
        R.drawable.errota_prozesua_5 to 5,
        R.drawable.errota_prozesua_6 to 6
    )

    /**
     * Jokoan erabiliko diren irudien zerrenda
     */
    var photos by mutableStateOf(photoNumberMap.keys.toList())
        private set

    /**
     * Zokaloetan (slot) esleitutako irudiak: indizea -> baliabide-identifikadorea edo null
     */
    val slotAssignments = mutableStateListOf<Int?>()

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
     * Oraingo erabiltzailearen izena gordetzeko
     */
    var currentUserName: String? = null

    init {
        initGame(shuffle = true)
    }

    /**
     * Jokoa hasieratzen du
     * @param shuffle Irudiak nahastu behar diren ala ez (lehenetsia: bai)
     */
    fun initGame(shuffle: Boolean = true) {
        photos = if (shuffle) photoNumberMap.keys.toList().shuffled() else photoNumberMap.keys.toList()
        slotAssignments.clear()
        repeat(photos.size) { slotAssignments.add(null) }
        showSuccessDialog = false
        showWrongDialog = false
    }

    /**
     * Irudi bat zokalo batera esleitzen du (irudien paneletik arrastratuz)
     * @param photoRes Esleitu beharreko irudiaren baliabide-identifikadorea
     * @param targetIndex Helburu zokaloaren indizea
     */
    fun assignPhotoToSlot(photoRes: Int, targetIndex: Int) {
        if (targetIndex !in slotAssignments.indices) return

        // Irudia beste zokalo batean bazegoen, zokalo hori garbitzen du
        val previousSlot = slotAssignments.indexOf(photoRes)
        if (previousSlot != -1 && previousSlot != targetIndex) {
            slotAssignments[previousSlot] = null
        }

        // Helburu zokaloan irudi bat badago, geratzen da (edo trukatzea erabaki dezakezu)
        slotAssignments[targetIndex] = photoRes
        checkCompletion()
    }

    /**
     * Bi zokaloen arteko trukea egiten du (zokalotik zokaloara arrastratuz)
     * @param sourceIndex Iturburu zokaloaren indizea
     * @param targetIndex Helburu zokaloaren indizea
     */
    fun swapSlots(sourceIndex: Int, targetIndex: Int) {
        if (sourceIndex !in slotAssignments.indices || targetIndex !in slotAssignments.indices) return
        if (sourceIndex == targetIndex) return
        val temp = slotAssignments[sourceIndex]
        slotAssignments[sourceIndex] = slotAssignments[targetIndex]
        slotAssignments[targetIndex] = temp
        checkCompletion()
    }

    /**
     * Zuzen esleitutako irudien kopurua kalkulatzen du
     * @return Zuzen esleitutako irudien kopurua
     */
    private val correctCount: Int
        get() = slotAssignments.withIndex().count { (slotIndex, photoRes) ->
            photoRes != null && photoNumberMap[photoRes] == slotIndex + 1
        }

    /**
     * Jokoa osatuta dagoen egiaztatzen du (zokalo guztiak beteta daude)
     * @return Jokoa osatuta dagoen ala ez
     */
    private val isComplete: Boolean
        get() = slotAssignments.all { it != null }

    /**
     * Jokoaren osaketa egiaztatzen du
     */
    private fun checkCompletion() {
        if (!isComplete) return
        verificarCompletado()
    }

    /**
     * Jokoa berrabiarazten du
     */
    fun resetGame() {
        initGame(shuffle = true)
    }

    /**
     * Elkarrizketa guztiak ixteko
     */
    fun dismissDialogs() {
        showSuccessDialog = false
        showWrongDialog = false
    }

    /**
     * Puntuazioa datu-basean gordetzen du
     * @param puntos Gorde beharreko puntu kopurua
     */
    fun guardarPuntuacion(puntos: Int) {
        viewModelScope.launch {
            currentUserName?.let { nombreUsuario ->
                puntuazioaDao?.let { dao ->
                    val puntuazioActual = dao.getByName(nombreUsuario)

                    if (puntuazioActual != null) {
                        val nuevaPuntuazio = puntuazioActual.copy(
                            puntuazioaArrastrar = puntuazioActual.puntuazioaArrastrar + puntos
                        )
                        dao.insert(nuevaPuntuazio)
                    } else {
                        val nuevaPuntuazio = Puntuazioa(
                            izenaAbizena = nombreUsuario,
                            puntuazioaBertso = 0,
                            puntuazioaGalderak = 0,
                            puntuazioaGurutzegrama = 0,
                            puntuazioaArropaBuruHandiak = 0,
                            puntuazioaPapresa = 0,
                            puntuazioaArrastrar = puntos,
                            puntuazioaSopaLetra = 0
                        )
                        dao.insert(nuevaPuntuazio)
                    }
                }
            }
        }
    }

    /**
     * Jokoa osatuta dagoen egiaztatzen du eta puntuak kalkulatzen ditu
     */
    private fun verificarCompletado() {
        val puntosCorrectos = calcularPuntos()
        if (puntosCorrectos >= 3) { // Zure mezuan "gutxienez 3 ondo" adierazten duzun bezala
            // Puntuazioa gordetzen du jokoa arrakastaz osatzen denean
            guardarPuntuacion(puntosCorrectos)
            showSuccessDialog = true
        } else {
            showWrongDialog = true
        }
    }

    /**
     * Puntu kopurua kalkulatzen du
     * @return Zuzen esleitutako irudien kopurua
     */
    private fun calcularPuntos(): Int {
        var puntos = 0
        slotAssignments.forEachIndexed { index, photoRes ->
            if (photoRes != null && photoNumberMap[photoRes] == index + 1) {
                puntos++
            }
        }
        return puntos
    }

    /**
     * Erabiltzailea ezartzen du
     * @param nombre Erabiltzailearen izena
     */
    fun setUsuario(nombre: String) {
        currentUserName = nombre
    }
}