package com.example.errenteriaapp.database.viewModel

import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.classes.CeldaEstado
import com.example.errenteriaapp.classes.CrucigramaEstado
import com.example.errenteriaapp.classes.PalabraInfo
import com.example.errenteriaapp.database.Puntuazioa
import com.example.errenteriaapp.database.PuntuazioaDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModela erabili gurutzegramaren egoera kudeatzeko
 * @see ViewModel
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 * @param configJuego Jokoaren konfigurazioa, ConfigJuego.DEFAULT_CRUCIGRAMA balioa erabiltzen du berez
 */
class CrucigramaViewModel(
    private val puntuazioaDao: PuntuazioaDao?,
    private val configJuego: ConfigJuego = ConfigJuego.DEFAULT_CRUCIGRAMA
) : ViewModel() {

    /**
     * Jokoaren konfigurazioaren datu-klasea
     * @property minCorrectosRequeridos Onargarriak diren hitz kopuru minimoa
     * @property necesitaTodosCorrectos Hitz guztiak onargarriak izan behar diren ala ez
     * @property puntosPorPalabraCompleta Hitz oso bakoitzeko puntuak
     * @property puntosPorLetraCorrecta Letra onargarri bakoitzeko puntuak
     * @property puntosExtraPerfecto Partida perfektuarentzako puntu gehigarriak
     */
    data class ConfigJuego(
        val minCorrectosRequeridos: Int,
        val necesitaTodosCorrectos: Boolean,
        val puntosPorPalabraCompleta: Int = 5,
        val puntosPorLetraCorrecta: Int = 0,
        val puntosExtraPerfecto: Int = 5
    ) {
        companion object {
            /**
             * Gurutzegrama erabilera arruntarentzako konfigurazio lehenetsia
             */
            val DEFAULT_CRUCIGRAMA = ConfigJuego(
                minCorrectosRequeridos = 3, // 5 hitzetatik gutxienez 3 onargarri
                necesitaTodosCorrectos = false,
                puntosPorPalabraCompleta = 5,
                puntosPorLetraCorrecta = 0,
                puntosExtraPerfecto = 5
            )
        }
    }

    /**
     * Oraingo erabiltzailearen izena gordetzeko
     */
    var currentUserName: String? = null

    /**
     * Gurutzegramako gelaxken egoera
     */
    private val _celdas = mutableStateOf(crearCeldasEstado())
    val celdas: State<List<CeldaEstado>> = _celdas

    private val _crucigramaEstado = mutableStateOf(CrucigramaEstado())
    val crucigramaEstado: State<CrucigramaEstado> = _crucigramaEstado

    /**
     * Egindako egiaztapena gordetzeko
     */
    private val _verificacionRealizada = mutableStateOf(false)
    val verificacionRealizada: State<Boolean> = _verificacionRealizada

    /**
     * Arrakasta elkarrizketaren bistaratzea kontrolatzeko
     */
    private val _mostrarDialogoExito = mutableStateOf(false)
    val mostrarDialogoExito: State<Boolean> = _mostrarDialogoExito

    /**
     * Errore elkarrizketaren bistaratzea kontrolatzeko
     */
    private val _mostrarDialogoError = mutableStateOf(false)
    val mostrarDialogoError: State<Boolean> = _mostrarDialogoError

    /**
     * Unean aktibatutako hitza gordetzeko
     */
    private val _palabraActiva = mutableStateOf<PalabraInfo?>(null)
    val palabraActiva: State<PalabraInfo?> = _palabraActiva

    /**
     * Hasierako instrukzioen bistaratzea kontrolatzeko
     */
    private val _mostrarInstruccionesIniciales = mutableStateOf(true)
    val mostrarInstruccionesIniciales: State<Boolean> = _mostrarInstruccionesIniciales

    // Jokoaren estatistikak
    private var palabrasCorrectas: Int = 0
    private var letrasCorrectas: Int = 0

    /**
     * Hitz osatuak zenbatzen ditu
     * @return Hitz osatu kopurua
     */
    private fun contarPalabrasCompletas(): Int {
        var completas = 0

        // 1. hitza: Horizontala, lerroa 0, zutabea 2, luzera 6 (LEIZEA)
        if (verificarPalabraCompleta(0, 2, 6, "HORIZONTAL", "LEIZEA")) completas++

        // 2. hitza: Bertikala, lerroa 0, zutabea 6, luzera 11 (ESTALAKTITA)
        if (verificarPalabraCompleta(0, 6, 11, "VERTICAL", "ESTALAKTITA")) completas++

        // 3. hitza: Bertikala, lerroa 0, zutabea 3, luzera 10 (ESTALAGMITA)
        if (verificarPalabraCompleta(0, 3, 10, "VERTICAL", "ESTALAGMITA")) completas++

        // 4. hitza: Horizontala, lerroa 3, zutabea 0, luzera 7 (ZUTABEA)
        if (verificarPalabraCompleta(3, 0, 7, "HORIZONTAL", "ZUTABEA")) completas++

        // 5. hitza: Bertikala, lerroa 2, zutabea 1, luzera 8 (RUPESTRE)
        if (verificarPalabraCompleta(2, 1, 8, "VERTICAL", "RUPESTRE")) completas++

        return completas
    }

    /**
     * Letra onargarriak zenbatzen ditu
     * @return Letra onargarri kopurua
     */
    private fun contarLetrasCorrectas(): Int {
        return _celdas.value.count {
            !it.esNegra && it.esCorrecta && it.letraUsuario != null
        }
    }

    /**
     * Hitz bat osatuta dagoen egiaztatzen du
     * @param filaInicio Hasiereko lerroa
     * @param columnaInicio Hasiereko zutabea
     * @param longitud Hitzaren luzera
     * @param direccion Norabidea (HORIZONTAL edo VERTICAL)
     * @param respuesta Erantzun zuzena
     * @return Hitza osatuta dagoen ala ez
     */
    private fun verificarPalabraCompleta(
        filaInicio: Int,
        columnaInicio: Int,
        longitud: Int,
        direccion: String,
        respuesta: String
    ): Boolean {
        for (i in 0 until longitud) {
            val fila = if (direccion == "HORIZONTAL") filaInicio else filaInicio + i
            val columna = if (direccion == "HORIZONTAL") columnaInicio + i else columnaInicio

            val celda = obtenerCelda(fila, columna)
            if (celda == null || !celda.esCorrecta || celda.letraUsuario == null) {
                return false
            }
        }
        return true
    }

    /**
     * Erantzunak egiaztatzen ditu eta puntuak kalkulatzen ditu
     */
    fun verificarRespuestas() {
        // Aurretiko egiaztapenas berrezartzen ditu
        val nuevaLista = _celdas.value.toMutableList()
        nuevaLista.forEachIndexed { index, celda ->
            if (!celda.esNegra) {
                nuevaLista[index] = celda.copy(
                    esCorrecta = false
                )
            }
        }
        _celdas.value = nuevaLista

        // Erantzunak egiaztatzen ditu
        verificarHorizontal(1, 0, 2, "LEIZEA")
        verificarHorizontal(5, 3, 0, "ZUTABEA")
        verificarVertical(2, 0, 3, "ESTALAGMITA")
        verificarVertical(4, 2, 1, "RUPESTRE")
        verificarVertical(3, 0, 6, "ESTALAKTITA")

        _verificacionRealizada.value = true

        // Estatistikak kalkulatzen ditu
        palabrasCorrectas = contarPalabrasCompletas()
        letrasCorrectas = contarLetrasCorrectas()

        // Jokoa osatuta dagoen egiaztatzen du
        val todasLasPalabras = palabrasCorrectas == 5
        val juegoCompletado = if (configJuego.necesitaTodosCorrectos) {
            todasLasPalabras
        } else {
            palabrasCorrectas >= configJuego.minCorrectosRequeridos
        }

        if (juegoCompletado) {
            // Puntuazioa gordetzen du gutxieneko irismena betetzen bada
            guardarPuntuacion()
            _mostrarDialogoExito.value = true
        } else {
            _mostrarDialogoError.value = true
        }
    }

    /**
     * Puntuazioa kalkulatzen du
     * @return Puntuazio totala
     */
    private fun calcularPuntuacion(): Int {
        var puntos = 0

        // Hitz osatu bakoitzeko puntuak
        puntos += palabrasCorrectas * configJuego.puntosPorPalabraCompleta

        // Letra onargarri bakoitzeko puntuak
        puntos += letrasCorrectas * configJuego.puntosPorLetraCorrecta

        // Bono partida perfektuarentzat
        if (palabrasCorrectas == 5) {
            puntos += configJuego.puntosExtraPerfecto
        }

        return puntos
    }

    /**
     * Puntuazioa datu-basean gordetzen du
     */
    fun guardarPuntuacion() {
        viewModelScope.launch {
            currentUserName?.let { nombreUsuario ->
                puntuazioaDao?.let { dao ->
                    val puntuazioActual = dao.getByName(nombreUsuario)
                    val puntosFinales = calcularPuntuacion()

                    if (puntuazioActual != null) {
                        val nuevaPuntuazio = puntuazioActual.copy(
                            puntuazioaGurutzegrama = puntosFinales
                        )
                        dao.insert(nuevaPuntuazio)
                    } else {
                        val nuevaPuntuazio = Puntuazioa(
                            izenaAbizena = nombreUsuario,
                            puntuazioaBertso = 0,
                            puntuazioaGalderak = 0,
                            puntuazioaGurutzegrama = puntosFinales,
                            puntuazioaArropaBuruHandiak = 0,
                            puntuazioaPapresa = 0,
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
     * Erabiltzailea ezartzen du
     * @param nombre Erabiltzailearen izena
     */
    fun setUsuario(nombre: String) {
        currentUserName = nombre
    }

    /**
     * Errore elkarrizketa ixteko
     */
    fun cerrarDialogoError() {
        _mostrarDialogoError.value = false
    }

    /**
     * Gelaxka zehatz bat lortzen du
     * @param fila Lerroa
     * @param columna Zutabea
     * @return Gelaxka-egoera edo null
     */
    fun obtenerCelda(fila: Int, columna: Int): CeldaEstado? {
        return _celdas.value.find { it.fila == fila && it.columna == columna }
    }

    /**
     * Letra adimendunez ezabatzen du, hitz aktiboaren barruan
     * @param fila Lerroa
     * @param columna Zutabea
     */
    fun borrarLetraInteligente(fila: Int, columna: Int) {
        val celdaActual = obtenerCelda(fila, columna)

        // Gelaxka aktiboak letra badu eta ez bada onargarria, normalean ezabatzen du
        if (celdaActual?.letraUsuario != null && !celdaActual.esCorrecta) {
            actualizarCelda(fila, columna) { it.copy(letraUsuario = null, esCorrecta = false) }
            return
        }

        // Gelaxka aktiboa hutsik badago, hitz aktiboan betetako azken gelaxka bilatzen du
        val palabra = _palabraActiva.value
        if (palabra != null) {
            var ultimaCasillaLlena: Pair<Int, Int>? = null

            // Hitz aktiboaren gelaxka guztiak bilatzen ditu
            for (i in 0 until palabra.longitud) {
                val currentFila = if (palabra.direccion == "HORIZONTAL") {
                    palabra.filaInicio
                } else {
                    palabra.filaInicio + i
                }

                val currentColumna = if (palabra.direccion == "HORIZONTAL") {
                    palabra.columnaInicio + i
                } else {
                    palabra.columnaInicio
                }

                val celda = obtenerCelda(currentFila, currentColumna)
                // Gelaxka existitzen bada, ez bada beltza, letra badu eta ez bada onargarria
                if (celda != null && !celda.esNegra && celda.letraUsuario != null && !celda.esCorrecta) {
                    ultimaCasillaLlena = Pair(currentFila, currentColumna)
                }
            }

            // Gelaxka beteta bat aurkitzen badu, bere edukia ezabatzen du
            ultimaCasillaLlena?.let { (filaLlena, columnaLlena) ->
                actualizarCelda(filaLlena, columnaLlena) { it.copy(letraUsuario = null, esCorrecta = false) }
            }
        }
    }

    /**
     * Gelaxka bat eguneratzen du
     * @param fila Lerroa
     * @param columna Zutabea
     * @param actualizacion Gelaxkaren eguneratze-funtzioa
     */
    private fun actualizarCelda(fila: Int, columna: Int, actualizacion: (CeldaEstado) -> CeldaEstado) {
        val index = _celdas.value.indexOfFirst { it.fila == fila && it.columna == columna }
        if (index != -1) {
            val nuevaLista = _celdas.value.toMutableList()
            nuevaLista[index] = actualizacion(nuevaLista[index])
            _celdas.value = nuevaLista
        }
    }

    /**
     * Gelaxka batean hitzak bilatzen ditu
     * @param fila Lerroa
     * @param columna Zutabea
     * @return Gelaxkan dauden hitzen zerrenda
     */
    fun encontrarPalabraEnCelda(fila: Int, columna: Int): List<PalabraInfo> {
        return _crucigramaEstado.value.mapaCeldas[Pair(fila, columna)] ?: emptyList()
    }

    /**
     * Jokoa osatuta dagoen egiaztatzen du
     * @return Jokoa osatuta dagoen ala ez
     */
    fun juegoCompletado(): Boolean {
        return _celdas.value
            .filter { !it.esNegra }
            .all { it.letraUsuario != null && it.esCorrecta }
    }

    /**
     * Hitz bat aktibatzen du bere zenbakiaren arabera
     * @param numero Aktibatu beharreko hitzaren zenbakia
     */
    fun activarPalabraPorNumero(numero: Int) {
        viewModelScope.launch {
            // Hitzaren zenbakiaren arabera bilatzen du
            val palabra = _crucigramaEstado.value.mapaPalabrasPorNumero[numero]
            palabra?.let {
                // Hitz bera aktibatuta badago, desaktibatzen du
                if (_palabraActiva.value?.numero == numero) {
                    _palabraActiva.value = null
                } else {
                    // Hitz berria aktibatzen du
                    _palabraActiva.value = it

                    // Atzerapen txiki UIa eguneratzeko
                    delay(50)
                }
            }
        }
    }

    /**
     * Fokua hurrengo gelaxkara eramaten du Enter sakatutakoan
     * @param fila Oraingo lerroa
     * @param columna Oraingo zutabea
     * @param coroutineScope Funtzioa exekutatuko den coroutine-esparrua
     * @param focusRequesters Foku-eskaeren mapa
     */
    fun moverFocoSiguienteDesdeEnter(
        fila: Int,
        columna: Int,
        coroutineScope: CoroutineScope,
        focusRequesters: Map<Pair<Int, Int>, FocusRequester>
    ) {
        val siguiente = obtenerSiguienteCeldaVacia(fila, columna)
            ?: obtenerCeldaSiguiente(fila, columna)

        siguiente?.let { (f, c) ->
            coroutineScope.launch {
                delay(50)
                focusRequesters[Pair(f, c)]?.requestFocus()
            }
        }
    }

    /**
     * Hurrengo gelaxka lortzen du hitz aktiboaren barruan
     * @param filaActual Oraingo lerroa
     * @param columnaActual Oraingo zutabea
     * @return Hurrengo gelaxkaren (lerroa, zutabea) edo null
     */
    fun obtenerCeldaSiguiente(filaActual: Int, columnaActual: Int): Pair<Int, Int>? {
        val palabra = _palabraActiva.value ?: return null

        val posActual = if (palabra.direccion == "HORIZONTAL") {
            columnaActual - palabra.columnaInicio
        } else {
            filaActual - palabra.filaInicio
        }

        val siguientePos = posActual + 1
        if (siguientePos >= palabra.longitud) return null

        val fila = if (palabra.direccion == "HORIZONTAL") {
            palabra.filaInicio
        } else {
            palabra.filaInicio + siguientePos
        }

        val columna = if (palabra.direccion == "HORIZONTAL") {
            palabra.columnaInicio + siguientePos
        } else {
            palabra.columnaInicio
        }

        return Pair(fila, columna)
    }

    /**
     * Gurutzegrama osoan aurreko gelaxka lortzen du
     * @param fila Oraingo lerroa
     * @param columna Oraingo zutabea
     * @return Aurreko gelaxkaren (lerroa, zutabea) edo null
     */
    fun obtenerCeldaAnteriorCompleta(fila: Int, columna: Int): Pair<Int, Int>? {
        val ultimaColumna = 7 // Zure sareak 8 zutabe ditu (0-7)

        // 1. Lehenik, aurreko zutabea probatzen du lerro berean
        if (columna > 0) {
            val prevCol = columna - 1
            val celda = obtenerCelda(fila, prevCol)
            if (celda != null && !celda.esNegra && !celda.esCorrecta) {
                return Pair(fila, prevCol)
            }
        }

        // 2. Lehenengo zutabea bada, aurreko lerroko azken zutabera joaten da
        if (fila > 0) {
            val prevFila = fila - 1

            // Azken zutabetik atzera bilatzen du
            for (prevCol in ultimaColumna downTo 0) {
                val celda = obtenerCelda(prevFila, prevCol)
                if (celda != null && !celda.esNegra && !celda.esCorrecta) {
                    return Pair(prevFila, prevCol)
                }
            }
        }

        // 3. Ez badu ezer aurkitzen, sare osoan atzera bilatzen du
        var currentFila = fila
        var currentColumna = columna - 1

        while (currentFila >= 0) {
            while (currentColumna >= 0) {
                val celda = obtenerCelda(currentFila, currentColumna)
                if (celda != null && !celda.esNegra && !celda.esCorrecta) {
                    return Pair(currentFila, currentColumna)
                }
                currentColumna--
            }
            // Aurreko lerrora joaten da, azken zutabearekin hasten delarik
            currentFila--
            currentColumna = ultimaColumna
        }

        return null
    }

    /**
     * Gurutzegrama osoan letra duen aurreko gelaxka lortzen du
     * @param fila Oraingo lerroa
     * @param columna Oraingo zutabea
     * @return Letra duen aurreko gelaxkaren (lerroa, zutabea) edo null
     */
    fun obtenerAnteriorCeldaLlenaCompleta(fila: Int, columna: Int): Pair<Int, Int>? {
        var currentFila = fila
        var currentColumna = columna - 1
        val ultimaColumna = 7

        while (currentFila >= 0) {
            while (currentColumna >= 0) {
                val celda = obtenerCelda(currentFila, currentColumna)
                if (celda != null &&
                    !celda.esNegra &&
                    !celda.esCorrecta &&
                    celda.letraUsuario != null &&
                    celda.letraUsuario != ' ') {
                    return Pair(currentFila, currentColumna)
                }
                currentColumna--
            }
            currentFila--
            currentColumna = ultimaColumna
        }

        return null
    }

    /**
     * Fokua aurreko gelaxkara eramaten du Delete sakatutakoan
     * @param fila Oraingo lerroa
     * @param columna Oraingo zutabea
     * @param coroutineScope Funtzioa exekutatuko den coroutine-esparrua
     * @param focusRequesters Foku-eskaeren mapa
     */
    fun moverFocoAnteriorDesdeBorrar(
        fila: Int,
        columna: Int,
        coroutineScope: CoroutineScope,
        focusRequesters: Map<Pair<Int, Int>, FocusRequester>
    ) {
        // Estrategia: BETI aurreko gelaxkara joan, beteta dagoen ala hutsi dagoen kontuan hartu gabe

        // 1. Lehenik, letra duen aurreko gelaxkaren bila saiatzen da
        val anteriorLlena = obtenerAnteriorCeldaLlenaCompleta(fila, columna)

        // 2. Ez badago letradun gelaxkarik, aurreko edozein gelaxkaren bila saiatzen da
        val anterior = anteriorLlena ?: obtenerCeldaAnteriorCompleta(fila, columna)

        anterior?.let { (f, c) ->
            coroutineScope.launch {
                delay(50)
                focusRequesters[Pair(f, c)]?.requestFocus()
            }
        }
    }

    /**
     * Gelaxka bateko letra aldatzen denean deitzen da
     * @param fila Lerroa
     * @param columna Zutabea
     * @param nuevoCaracter Letra berria edo null
     */
    fun onLetraCambiada(fila: Int, columna: Int, nuevoCaracter: Char?) {
        if (nuevoCaracter == null) return

        // 1. Letra eguneratzen du
        actualizarCelda(fila, columna) { celda ->
            if (!celda.esNegra && !celda.esCorrecta) {
                celda.copy(
                    letraUsuario = nuevoCaracter.uppercaseChar(),
                    esCorrecta = false
                )
            } else celda
        }
    }

    /**
     * Hitz aktiboan hutsik dagoen hurrengo gelaxka lortzen du
     * @param filaActual Oraingo lerroa
     * @param columnaActual Oraingo zutabea
     * @return Hutsik dagoen hurrengo gelaxkaren (lerroa, zutabea) edo null
     */
    fun obtenerSiguienteCeldaVacia(filaActual: Int, columnaActual: Int): Pair<Int, Int>? {
        val palabra = _palabraActiva.value ?: return null

        val posActual = if (palabra.direccion == "HORIZONTAL") {
            columnaActual - palabra.columnaInicio
        } else {
            filaActual - palabra.filaInicio
        }

        // Hitzaren barruan hutsik dagoen hurrengo gelaxka bilatzen du
        for (i in posActual + 1 until palabra.longitud) {
            val nextFila = if (palabra.direccion == "HORIZONTAL") {
                palabra.filaInicio
            } else {
                palabra.filaInicio + i
            }

            val nextColumna = if (palabra.direccion == "HORIZONTAL") {
                palabra.columnaInicio + i
            } else {
                palabra.columnaInicio
            }

            val celda = obtenerCelda(nextFila, nextColumna)
            if (celda != null && !celda.esNegra && celda.letraUsuario == null) {
                return Pair(nextFila, nextColumna)
            }
        }

        return null
    }

    /**
     * Hitz horizontal bat egiaztatzen du
     * @param numeroPista Hitzaren pista-zenbakia
     * @param filaInicio Hasiereko lerroa
     * @param columnaInicio Hasiereko zutabea
     * @param respuesta Erantzun zuzena
     */
    private fun verificarHorizontal(
        numeroPista: Int,
        filaInicio: Int,
        columnaInicio: Int,
        respuesta: String
    ) {
        var columna = columnaInicio
        val nuevaLista = _celdas.value.toMutableList()

        for (letra in respuesta) {
            val index = nuevaLista.indexOfFirst {
                it.fila == filaInicio && it.columna == columna
            }
            if (index != -1) {
                val celda = nuevaLista[index]
                val coincide = celda.letraUsuario?.equals(letra, ignoreCase = true) ?: false
                nuevaLista[index] = celda.copy(
                    esCorrecta = celda.esCorrecta || coincide
                )
            }
            columna++
        }

        _celdas.value = nuevaLista
    }

    /**
     * Hitz bertikal bat egiaztatzen du
     * @param numeroPista Hitzaren pista-zenbakia
     * @param filaInicio Hasiereko lerroa
     * @param columnaInicio Hasiereko zutabea
     * @param respuesta Erantzun zuzena
     */
    private fun verificarVertical(
        numeroPista: Int,
        filaInicio: Int,
        columnaInicio: Int,
        respuesta: String
    ) {
        var fila = filaInicio
        val nuevaLista = _celdas.value.toMutableList()

        for (letra in respuesta) {
            val index = nuevaLista.indexOfFirst {
                it.fila == fila && it.columna == columnaInicio
            }
            if (index != -1) {
                val celda = nuevaLista[index]
                val esCorrecta = celda.letraUsuario?.equals(letra, ignoreCase = true) ?: false
                nuevaLista[index] = celda.copy(esCorrecta = esCorrecta)
            }
            fila++
        }

        _celdas.value = nuevaLista
    }

    /**
     * UIa kontrolatzeko metodoak
     */

    /**
     * Instrukzioak ixteko
     */
    fun cerrarInstrucciones() {
        _mostrarInstruccionesIniciales.value = false
    }

    /**
     * Arrakasta elkarrizketa ixteko
     */
    fun cerrarDialogoExito() {
        _mostrarDialogoExito.value = false
    }

    /**
     * Hitz aktiboa desaktibatzeko
     */
    fun desactivarPalabra() {
        _palabraActiva.value = null
    }

    /**
     * Gurutzegramaren gelaxken egoera sortzen du
     * @return Gelaxka-egoeren zerrenda
     */
    private fun crearCeldasEstado(): List<CeldaEstado> {
        val celdas = mutableListOf<CeldaEstado>()

        // Lerroa 0
        celdas.add(CeldaEstado(0, 0, esNegra = true))
        celdas.add(CeldaEstado(0, 1, esNegra = true))
        celdas.add(CeldaEstado(0, 2, esNegra = false, numeroPista = 1, letraCorrecta = 'L'))
        celdas.add(CeldaEstado(0, 3, esNegra = false, numeroPista = 2, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(0, 4, esNegra = false, letraCorrecta = 'I'))
        celdas.add(CeldaEstado(0, 5, esNegra = false, letraCorrecta = 'Z'))
        celdas.add(CeldaEstado(0, 6, esNegra = false, numeroPista = 3, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(0, 7, esNegra = false, letraCorrecta = 'A'))

        // Lerroa 1
        celdas.add(CeldaEstado(1, 0, esNegra = true))
        celdas.add(CeldaEstado(1, 1, esNegra = true))
        celdas.add(CeldaEstado(1, 2, esNegra = true))
        celdas.add(CeldaEstado(1, 3, esNegra = false, letraCorrecta = 'S'))
        celdas.add(CeldaEstado(1, 4, esNegra = true))
        celdas.add(CeldaEstado(1, 5, esNegra = true))
        celdas.add(CeldaEstado(1, 6, esNegra = false, letraCorrecta = 'S'))
        celdas.add(CeldaEstado(1, 7, esNegra = true))

        // Lerroa 2
        celdas.add(CeldaEstado(2, 0, esNegra = true))
        celdas.add(CeldaEstado(2, 1, esNegra = false, numeroPista = 4, letraCorrecta = 'R'))
        celdas.add(CeldaEstado(2, 2, esNegra = true))
        celdas.add(CeldaEstado(2, 3, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(2, 4, esNegra = true))
        celdas.add(CeldaEstado(2, 5, esNegra = true))
        celdas.add(CeldaEstado(2, 6, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(2, 7, esNegra = true))

        // Lerroa 3
        celdas.add(CeldaEstado(3, 0, esNegra = false, numeroPista = 5, letraCorrecta = 'Z'))
        celdas.add(CeldaEstado(3, 1, esNegra = false, letraCorrecta = 'U'))
        celdas.add(CeldaEstado(3, 2, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(3, 3, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(3, 4, esNegra = false, letraCorrecta = 'B'))
        celdas.add(CeldaEstado(3, 5, esNegra = false, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(3, 6, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(3, 7, esNegra = true))

        // Lerroa 4
        celdas.add(CeldaEstado(4, 0, esNegra = true))
        celdas.add(CeldaEstado(4, 1, esNegra = false, letraCorrecta = 'P'))
        celdas.add(CeldaEstado(4, 2, esNegra = true))
        celdas.add(CeldaEstado(4, 3, esNegra = false, letraCorrecta = 'L'))
        celdas.add(CeldaEstado(4, 4, esNegra = true))
        celdas.add(CeldaEstado(4, 5, esNegra = true))
        celdas.add(CeldaEstado(4, 6, esNegra = false, letraCorrecta = 'L'))
        celdas.add(CeldaEstado(4, 7, esNegra = true))

        // Lerroa 5
        celdas.add(CeldaEstado(5, 0, esNegra = true))
        celdas.add(CeldaEstado(5, 1, esNegra = false, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(5, 2, esNegra = true))
        celdas.add(CeldaEstado(5, 3, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(5, 4, esNegra = true))
        celdas.add(CeldaEstado(5, 5, esNegra = true))
        celdas.add(CeldaEstado(5, 6, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(5, 7, esNegra = true))

        // Lerroa 6
        celdas.add(CeldaEstado(6, 0, esNegra = true))
        celdas.add(CeldaEstado(6, 1, esNegra = false, letraCorrecta = 'S'))
        celdas.add(CeldaEstado(6, 2, esNegra = true))
        celdas.add(CeldaEstado(6, 3, esNegra = false, letraCorrecta = 'G'))
        celdas.add(CeldaEstado(6, 4, esNegra = true))
        celdas.add(CeldaEstado(6, 5, esNegra = true))
        celdas.add(CeldaEstado(6, 6, esNegra = false, letraCorrecta = 'K'))
        celdas.add(CeldaEstado(6, 7, esNegra = true))

        // Lerroa 7
        celdas.add(CeldaEstado(7, 0, esNegra = true))
        celdas.add(CeldaEstado(7, 1, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(7, 2, esNegra = true))
        celdas.add(CeldaEstado(7, 3, esNegra = false, letraCorrecta = 'M'))
        celdas.add(CeldaEstado(7, 4, esNegra = true))
        celdas.add(CeldaEstado(7, 5, esNegra = true))
        celdas.add(CeldaEstado(7, 6, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(7, 7, esNegra = true))

        // Lerroa 8
        celdas.add(CeldaEstado(8, 0, esNegra = true))
        celdas.add(CeldaEstado(8, 1, esNegra = false, letraCorrecta = 'R'))
        celdas.add(CeldaEstado(8, 2, esNegra = true))
        celdas.add(CeldaEstado(8, 3, esNegra = false, letraCorrecta = 'I'))
        celdas.add(CeldaEstado(8, 4, esNegra = true))
        celdas.add(CeldaEstado(8, 5, esNegra = true))
        celdas.add(CeldaEstado(8, 6, esNegra = false, letraCorrecta = 'I'))
        celdas.add(CeldaEstado(8, 7, esNegra = true))

        // Lerroa 9
        celdas.add(CeldaEstado(9, 0, esNegra = true))
        celdas.add(CeldaEstado(9, 1, esNegra = false, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(9, 2, esNegra = true))
        celdas.add(CeldaEstado(9, 3, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(9, 4, esNegra = true))
        celdas.add(CeldaEstado(9, 5, esNegra = true))
        celdas.add(CeldaEstado(9, 6, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(9, 7, esNegra = true))

        // Lerroa 10
        celdas.add(CeldaEstado(10, 0, esNegra = true))
        celdas.add(CeldaEstado(10, 1, esNegra = true))
        celdas.add(CeldaEstado(10, 2, esNegra = true))
        celdas.add(CeldaEstado(10, 3, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(10, 4, esNegra = true))
        celdas.add(CeldaEstado(10, 5, esNegra = true))
        celdas.add(CeldaEstado(10, 6, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(10, 7, esNegra = true))

        return celdas
    }
}