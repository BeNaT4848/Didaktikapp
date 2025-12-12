package com.example.errenteriaapp.database.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.errenteriaapp.R

class OrdenatuJolasaViewModel : ViewModel() {
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
        if (correctCount >= 3) {
            showSuccessDialog = true
        } else {
            showWrongDialog = true
        }
    }

    fun resetGame() {
        initGame(shuffle = true)
    }

    fun dismissDialogs() {
        showSuccessDialog = false
        showWrongDialog = false
    }
}