package com.example.errenteriaapp.database.viewModel

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.classes.Character
import com.example.errenteriaapp.classes.DragGameUiState
import com.example.errenteriaapp.classes.GameWords
import com.example.errenteriaapp.database.Puntuazioa
import com.example.errenteriaapp.database.PuntuazioaDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ArropaBuruHandiakViewModel(
    private val puntuazioaDao: PuntuazioaDao?,
    private val configJuego: ConfigJuego = ConfigJuego.DEFAULT_DRAG
) : ViewModel() {

    data class ConfigJuego(
        val minCorrectosRequeridos: Int,
        val puntosPorPalabraCorrecta: Int = 2,
        val puntosExtraPerfecto: Int = 5,
        val puntosExtraAmbosPersonajes: Int = 2
    ) {
        companion object {
            val DEFAULT_DRAG = ConfigJuego(
                minCorrectosRequeridos = 6,  // 6 de 9 palabras (no 10 porque hay 1 extra)
                puntosPorPalabraCorrecta = 2,
                puntosExtraPerfecto = 5,
                puntosExtraAmbosPersonajes = 2
            )
        }
    }

    var currentUserName: String? = null

    // Estado inicial
    private val _uiState = MutableStateFlow(DragGameUiState())
    val uiState: StateFlow<DragGameUiState> = _uiState.asStateFlow()

    // Drop zones
    private val xantiDropZones = mutableListOf<Rect?>()
    private val maialenDropZones = mutableListOf<Rect?>()

    // Estadísticas del juego
    private var palabrasCorrectasXanti: Int = 0
    private var palabrasCorrectasMaialen: Int = 0
    private var puntuacionActual: Int = 0

    init {
        // Inicializar listas del tamaño correcto
        repeat(GameWords.XANTI_WORDS.size) { xantiDropZones.add(null) }
        repeat(GameWords.MAIALEN_WORDS.size) { maialenDropZones.add(null) }

        // Estado inicial
        resetGame()
    }

    // ===== FUNCIONES EXISTENTES =====

    fun updateXantiDropZone(index: Int, rect: Rect) {
        if (index < xantiDropZones.size) {
            xantiDropZones[index] = rect
        }
    }

    fun updateMaialenDropZone(index: Int, rect: Rect) {
        if (index < maialenDropZones.size) {
            maialenDropZones[index] = rect
        }
    }

    fun handleDrop(
        dropPoint: Offset,
        draggingWord: String?,
        draggingSlotCharacter: Character?,
        draggingSlotIndex: Int?,
        isDraggingFromSlot: Boolean
    ) {
        viewModelScope.launch {
            val word = draggingWord ?: return@launch

            // Buscar en zonas de Xanti
            xantiDropZones.forEachIndexed { index, rect ->
                if (rect?.contains(dropPoint) == true) {
                    updateAssignments(
                        targetCharacter = Character.XANTI,
                        targetIndex = index,
                        word = word,
                        draggingSlotCharacter = draggingSlotCharacter,
                        draggingSlotIndex = draggingSlotIndex,
                        isDraggingFromSlot = isDraggingFromSlot
                    )
                    return@launch
                }
            }

            // Buscar en zonas de Maialen
            maialenDropZones.forEachIndexed { index, rect ->
                if (rect?.contains(dropPoint) == true) {
                    updateAssignments(
                        targetCharacter = Character.MAIALEN,
                        targetIndex = index,
                        word = word,
                        draggingSlotCharacter = draggingSlotCharacter,
                        draggingSlotIndex = draggingSlotIndex,
                        isDraggingFromSlot = isDraggingFromSlot
                    )
                    return@launch
                }
            }
        }
    }

    private fun updateAssignments(
        targetCharacter: Character,
        targetIndex: Int,
        word: String,
        draggingSlotCharacter: Character?,
        draggingSlotIndex: Int?,
        isDraggingFromSlot: Boolean
    ) {
        _uiState.update { currentState ->
            when (targetCharacter) {
                Character.XANTI -> {
                    val currentAssignments = currentState.xantiAssignments.toMutableList()
                    val existingWord = currentAssignments[targetIndex]
                    currentAssignments[targetIndex] = word

                    var newState = currentState.copy(
                        xantiAssignments = currentAssignments
                    )

                    // Si veníamos de un slot, manejar intercambio
                    if (isDraggingFromSlot) {
                        newState = handleSlotExchange(
                            currentState = newState,
                            existingWord = existingWord,
                            draggingSlotCharacter = draggingSlotCharacter,
                            draggingSlotIndex = draggingSlotIndex
                        )
                    }
                    newState
                }
                Character.MAIALEN -> {
                    val currentAssignments = currentState.maialenAssignments.toMutableList()
                    val existingWord = currentAssignments[targetIndex]
                    currentAssignments[targetIndex] = word

                    var newState = currentState.copy(
                        maialenAssignments = currentAssignments
                    )

                    // Si veníamos de un slot, manejar intercambio
                    if (isDraggingFromSlot) {
                        newState = handleSlotExchange(
                            currentState = newState,
                            existingWord = existingWord,
                            draggingSlotCharacter = draggingSlotCharacter,
                            draggingSlotIndex = draggingSlotIndex
                        )
                    }
                    newState
                }
            }
        }
    }

    private fun handleSlotExchange(
        currentState: DragGameUiState,
        existingWord: String?,
        draggingSlotCharacter: Character?,
        draggingSlotIndex: Int?
    ): DragGameUiState {
        return when (draggingSlotCharacter) {
            Character.XANTI -> {
                draggingSlotIndex?.let { index ->
                    currentState.copy(
                        xantiAssignments = currentState.xantiAssignments.toMutableList().apply {
                            this[index] = existingWord
                        }
                    )
                } ?: currentState
            }
            Character.MAIALEN -> {
                draggingSlotIndex?.let { index ->
                    currentState.copy(
                        maialenAssignments = currentState.maialenAssignments.toMutableList().apply {
                            this[index] = existingWord
                        }
                    )
                } ?: currentState
            }
            null -> currentState
        }
    }

    // ===== FUNCIONES PARA PUNTOS - SIN ORDEN =====

    // Función para normalizar palabras (quitar espacios y poner en minúsculas)
    private fun normalizarPalabra(palabra: String): String {
        return palabra.trim().lowercase()
    }

    // Función para verificar respuestas SIN IMPORTAR EL ORDEN
    fun checkAnswers(): Pair<Boolean, Boolean> {
        val currentState = _uiState.value

        Log.d("DRAG_GAME", "=== VERIFICACIÓN INICIADA (SIN ORDEN) ===")
        Log.d("DRAG_GAME", "Xanti assignments: ${currentState.xantiAssignments}")
        Log.d("DRAG_GAME", "Maialen assignments: ${currentState.maialenAssignments}")

        // Normalizar todas las listas para comparación
        val palabrasXantiUsuario = currentState.xantiAssignments
            .filterNotNull()
            .map { normalizarPalabra(it) }
            .toSet()

        val palabrasMaialenUsuario = currentState.maialenAssignments
            .filterNotNull()
            .map { normalizarPalabra(it) }
            .toSet()

        val palabrasXantiEsperadas = GameWords.XANTI_WORDS
            .map { normalizarPalabra(it) }
            .toSet()

        val palabrasMaialenEsperadas = GameWords.MAIALEN_WORDS
            .map { normalizarPalabra(it) }
            .toSet()

        Log.d("DRAG_GAME", "Xanti usuario (normalizado): $palabrasXantiUsuario")
        Log.d("DRAG_GAME", "Xanti esperadas (normalizado): $palabrasXantiEsperadas")
        Log.d("DRAG_GAME", "Maialen usuario (normalizado): $palabrasMaialenUsuario")
        Log.d("DRAG_GAME", "Maialen esperadas (normalizado): $palabrasMaialenEsperadas")

        // Contar intersecciones (palabras correctas independientemente del orden)
        palabrasCorrectasXanti = palabrasXantiUsuario.intersect(palabrasXantiEsperadas).size
        palabrasCorrectasMaialen = palabrasMaialenUsuario.intersect(palabrasMaialenEsperadas).size

        // Verificar palabras incorrectas (de un personaje en el otro)
        val palabrasXantiEnMaialen = palabrasXantiUsuario.intersect(palabrasMaialenEsperadas).size
        val palabrasMaialenEnXanti = palabrasMaialenUsuario.intersect(palabrasXantiEsperadas).size

        val totalCorrectas = palabrasCorrectasXanti + palabrasCorrectasMaialen
        val totalPalabras = GameWords.XANTI_WORDS.size + GameWords.MAIALEN_WORDS.size

        Log.d("DRAG_GAME", "=== RESULTADOS ===")
        Log.d("DRAG_GAME", "Xanti correctas: $palabrasCorrectasXanti/${GameWords.XANTI_WORDS.size}")
        Log.d("DRAG_GAME", "Maialen correctas: $palabrasCorrectasMaialen/${GameWords.MAIALEN_WORDS.size}")
        Log.d("DRAG_GAME", "Palabras Xanti en Maialen (error): $palabrasXantiEnMaialen")
        Log.d("DRAG_GAME", "Palabras Maialen en Xanti (error): $palabrasMaialenEnXanti")
        Log.d("DRAG_GAME", "Total correctas: $totalCorrectas/$totalPalabras")
        Log.d("DRAG_GAME", "Mínimo requerido: ${configJuego.minCorrectosRequeridos}")

        // El juego es perfecto si TODAS las palabras están en su lugar correcto
        // y NO HAY palabras mezcladas entre personajes
        val esPerfecto = (palabrasCorrectasXanti == GameWords.XANTI_WORDS.size &&
                palabrasCorrectasMaialen == GameWords.MAIALEN_WORDS.size &&
                palabrasXantiEnMaialen == 0 &&
                palabrasMaialenEnXanti == 0)

        // Aprobado si tiene al menos el mínimo requerido de palabras correctas
        val haAprobado = totalCorrectas >= configJuego.minCorrectosRequeridos

        Log.d("DRAG_GAME", "Ha aprobado: $haAprobado")
        Log.d("DRAG_GAME", "Es perfecto: $esPerfecto")

        if (haAprobado) {
            puntuacionActual = calcularPuntuacion()
            Log.d("DRAG_GAME", "Puntuación calculada: $puntuacionActual")
            guardarPuntuacion()

            // Actualizar la puntuación en el estado
            _uiState.update { it.copy(puntuacion = puntuacionActual) }
        }

        Log.d("DRAG_GAME", "=== VERIFICACIÓN FINALIZADA ===")

        return Pair(haAprobado, esPerfecto)
    }

    // Calcular puntuación (actualizada para trabajar sin orden)
    private fun calcularPuntuacion(): Int {
        var puntos = 0

        // Puntos base por palabras correctas
        puntos += (palabrasCorrectasXanti + palabrasCorrectasMaialen) * configJuego.puntosPorPalabraCorrecta

        Log.d("DRAG_GAME", "Puntos base: ${(palabrasCorrectasXanti + palabrasCorrectasMaialen) * configJuego.puntosPorPalabraCorrecta}")

        // Bonus por completar todas las palabras de Xanti
        if (palabrasCorrectasXanti == GameWords.XANTI_WORDS.size) {
            puntos += configJuego.puntosExtraPerfecto / 2
            Log.d("DRAG_GAME", "Bonus Xanti completo: +${configJuego.puntosExtraPerfecto / 2}")
        }

        // Bonus por completar todas las palabras de Maialen
        if (palabrasCorrectasMaialen == GameWords.MAIALEN_WORDS.size) {
            puntos += configJuego.puntosExtraPerfecto / 2
            Log.d("DRAG_GAME", "Bonus Maialen completo: +${configJuego.puntosExtraPerfecto / 2}")
        }

        // Bonus por completar ambos personajes (sin palabras mezcladas)
        if (palabrasCorrectasXanti == GameWords.XANTI_WORDS.size &&
            palabrasCorrectasMaialen == GameWords.MAIALEN_WORDS.size) {
            puntos += configJuego.puntosExtraAmbosPersonajes
            Log.d("DRAG_GAME", "Bonus ambos personajes: +${configJuego.puntosExtraAmbosPersonajes}")
        }

        Log.d("DRAG_GAME", "Puntos totales: $puntos")

        return puntos
    }

    // Guardar puntuación en base de datos
    private fun guardarPuntuacion() {
        viewModelScope.launch {
            currentUserName?.let { nombreUsuario ->
                puntuazioaDao?.let { dao ->
                    val puntuazioActual = dao.getByName(nombreUsuario)

                    if (puntuazioActual != null) {
                        val nuevaPuntuazio = puntuazioActual.copy(
                            puntuazioaArropaBuruHandiak = puntuacionActual
                        )
                        dao.insert(nuevaPuntuazio)
                        Log.d("DRAG_GAME", "Puntuación guardada en BD: $puntuacionActual")
                    } else {
                        val nuevaPuntuazio = Puntuazioa(
                            izenaAbizena = nombreUsuario,
                            puntuazioaBertso = 0,
                            puntuazioaGalderak = 0,
                            puntuazioaGurutzegrama = 0,
                            puntuazioaArropaBuruHandiak = puntuacionActual,
                            puntuazioaPapresa = 0,
                            puntuazioaArrastrar = 0,
                            puntuazioaSopaLetra = 0
                        )
                        dao.insert(nuevaPuntuazio)
                        Log.d("DRAG_GAME", "Nueva puntuación creada en BD: $puntuacionActual")
                    }
                }
            }
        }
    }

    fun resetGame() {
        viewModelScope.launch {
            _uiState.update {
                DragGameUiState(
                    allWords = GameWords.ALL_WORDS.shuffled(),
                    xantiAssignments = List(GameWords.XANTI_WORDS.size) { null },
                    maialenAssignments = List(GameWords.MAIALEN_WORDS.size) { null },
                    showSuccessDialog = false,
                    showErrorDialog = false,
                    puntuacion = 0
                )
            }
            palabrasCorrectasXanti = 0
            palabrasCorrectasMaialen = 0
            puntuacionActual = 0
            Log.d("DRAG_GAME", "Juego reiniciado")
        }
    }

    fun showSuccessDialog(show: Boolean) {
        _uiState.update { it.copy(showSuccessDialog = show) }
        Log.d("DRAG_GAME", "Mostrar diálogo éxito: $show")
    }

    fun showErrorDialog(show: Boolean) {
        _uiState.update { it.copy(showErrorDialog = show) }
        Log.d("DRAG_GAME", "Mostrar diálogo error: $show")
    }

    // Método para establecer el usuario
    fun setUsuario(nombre: String) {
        currentUserName = nombre
        Log.d("DRAG_GAME", "Usuario establecido: $nombre")
    }
}