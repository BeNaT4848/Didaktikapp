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

/**
 * "Arropa Buru Handiak" (Taula Arrastrar) jokoaren ViewModel-a.
 * Hitzak pertsonaien artean arrastatzearen logika kudeatzen du.
 * Puntuazioa ordenik gabe kalkulatzen du.
 *
 * @param puntuazioaDao Puntuazioaren datu-basearen atzipenerako DAO (null izan daiteke)
 * @param configJuego Jokoaren konfigurazioa (lehenetsiak erabiltzen ditu)
 */
class ArropaBuruHandiakViewModel(
    private val puntuazioaDao: PuntuazioaDao?,
    private val configJuego: ConfigJuego = ConfigJuego.DEFAULT_DRAG
) : ViewModel() {

    /**
     * Jokoaren konfigurazioa.
     * @property minCorrectosRequeridos Aprobatutako minimo hitz zuzenak
     * @property puntosPorPalabraCorrecta Hitz zuzen bakoitzeko puntuak
     * @property puntosExtraPerfecto Guztiz zuzena denean bonus puntuak
     * @property puntosExtraAmbosPersonajes Bi pertsonaiek guztiz zuzen dute bonus puntuak
     */
    data class ConfigJuego(
        val minCorrectosRequeridos: Int,
        val puntosPorPalabraCorrecta: Int = 2,
        val puntosExtraPerfecto: Int = 5,
        val puntosExtraAmbosPersonajes: Int = 2
    ) {
        companion object {
            /** Konfigurazio lehenetsiak */
            val DEFAULT_DRAG = ConfigJuego(
                minCorrectosRequeridos = 6,  // 9 hitzetatik 6 (10 ez, bat extra baitago)
                puntosPorPalabraCorrecta = 2,
                puntosExtraPerfecto = 5,
                puntosExtraAmbosPersonajes = 2
            )
        }
    }

    var currentUserName: String? = null

    // Egoera hasieratzailea
    private val _uiState = MutableStateFlow(DragGameUiState())
    val uiState: StateFlow<DragGameUiState> = _uiState.asStateFlow()

    // Asketzeko eremuak
    private val xantiDropZones = mutableListOf<Rect?>()
    private val maialenDropZones = mutableListOf<Rect?>()

    // Jokoaren estatistikak
    private var palabrasCorrectasXanti: Int = 0
    private var palabrasCorrectasMaialen: Int = 0
    private var puntuacionActual: Int = 0

    init {
        // Zerrendak tamaina egokian hasieratu
        repeat(GameWords.XANTI_WORDS.size) { xantiDropZones.add(null) }
        repeat(GameWords.MAIALEN_WORDS.size) { maialenDropZones.add(null) }

        // Egoera hasieratzailea
        resetGame()
    }

    // ===== EXISTITZEN DIREN FUNTZIOAK =====

    /**
     * Xanti pertsonaiko asketzeko eremua eguneratzen du.
     * @param index Eremuaren indizea
     * @param rect Eremuaren mugak (Rect Compose)
     */
    fun updateXantiDropZone(index: Int, rect: Rect) {
        if (index < xantiDropZones.size) {
            xantiDropZones[index] = rect
        }
    }

    /**
     * Maialen pertsonaiko asketzeko eremua eguneratzen du.
     * @param index Eremuaren indizea
     * @param rect Eremuaren mugak (Rect Compose)
     */
    fun updateMaialenDropZone(index: Int, rect: Rect) {
        if (index < maialenDropZones.size) {
            maialenDropZones[index] = rect
        }
    }

    /**
     * Hitz bat askatzean kudeatzen du.
     * Asketze-puntua erabiltzen du zein eremutan askatu den zehazteko.
     * @param dropPoint Asketzearen posizioa (Offset Compose)
     * @param draggingWord Arrastatzen ari den hitza
     * @param draggingSlotCharacter "Slot"-etik arrastatzen ari den pertsonaia
     * @param draggingSlotIndex "Slot"-aren indizea
     * @param isDraggingFromSlot "Slot"-etik arrastatzen ari den
     */
    fun handleDrop(
        dropPoint: Offset,
        draggingWord: String?,
        draggingSlotCharacter: Character?,
        draggingSlotIndex: Int?,
        isDraggingFromSlot: Boolean
    ) {
        viewModelScope.launch {
            val word = draggingWord ?: return@launch

            // Xanti pertsonaiko eremuetan bilatu
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

            // Maialen pertsonaiko eremuetan bilatu
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

    /**
     * Esleipenak eguneratzen ditu hitz bat pertsonaia baten eremu batean askatzean.
     * @param targetCharacter Helburuko pertsonaia
     * @param targetIndex Helburuko indizea
     * @param word Askatutako hitza
     * @param draggingSlotCharacter "Slot"-etik arrastatzen ari den pertsonaia
     * @param draggingSlotIndex "Slot"-aren indizea
     * @param isDraggingFromSlot "Slot"-etik arrastatzen ari den
     */
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

                    // "Slot"-etik badator, trukaketa kudeatu
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

                    // "Slot"-etik badator, trukaketa kudeatu
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

    /**
     * "Slot"-etatik trukaketa kudeatzen du (hitz bat beste batera mugitzean).
     * @param currentState Uneko egoera
     * @param existingWord Eremuan dagoen hitza (hutsik izan daiteke)
     * @param draggingSlotCharacter "Slot"-etik arrastatzen ari den pertsonaia
     * @param draggingSlotIndex "Slot"-aren indizea
     * @return Egoera eguneratua
     */
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

    // ===== PUNTUAKO FUNTZIOAK - ORDENIK GABE =====

    /**
     * Hitz bat normalizatzen du (zuriuneak kendu eta minuskulara bihurtu).
     * @param palabra Normalizatzeko hitza
     * @return Hitz normalizatua
     */
    private fun normalizarPalabra(palabra: String): String {
        return palabra.trim().lowercase()
    }

    /**
     * Erabiltzailearen erantzunak egiaztatzen ditu ORDENIK GABE.
     * @return (haAprobado, esPerfecto) bikotea (gainditu duen, perfektua den)
     */
    fun checkAnswers(): Pair<Boolean, Boolean> {
        val currentState = _uiState.value

        Log.d("DRAG_GAME", "=== EGIAZTAPENA HASITA (ORDENIK GABE) ===")
        Log.d("DRAG_GAME", "Xanti esleipenak: ${currentState.xantiAssignments}")
        Log.d("DRAG_GAME", "Maialen esleipenak: ${currentState.maialenAssignments}")

        // Konparatzeko zerrenda guztiak normalizatu
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

        Log.d("DRAG_GAME", "Xanti erabiltzailea (normalizatua): $palabrasXantiUsuario")
        Log.d("DRAG_GAME", "Xanti esperotakoa (normalizatua): $palabrasXantiEsperadas")
        Log.d("DRAG_GAME", "Maialen erabiltzailea (normalizatua): $palabrasMaialenUsuario")
        Log.d("DRAG_GAME", "Maialen esperotakoa (normalizatua): $palabrasMaialenEsperadas")

        // Ebaketa kopurua zenbatu (ordenik gabe hitz zuzenak)
        palabrasCorrectasXanti = palabrasXantiUsuario.intersect(palabrasXantiEsperadas).size
        palabrasCorrectasMaialen = palabrasMaialenUsuario.intersect(palabrasMaialenEsperadas).size

        // Hitz okerrak egiaztatu (pertsonaia bateko hitza bestean)
        val palabrasXantiEnMaialen = palabrasXantiUsuario.intersect(palabrasMaialenEsperadas).size
        val palabrasMaialenEnXanti = palabrasMaialenUsuario.intersect(palabrasXantiEsperadas).size

        val totalCorrectas = palabrasCorrectasXanti + palabrasCorrectasMaialen
        val totalPalabras = GameWords.XANTI_WORDS.size + GameWords.MAIALEN_WORDS.size

        Log.d("DRAG_GAME", "=== EMAITZAK ===")
        Log.d("DRAG_GAME", "Xanti zuzenak: $palabrasCorrectasXanti/${GameWords.XANTI_WORDS.size}")
        Log.d("DRAG_GAME", "Maialen zuzenak: $palabrasCorrectasMaialen/${GameWords.MAIALEN_WORDS.size}")
        Log.d("DRAG_GAME", "Xanti hitzak Maialen-en (errorea): $palabrasXantiEnMaialen")
        Log.d("DRAG_GAME", "Maialen hitzak Xanti-n (errorea): $palabrasMaialenEnXanti")
        Log.d("DRAG_GAME", "Guztira zuzenak: $totalCorrectas/$totalPalabras")
        Log.d("DRAG_GAME", "Minimo beharrezkoa: ${configJuego.minCorrectosRequeridos}")

        // Jokoa perfektua da HITZ GUZTIAK tokian badaude
        // eta EZ DAUDE pertsonaiak nahastuta
        val esPerfecto = (palabrasCorrectasXanti == GameWords.XANTI_WORDS.size &&
                palabrasCorrectasMaialen == GameWords.MAIALEN_WORDS.size &&
                palabrasXantiEnMaialen == 0 &&
                palabrasMaialenEnXanti == 0)

        // Gainditzen du gutxienez hitz zuzen minimoak baditu
        val haAprobado = totalCorrectas >= configJuego.minCorrectosRequeridos

        Log.d("DRAG_GAME", "Gainditu du: $haAprobado")
        Log.d("DRAG_GAME", "Perfektua da: $esPerfecto")

        if (haAprobado) {
            puntuacionActual = calcularPuntuacion()
            Log.d("DRAG_GAME", "Kalkulatutako puntuazioa: $puntuacionActual")
            guardarPuntuacion()

            // Puntuazioa egoeran eguneratu
            _uiState.update { it.copy(puntuacion = puntuacionActual) }
        }

        Log.d("DRAG_GAME", "=== EGIAZTAPENA AMAITUTA ===")

        return Pair(haAprobado, esPerfecto)
    }

    /**
     * Puntuazioa kalkulatzen du (ordenik gabe lan egiteko eguneratuta).
     * @return Kalkulatutako puntuazioa
     */
    private fun calcularPuntuacion(): Int {
        var puntos = 0

        // Oinarrizko puntuak hitz zuzen bakoitzeko
        puntos += (palabrasCorrectasXanti + palabrasCorrectasMaialen) * configJuego.puntosPorPalabraCorrecta

        Log.d("DRAG_GAME", "Oinarrizko puntuak: ${(palabrasCorrectasXanti + palabrasCorrectasMaialen) * configJuego.puntosPorPalabraCorrecta}")

        // Bonusa Xanti pertsonaiko hitz guztiak osatzean
        if (palabrasCorrectasXanti == GameWords.XANTI_WORDS.size) {
            puntos += configJuego.puntosExtraPerfecto / 2
            Log.d("DRAG_GAME", "Xanti osoaren bonusa: +${configJuego.puntosExtraPerfecto / 2}")
        }

        // Bonusa Maialen pertsonaiko hitz guztiak osatzean
        if (palabrasCorrectasMaialen == GameWords.MAIALEN_WORDS.size) {
            puntos += configJuego.puntosExtraPerfecto / 2
            Log.d("DRAG_GAME", "Maialen osoaren bonusa: +${configJuego.puntosExtraPerfecto / 2}")
        }

        // Bonusa bi pertsonaiek guztiz osatzean (hitz nahasirik gabe)
        if (palabrasCorrectasXanti == GameWords.XANTI_WORDS.size &&
            palabrasCorrectasMaialen == GameWords.MAIALEN_WORDS.size) {
            puntos += configJuego.puntosExtraAmbosPersonajes
            Log.d("DRAG_GAME", "Bi pertsonaien bonusa: +${configJuego.puntosExtraAmbosPersonajes}")
        }

        Log.d("DRAG_GAME", "Puntu guztiak: $puntos")

        return puntos
    }

    /**
     * Puntuazioa datu-basean gordetzen du.
     */
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
                        Log.d("DRAG_GAME", "Puntuazioa BD-n gordeta: $puntuacionActual")
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
                        Log.d("DRAG_GAME", "Puntuazio berria BD-n: $puntuacionActual")
                    }
                }
            }
        }
    }

    /**
     * Jokoa berrezartzen du hasierako egoerara.
     */
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
            Log.d("DRAG_GAME", "Jokoa berrezarrita")
        }
    }

    /**
     * Arrakasta-dialogoa erakusten du.
     * @param show Erakutsi ala ez
     */
    fun showSuccessDialog(show: Boolean) {
        _uiState.update { it.copy(showSuccessDialog = show) }
        Log.d("DRAG_GAME", "Arrakasta-dialogoa erakustea: $show")
    }

    /**
     * Errore-dialogoa erakusten du.
     * @param show Erakutsi ala ez
     */
    fun showErrorDialog(show: Boolean) {
        _uiState.update { it.copy(showErrorDialog = show) }
        Log.d("DRAG_GAME", "Errore-dialogoa erakustea: $show")
    }

    /**
     * Erabiltzailea ezartzen du.
     * @param nombre Erabiltzailearen izena
     */
    fun setUsuario(nombre: String) {
        currentUserName = nombre
        Log.d("DRAG_GAME", "Erabiltzailea ezarrita: $nombre")
    }
}