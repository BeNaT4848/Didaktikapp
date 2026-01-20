package com.example.errenteriaapp.database.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PuzzlePiece(
    val id: Int,
    val bitmap: ImageBitmap,
    val correctSlot: Int,      // Slot correcto (0-8)
    var currentSlot: Int? = null, // Slot actual (null si no está colocado)
    var offsetX: Float = 0f,   // Offset X para posición libre
    var offsetY: Float = 0f    // Offset Y para posición libre
)

class PuzzleViewModel : ViewModel() {

    // Configuración 3x3
    private val rows = 3
    private val cols = 3
    val totalPieces = rows * cols

    // Piezas del puzzle
    private val _pieces = mutableStateListOf<PuzzlePiece>()
    val pieces: List<PuzzlePiece> get() = _pieces

    // Slots (qué pieza hay en cada slot)
    private val _slots = mutableStateListOf<Int?>().apply {
        repeat(totalPieces) { add(null) }
    }
    val slots: List<Int?> get() = _slots

    // Estados
    val isLoading = mutableStateOf(true)

    // Imagen completa para mostrar al final
    val fullPuzzleImageRes: Int = R.drawable.papresa_azalpena

    // Contador de piezas correctas
    val correctCount: Int
        get() = _slots.countIndexed { index, pieceId ->
            pieceId != null && getPieceById(pieceId).correctSlot == index
        }

    // ¿Está el puzzle completo?
    val isPuzzleComplete: Boolean get() = correctCount == totalPieces

    // Inicializar puzzle
    fun initializePuzzle(context: Context) {
        isLoading.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    // Cargar y dividir imagen
                    val options = BitmapFactory.Options().apply {
                        inScaled = false
                    }

                    val originalBitmap = BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.papresa_azalpena,
                        options
                    ) ?: throw Exception("No se pudo cargar la imagen")

                    val piecesList = divideImage(originalBitmap)

                    withContext(Dispatchers.Main) {
                        _pieces.clear()
                        _pieces.addAll(piecesList)
                        _slots.clear()
                        repeat(totalPieces) { _slots.add(null) }
                        placePiecesRandomly(context)
                        isLoading.value = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    isLoading.value = false
                }
            }
        }
    }

    private fun divideImage(bitmap: Bitmap): List<PuzzlePiece> {
        val pieces = mutableListOf<PuzzlePiece>()
        val pieceWidth = bitmap.width / cols
        val pieceHeight = bitmap.height / rows

        var pieceId = 0
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val x = col * pieceWidth
                val y = row * pieceHeight

                val pieceBitmap = Bitmap.createBitmap(
                    bitmap, x, y, pieceWidth, pieceHeight
                ).asImageBitmap()

                pieces.add(
                    PuzzlePiece(
                        id = pieceId,
                        bitmap = pieceBitmap,
                        correctSlot = row * cols + col
                    )
                )
                pieceId++
            }
        }
        return pieces
    }

    private fun placePiecesRandomly(context: Context) {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels.toFloat()

        // Área inferior para piezas
        val areaStartY = displayMetrics.heightPixels.toFloat() - 500f

        _pieces.forEachIndexed { index, piece ->
            // Distribuir en 3 columnas en la parte inferior
            val col = index % 3
            val row = index / 3

            piece.offsetX = 30f + col * 110f
            piece.offsetY = areaStartY + row * 110f
            piece.currentSlot = null
        }
    }

    // Obtener pieza por ID
    fun getPieceById(pieceId: Int): PuzzlePiece {
        return _pieces.find { it.id == pieceId } ?: throw Exception("Pieza no encontrada")
    }

    // Colocar pieza en slot
    fun placePieceInSlot(pieceId: Int, slotIndex: Int): Boolean {
        val piece = getPieceById(pieceId)

        // Si el slot ya tiene una pieza, intercambiar
        val existingPieceId = _slots[slotIndex]

        if (existingPieceId != null && existingPieceId != pieceId) {
            // Intercambiar
            val existingPiece = getPieceById(existingPieceId)
            val existingPieceIndex = _pieces.indexOfFirst { it.id == existingPieceId }

            // Mover pieza existente a la posición de la nueva
            _pieces[existingPieceIndex] = existingPiece.copy(
                currentSlot = piece.currentSlot,
                offsetX = piece.offsetX,
                offsetY = piece.offsetY
            )

            // Actualizar slot de la pieza existente
            if (piece.currentSlot != null) {
                _slots[piece.currentSlot!!] = existingPieceId
            }
        } else if (piece.currentSlot != null) {
            // Liberar el slot anterior
            _slots[piece.currentSlot!!] = null
        }

        // Colocar nueva pieza en slot
        val pieceIndex = _pieces.indexOfFirst { it.id == pieceId }
        _pieces[pieceIndex] = piece.copy(
            currentSlot = slotIndex,
            offsetX = 0f,
            offsetY = 0f
        )

        _slots[slotIndex] = pieceId

        return true
    }

    // Quitar pieza de slot (para arrastrar)
    fun removePieceFromSlot(pieceId: Int) {
        val piece = getPieceById(pieceId)
        if (piece.currentSlot != null) {
            _slots[piece.currentSlot!!] = null

            val pieceIndex = _pieces.indexOfFirst { it.id == pieceId }
            _pieces[pieceIndex] = piece.copy(
                currentSlot = null
            )
        }
    }

    fun updatePiecePosition(pieceId: Int, offsetX: Float, offsetY: Float) {
        val piece = getPieceById(pieceId)
        if (piece.currentSlot == null) {
            val pieceIndex = _pieces.indexOfFirst { it.id == pieceId }
            _pieces[pieceIndex] = piece.copy(
                offsetX = offsetX,
                offsetY = offsetY
            )
        }
    }

    // Extensión para contar con índice
    inline fun <T> List<T?>.countIndexed(predicate: (index: Int, T?) -> Boolean): Int {
        var count = 0
        for (i in indices) {
            if (predicate(i, this[i])) count++
        }
        return count
    }
}