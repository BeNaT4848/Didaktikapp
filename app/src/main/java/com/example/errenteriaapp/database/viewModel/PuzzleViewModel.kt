package com.example.errenteriaapp.database.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max

data class PuzzlePiece(
    val id: Int,
    val bitmap: ImageBitmap,
    val correctSlot: Int,      // Slot correcto (0-8)
    var currentSlot: Int? = null, // Slot actual (null si no está colocado)
    var offsetX: Float = 0f,   // Offset X para posición libre
    var offsetY: Float = 0f,   // Offset Y para posición libre
    var isVisible: Boolean = false  // Nueva: si la pieza es visible abajo
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

    // Nueva: índice de la siguiente pieza a mostrar
    var nextPieceIndex by mutableStateOf(0)
        private set

    // Imagen completa para mostrar al final
    val fullPuzzleImageRes: Int = R.drawable.papresa_azalpena

    // Contador de piezas correctas
    val correctCount: Int
        get() = _slots.countIndexed { index, pieceId ->
            pieceId != null && getPieceById(pieceId).correctSlot == index
        }

    // ¿Está el puzzle completo?
    val isPuzzleComplete: Boolean get() = correctCount == totalPieces

    // Variable para trackear qué pieza se está arrastrando
    var draggingPieceId by mutableStateOf<Int?>(null)

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
                        nextPieceIndex = 0

                        // Solo mostrar la primera pieza
                        showNextPiece(context)

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
                        correctSlot = row * cols + col,
                        isVisible = false  // Inicialmente ninguna es visible
                    )
                )
                pieceId++
            }
        }
        return pieces.shuffled()  // Mezclar las piezas
    }

    // Mostrar la siguiente pieza disponible
    private fun showNextPiece(context: Context) {
        if (nextPieceIndex < _pieces.size) {
            // Hacer visible la siguiente pieza
            val pieceIndex = nextPieceIndex
            _pieces[pieceIndex] = _pieces[pieceIndex].copy(isVisible = true)

            // Posicionarla en la parte inferior (sola en el centro)
            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels.toFloat()
            val screenHeight = displayMetrics.heightPixels.toFloat()

            val pieceSize = 100f
            val posX = (screenWidth - pieceSize) / 3  // Centrada
            val posY = screenHeight - 420f  // Un poco arriba del borde inferior

            _pieces[pieceIndex] = _pieces[pieceIndex].copy(
                offsetX = posX,
                offsetY = posY,
                currentSlot = null
            )

            nextPieceIndex++
        }
    }

    // Obtener pieza por ID
    fun getPieceById(pieceId: Int): PuzzlePiece {
        return _pieces.find { it.id == pieceId } ?: throw Exception("Pieza no encontrada")
    }

    // Método para intercambiar piezas
    fun swapPieces(pieceId1: Int, pieceId2: Int): Boolean {
        val piece1 = getPieceById(pieceId1)
        val piece2 = getPieceById(pieceId2)

        if (piece1.currentSlot == null || piece2.currentSlot == null) {
            return false
        }

        val slot1 = piece1.currentSlot!!
        val slot2 = piece2.currentSlot!!

        // Intercambiar en los slots
        _slots[slot1] = pieceId2
        _slots[slot2] = pieceId1

        // Actualizar las piezas
        val piece1Index = _pieces.indexOfFirst { it.id == pieceId1 }
        val piece2Index = _pieces.indexOfFirst { it.id == pieceId2 }

        _pieces[piece1Index] = piece1.copy(currentSlot = slot2)
        _pieces[piece2Index] = piece2.copy(currentSlot = slot1)

        return true
    }

    // Colocar pieza en slot (con soporte para intercambio)
    fun placePieceInSlot(pieceId: Int, slotIndex: Int): Boolean {
        val piece = getPieceById(pieceId)

        // Si hay una pieza en el slot destino
        val existingPieceId = _slots[slotIndex]

        if (existingPieceId != null) {
            // Intercambiar las piezas
            return swapPieces(pieceId, existingPieceId)
        }

        // Si el slot está vacío...
        // Si la pieza ya estaba en otro slot, liberarlo
        if (piece.currentSlot != null) {
            _slots[piece.currentSlot!!] = null
        }

        // Colocar en nuevo slot
        val pieceIndex = _pieces.indexOfFirst { it.id == pieceId }
        _pieces[pieceIndex] = piece.copy(
            currentSlot = slotIndex,
            offsetX = 0f,
            offsetY = 0f,
            isVisible = true
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

    // Nueva: cuando se coloca una pieza, mostrar la siguiente
    fun onPiecePlaced(context: Context) {
        // Contar cuántas piezas están colocadas
        val placedPieces = _pieces.count { it.currentSlot != null }

        // Mostrar siguiente pieza solo si es una nueva colocación (no intercambio)
        // Y si hay más piezas por mostrar
        if (nextPieceIndex < totalPieces) {
            showNextPiece(context)
        }
    }

    // Obtener piezas visibles (las que están abajo)
    fun getVisiblePieces(): List<PuzzlePiece> {
        return _pieces.filter { it.isVisible && it.currentSlot == null }
    }

    // Método para obtener pieza en un slot
    fun getPieceInSlot(slotIndex: Int): PuzzlePiece? {
        return _slots[slotIndex]?.let { getPieceById(it) }
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