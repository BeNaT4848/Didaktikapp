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

class OrdenatuJolasaViewModel(private val puntuazioaDao: PuntuazioaDao?) : ViewModel() {
    // Mapeo foto -> número correcto
    val photoNumberMap = mapOf(
        R.drawable.errota_prozesua_1 to 1,
        R.drawable.errota_prozesua_2 to 2,
        R.drawable.errota_prozesua_3 to 3,
        R.drawable.errota_prozesua_4 to 4,
        R.drawable.errota_prozesua_5 to 5,
        R.drawable.errota_prozesua_6 to 6
    )

    // Lista de fotos (puede venir barajada o definida manualmente)
    var photos by mutableStateOf(photoNumberMap.keys.toList())
        private set

    // Asignaciones de slots: índice -> resourceId o null
    val slotAssignments = mutableStateListOf<Int?>()

    // Dialogs y estados globales del juego
    var showSuccessDialog by mutableStateOf(false)
        private set
    var showWrongDialog by mutableStateOf(false)
        private set

    // Añade esta variable para el nombre del usuario
    var currentUserName: String? = null

    init {
        initGame(shuffle = true)
    }

    fun initGame(shuffle: Boolean = true) {
        photos = if (shuffle) photoNumberMap.keys.toList().shuffled() else photoNumberMap.keys.toList()
        slotAssignments.clear()
        repeat(photos.size) { slotAssignments.add(null) }
        showSuccessDialog = false
        showWrongDialog = false
    }

    // Asignar una foto a un slot (desde drag desde panel de fotos)
    fun assignPhotoToSlot(photoRes: Int, targetIndex: Int) {
        if (targetIndex !in slotAssignments.indices) return

        // Si la foto ya estaba en otro slot, limpiar ese slot
        val previousSlot = slotAssignments.indexOf(photoRes)
        if (previousSlot != -1 && previousSlot != targetIndex) {
            slotAssignments[previousSlot] = null
        }

        // Si en target hay una foto, se queda (o puedes hacer swap según lógica)
        slotAssignments[targetIndex] = photoRes
        checkCompletion()
    }

    // Swap entre dos slots (drag desde slot a slot)
    fun swapSlots(sourceIndex: Int, targetIndex: Int) {
        if (sourceIndex !in slotAssignments.indices || targetIndex !in slotAssignments.indices) return
        if (sourceIndex == targetIndex) return
        val temp = slotAssignments[sourceIndex]
        slotAssignments[sourceIndex] = slotAssignments[targetIndex]
        slotAssignments[targetIndex] = temp
        checkCompletion()
    }

    private val correctCount: Int
        get() = slotAssignments.withIndex().count { (slotIndex, photoRes) ->
            photoRes != null && photoNumberMap[photoRes] == slotIndex + 1
        }

    private val isComplete: Boolean
        get() = slotAssignments.all { it != null }

    private fun checkCompletion() {
        if (!isComplete) return
        verificarCompletado()
    }

    fun resetGame() {
        initGame(shuffle = true)
    }

    fun dismissDialogs() {
        showSuccessDialog = false
        showWrongDialog = false
    }

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
                            puntuazioaErrotaProzezua = 0,
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

    // Función que verifica si el juego está completado
    private fun verificarCompletado() {
        val puntosCorrectos = calcularPuntos()
        if (puntosCorrectos >= 3) { // Según tu mensaje "gutxienez 3 ondo"
            // Guardar puntos cuando el juego se completa exitosamente
            guardarPuntuacion(puntosCorrectos)
            showSuccessDialog = true
        } else {
            showWrongDialog = true
        }
    }

    private fun calcularPuntos(): Int {
        // Lógica para calcular cuántas fotos están en la posición correcta
        var puntos = 0
        slotAssignments.forEachIndexed { index, photoRes ->
            if (photoRes != null && photoNumberMap[photoRes] == index + 1) {
                puntos++
            }
        }
        return puntos
    }

    // Método para establecer el usuario
    fun setUsuario(nombre: String) {
        currentUserName = nombre
    }
}