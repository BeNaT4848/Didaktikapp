package com.example.errenteriaapp.database.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.errenteriaapp.R

data class PuzzlePieceData(
    val id: Int,
    val imageRes: Int,
    val correctPosition: Int // 0-5 para grid 3x2
)

class PuzzleViewModel : ViewModel() {
    // Datos de las piezas del puzzle
    val pieces = listOf(
        PuzzlePieceData(id = 1, imageRes = R.drawable.pieza1, correctPosition = 0),
        PuzzlePieceData(id = 2, imageRes = R.drawable.pieza2, correctPosition = 1),
        PuzzlePieceData(id = 3, imageRes = R.drawable.pieza3, correctPosition = 2),
        PuzzlePieceData(id = 4, imageRes = R.drawable.pieza4, correctPosition = 3),
        PuzzlePieceData(id = 5, imageRes = R.drawable.pieza5, correctPosition = 4),
        PuzzlePieceData(id = 6, imageRes = R.drawable.pieza6, correctPosition = 5)
    )

    // Asignaciones de slots: índice -> resourceId o null
    val slots = mutableStateListOf<Int?>()

    // Dialogs y estados globales del juego
    var showSuccessDialog by mutableStateOf(false)
        private set
    var showWrongDialog by mutableStateOf(false)
        private set

    init {
        initGame()
    }

    fun initGame(shuffle: Boolean = true) {
        slots.clear()
        repeat(pieces.size) { slots.add(null) }
        showSuccessDialog = false
        showWrongDialog = false
    }

    // Asignar una pieza a un slot
    fun assignPieceToSlot(pieceRes: Int, targetIndex: Int) {
        if (targetIndex !in slots.indices) return

        // Si la pieza ya estaba en otro slot, limpiar ese slot
        val previousSlot = slots.indexOf(pieceRes)
        if (previousSlot != -1 && previousSlot != targetIndex) {
            slots[previousSlot] = null
        }

        // Asignar la pieza al slot objetivo
        slots[targetIndex] = pieceRes
        checkPuzzleCompletion()
    }

    // Swap entre dos slots
    fun swapSlots(sourceIndex: Int, targetIndex: Int) {
        if (sourceIndex !in slots.indices || targetIndex !in slots.indices) return
        if (sourceIndex == targetIndex) return
        val temp = slots[sourceIndex]
        slots[sourceIndex] = slots[targetIndex]
        slots[targetIndex] = temp
        checkPuzzleCompletion()
    }

    // Verificar si una pieza está en su posición correcta
    fun isPieceInCorrectSlot(pieceRes: Int, slotIndex: Int): Boolean {
        val piece = pieces.find { it.imageRes == pieceRes }
        return piece?.correctPosition == slotIndex
    }

    // Contador de piezas correctas
    val correctCount: Int
        get() = slots.withIndex().count { (slotIndex, pieceRes) ->
            pieceRes != null && isPieceInCorrectSlot(pieceRes, slotIndex)
        }

    // Verificar si el puzzle está completo (todas las piezas colocadas)
    val isPuzzleComplete: Boolean
        get() = slots.all { it != null }

    // Verificar la completitud del puzzle
    fun checkPuzzleCompletion() {
        if (!isPuzzleComplete) return

        // Para un puzzle, normalmente todas deben estar correctas
        if (correctCount == pieces.size) {
            showSuccessDialog = true
        } else {
            showWrongDialog = true
        }
    }

    // Resetear el juego
    fun resetGame() {
        initGame()
    }

    // Cerrar diálogos
    fun dismissDialogs() {
        showSuccessDialog = false
        showWrongDialog = false
    }
}