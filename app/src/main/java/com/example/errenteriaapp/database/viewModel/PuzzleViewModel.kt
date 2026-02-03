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

/**
 * Puzzle-pieza baten datuak gordetzeko klasea
 * @property id Pieza-identifikatzaile bakarra
 * @property bitmap Piezaren irudia
 * @property correctSlot Piezaren zokalo zuzena (0-8)
 * @property currentSlot Piezaren uneko zokaloa (null zokaloan ez badago)
 * @property offsetX X ardatzean desplazamendua posizio librean
 * @property offsetY Y ardatzean desplazamendua posizio librean
 * @property isVisible Pieza ikusgai dagoen ala ez
 * @property aspectRatio Irudiaren aspektu-erlazioa (zabalera/altuera)
 */
data class PuzzlePiece(
    val id: Int,
    val bitmap: ImageBitmap,
    val correctSlot: Int,      // Zokalo zuzena (0-8)
    var currentSlot: Int? = null, // Uneko zokaloa (null zokaloan ez badago)
    var offsetX: Float = 0f,   // X desplazamendua posizio librerako
    var offsetY: Float = 0f,   // Y desplazamendua posizio librerako
    var isVisible: Boolean = false,
    val aspectRatio: Float = 1f
)

/**
 * ViewModela erabili puzzle-jokoaren egoera kudeatzeko
 * @see ViewModel
 */
class PuzzleViewModel : ViewModel() {

    /**
     * Puzzlearen konfigurazioa 3x3
     */
    private val rows = 3
    private val cols = 3

    /**
     * Pieza guztien kopurua
     */
    val totalPieces = rows * cols

    /**
     * Puzzlearen piezen zerrenda
     */
    private val _pieces = mutableStateListOf<PuzzlePiece>()
    val pieces: List<PuzzlePiece> get() = _pieces

    /**
     * Zokaloak (pieza bat dagoen zokalo bakoitzean)
     */
    private val _slots = mutableStateListOf<Int?>().apply {
        repeat(totalPieces) { add(null) }
    }
    val slots: List<Int?> get() = _slots

    /**
     * Datuak kargatzen ari diren egoera kontrolatzeko
     */
    val isLoading = mutableStateOf(true)

    /**
     * Erakusteko hurrengo pieza-izendatzailea
     */
    var nextPieceIndex by mutableStateOf(0)
        private set

    /**
     * Puzzle osoko irudia amaieran erakusteko
     */
    val fullPuzzleImageRes: Int = R.drawable.papresa_azalpena

    /**
     * Zokalo zuzenetan dauden piezen kopurua kalkulatzen du
     * @return Zokalo zuzenetan dauden piezen kopurua
     */
    val correctCount: Int
        get() = _slots.countIndexed { index, pieceId ->
            pieceId != null && getPieceById(pieceId).correctSlot == index
        }

    /**
     * Puzzlea osatuta dagoen egiaztatzen du
     * @return Puzzlea osatuta dagoen ala ez
     */
    val isPuzzleComplete: Boolean get() = correctCount == totalPieces




    /**
     * Puzzlea hasieratzen du
     * @param context Aplikazioaren testuingurua
     */
    fun initializePuzzle(context: Context) {
        isLoading.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    // Irudia kargatu eta zatitu
                    val options = BitmapFactory.Options().apply {
                        inScaled = false
                    }

                    val originalBitmap = BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.papresa_azalpena,
                        options
                    ) ?: throw Exception("Ezin izan da irudia kargatu")

                    val piecesList = divideImage(originalBitmap)

                    withContext(Dispatchers.Main) {
                        _pieces.clear()
                        _pieces.addAll(piecesList)
                        _slots.clear()
                        repeat(totalPieces) { _slots.add(null) }
                        nextPieceIndex = 0

                        // Lehen pieza erakutsi bakarrik
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

    /**
     * Irudi bat zatitzen du pieza txikietan
     * @param bitmap Zatitu beharreko irudi nagusia
     * @return Puzzle-piezen zerrenda
     */
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
                        isVisible = false,
                        aspectRatio = pieceWidth.toFloat() / pieceHeight.toFloat()
                    )
                )
                pieceId++
            }
        }
        return pieces.shuffled()  // Piezak nahastu
    }

    /**
     * Hurrengo pieza erabilgarria erakusten du
     * @param context Aplikazioaren testuingurua
     */
    private fun showNextPiece(context: Context) {
        if (nextPieceIndex < _pieces.size) {
            // Hurrengo pieza ikusgai jarri
            val pieceIndex = nextPieceIndex
            _pieces[pieceIndex] = _pieces[pieceIndex].copy(isVisible = true)

            // Beheko aldean kokatu (erdian bakarrik)
            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels.toFloat()
            val screenHeight = displayMetrics.heightPixels.toFloat()

            val pieceSize = 100f
            val posX = (screenWidth - pieceSize) / 3  // Erdian
            val posY = screenHeight - 420f  // Beheko ertzaren apur bat gorago

            _pieces[pieceIndex] = _pieces[pieceIndex].copy(
                offsetX = posX,
                offsetY = posY,
                currentSlot = null
            )

            nextPieceIndex++
        }
    }

    /**
     * Pieza lortzen du bere identifikatzailearen arabera
     * @param pieceId Bilatu beharreko pieza-identifikatzailea
     * @return Aurkitu den pieza
     * @throws Exception Pieza ez bada aurkitzen
     */
    fun getPieceById(pieceId: Int): PuzzlePiece {
        return _pieces.find { it.id == pieceId } ?: throw Exception("Pieza ez da aurkitu")
    }



    /**
     * Pieza bat zokalo batean jartzen du (trukaketa laguntzarekin)
     * @param pieceId Kokatu beharreko pieza-identifikatzailea
     * @param slotIndex Helburu zokaloaren indizea
     * @return Pieza kokatu den ala ez
     */
    fun placePieceInSlot(pieceId: Int, slotIndex: Int): Boolean {
        val piece = getPieceById(pieceId)
        val originSlot = piece.currentSlot
        val existingPieceId = _slots[slotIndex]

        if (existingPieceId != null) {
            return if (originSlot != null) {
                movePieceBetweenSlots(originSlot, slotIndex)
            } else {
                false
            }
        }

        if (originSlot != null) {
            _slots[originSlot] = null
        }

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

    /**
     * Pieza bat zokalo batetik bestera mugitzen du
     * @param sourceSlot Iturburu zokaloaren indizea
     * @param targetSlot Helburu zokaloaren indizea
     * @return Mugimendua arrakastaz egin den ala ez
     */
    fun movePieceBetweenSlots(sourceSlot: Int, targetSlot: Int): Boolean {
        if (sourceSlot == targetSlot) return true
        val movingPieceId = _slots[sourceSlot] ?: return false
        val targetPieceId = _slots[targetSlot]

        _slots[targetSlot] = movingPieceId
        _slots[sourceSlot] = targetPieceId

        val movingIndex = _pieces.indexOfFirst { it.id == movingPieceId }
        if (movingIndex != -1) {
            val movingPiece = _pieces[movingIndex]
            _pieces[movingIndex] = movingPiece.copy(currentSlot = targetSlot)
        }

        if (targetPieceId != null) {
            val targetIndex = _pieces.indexOfFirst { it.id == targetPieceId }
            if (targetIndex != -1) {
                val targetPiece = _pieces[targetIndex]
                _pieces[targetIndex] = targetPiece.copy(currentSlot = sourceSlot)
            }
        }

        return true
    }




    /**
     * Pieza bat kokatu ondoren deitzen da, hurrengo pieza erakusteko
     * @param context Aplikazioaren testuingurua
     */
    fun onPiecePlaced(context: Context) {
        // Zenbat pieza daude kokatuak zenbatzen du
        val placedPieces = _pieces.count { it.currentSlot != null }

        // Hurrengo pieza erakutsi soilik kokapen berria bada (ez trukaketa)
        // Eta erakusteko pieza gehiago badago
        if (nextPieceIndex < totalPieces) {
            showNextPiece(context)
        }
    }



    /**
     * Indizearekin zenbatzen duen luzapena
     * @param predicate Zenbatuko den elementua zehazten duen baldintza
     * @return Baldintza betetzen duten elementu kopurua
     */
    inline fun <T> List<T?>.countIndexed(predicate: (index: Int, T?) -> Boolean): Int {
        var count = 0
        for (i in indices) {
            if (predicate(i, this[i])) count++
        }
        return count
    }
}