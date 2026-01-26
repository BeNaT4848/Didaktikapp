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

class CrucigramaViewModel (private val puntuazioaDao: PuntuazioaDao?,
                           private val configJuego: ConfigJuego = ConfigJuego.DEFAULT_CRUCIGRAMA
) : ViewModel() {

    data class ConfigJuego(
        val minCorrectosRequeridos: Int,
        val necesitaTodosCorrectos: Boolean,
        val puntosPorPalabraCompleta: Int = 5,
        val puntosPorLetraCorrecta: Int = 0,
        val puntosExtraPerfecto: Int = 5
    ) {
        companion object {
            val DEFAULT_CRUCIGRAMA = ConfigJuego(
                minCorrectosRequeridos = 3, // Mínimo 3 de las 5 palabras correctas
                necesitaTodosCorrectos = false,
                puntosPorPalabraCompleta = 5,
                puntosPorLetraCorrecta = 0,
                puntosExtraPerfecto = 5
            )
        }
    }

    // Añade esta variable para el nombre del usuario
    var currentUserName: String? = null

    // Estado del crucigrama
    private val _celdas = mutableStateOf(crearCeldasEstado())
    val celdas: State<List<CeldaEstado>> = _celdas

    private val _crucigramaEstado = mutableStateOf(CrucigramaEstado())
    val crucigramaEstado: State<CrucigramaEstado> = _crucigramaEstado

    private val _verificacionRealizada = mutableStateOf(false)
    val verificacionRealizada: State<Boolean> = _verificacionRealizada

    private val _mostrarDialogoExito = mutableStateOf(false)
    val mostrarDialogoExito: State<Boolean> = _mostrarDialogoExito

    private val _mostrarDialogoError = mutableStateOf(false)
    val mostrarDialogoError: State<Boolean> = _mostrarDialogoError
    private val _palabraActiva = mutableStateOf<PalabraInfo?>(null)
    val palabraActiva: State<PalabraInfo?> = _palabraActiva

    private val _mostrarInstruccionesIniciales = mutableStateOf(true)
    val mostrarInstruccionesIniciales: State<Boolean> = _mostrarInstruccionesIniciales

    // Estadísticas del juego
    private var palabrasCorrectas: Int = 0
    private var letrasCorrectas: Int = 0

    // Función para contar palabras completas
    private fun contarPalabrasCompletas(): Int {
        var completas = 0

        // Palabra 1: Horizontal, fila 0, columna 2, longitud 6 (LEIZEA)
        if (verificarPalabraCompleta(0, 2, 6, "HORIZONTAL", "LEIZEA")) completas++

        // Palabra 2: Vertical, fila 0, columna 6, longitud 11 (ESTALAKTITA)
        if (verificarPalabraCompleta(0, 6, 11, "VERTICAL", "ESTALAKTITA")) completas++

        // Palabra 3: Vertical, fila 0, columna 3, longitud 10 (ESTALAGMITA)
        if (verificarPalabraCompleta(0, 3, 10, "VERTICAL", "ESTALAGMITA")) completas++

        // Palabra 4: Horizontal, fila 3, columna 0, longitud 7 (ZUTABEA)
        if (verificarPalabraCompleta(3, 0, 7, "HORIZONTAL", "ZUTABEA")) completas++

        // Palabra 5: Vertical, fila 2, columna 1, longitud 8 (RUPESTRE)
        if (verificarPalabraCompleta(2, 1, 8, "VERTICAL", "RUPESTRE")) completas++

        return completas
    }

    // Función para contar letras correctas
    private fun contarLetrasCorrectas(): Int {
        return _celdas.value.count {
            !it.esNegra && it.esCorrecta && it.letraUsuario != null
        }
    }

    // Verificar si una palabra está completa
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

    // Función para verificar respuestas con puntos
    fun verificarRespuestas() {
        // Resetear verificaciones previas
        val nuevaLista = _celdas.value.toMutableList()
        nuevaLista.forEachIndexed { index, celda ->
            if (!celda.esNegra) {
                nuevaLista[index] = celda.copy(
                    esCorrecta = false
                )
            }
        }
        _celdas.value = nuevaLista

        // Verificar respuestas
        verificarHorizontal(1, 0, 2, "LEIZEA")
        verificarHorizontal(5, 3, 0, "ZUTABEA")
        verificarVertical(2, 0, 3, "ESTALAGMITA")
        verificarVertical(4, 2, 1, "RUPESTRE")
        verificarVertical(3, 0, 6, "ESTALAKTITA")

        _verificacionRealizada.value = true

        // Calcular estadísticas
        palabrasCorrectas = contarPalabrasCompletas()
        letrasCorrectas = contarLetrasCorrectas()

        // Verificar si el juego está completado
        val todasLasPalabras = palabrasCorrectas == 5
        val juegoCompletado = if (configJuego.necesitaTodosCorrectos) {
            todasLasPalabras
        } else {
            palabrasCorrectas >= configJuego.minCorrectosRequeridos
        }

        if (juegoCompletado) {
            // Guardar puntos si se cumple el mínimo
            guardarPuntuacion()
            _mostrarDialogoExito.value = true
        } else {
            _mostrarDialogoError.value = true
        }
    }

    // Calcular puntuación
    private fun calcularPuntuacion(): Int {
        var puntos = 0

        // Puntos por palabras completas
        puntos += palabrasCorrectas * configJuego.puntosPorPalabraCompleta

        // Puntos por letras correctas (solo si no están en palabras completas ya contadas)
        puntos += letrasCorrectas * configJuego.puntosPorLetraCorrecta

        // Bonus por juego perfecto
        if (palabrasCorrectas == 5) {
            puntos += configJuego.puntosExtraPerfecto
        }

        return puntos
    }

    // Guardar puntuación en base de datos
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

    // Método para establecer el usuario
    fun setUsuario(nombre: String) {
        currentUserName = nombre
    }

    // Métodos para controlar diálogos
    fun cerrarDialogoError() {
        _mostrarDialogoError.value = false
    }
    // Función para obtener una celda específica
    fun obtenerCelda(fila: Int, columna: Int): CeldaEstado? {
        return _celdas.value.find { it.fila == fila && it.columna == columna }
    }
    // Añade esta función en tu CrucigramaViewModel
    fun borrarLetraInteligente(fila: Int, columna: Int) {
        val celdaActual = obtenerCelda(fila, columna)

        // Si la celda actual tiene una letra y no es correcta, borrarla normalmente
        if (celdaActual?.letraUsuario != null && !celdaActual.esCorrecta) {
            actualizarCelda(fila, columna) { it.copy(letraUsuario = null, esCorrecta = false) }
            return
        }

        // Si la celda actual está vacía, buscar la última casilla llena en la palabra activa
        val palabra = _palabraActiva.value
        if (palabra != null) {
            // Buscar la última casilla llena en la palabra activa
            var ultimaCasillaLlena: Pair<Int, Int>? = null

            // Recorrer todas las celdas de la palabra activa
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
                // Si la celda existe, no es negra, tiene letra y no es correcta
                if (celda != null && !celda.esNegra && celda.letraUsuario != null && !celda.esCorrecta) {
                    ultimaCasillaLlena = Pair(currentFila, currentColumna)
                }
            }

            // Si encontramos una casilla llena, borrar su contenido
            ultimaCasillaLlena?.let { (filaLlena, columnaLlena) ->
                actualizarCelda(filaLlena, columnaLlena) { it.copy(letraUsuario = null, esCorrecta = false) }
            }
        }
    }
    // Función para actualizar una celda
    private fun actualizarCelda(fila: Int, columna: Int, actualizacion: (CeldaEstado) -> CeldaEstado) {
        val index = _celdas.value.indexOfFirst { it.fila == fila && it.columna == columna }
        if (index != -1) {
            val nuevaLista = _celdas.value.toMutableList()
            nuevaLista[index] = actualizacion(nuevaLista[index])
            _celdas.value = nuevaLista
        }
    }

    // Función para encontrar la palabra en una celda
    fun encontrarPalabraEnCelda(fila: Int, columna: Int): List<PalabraInfo> {
        return _crucigramaEstado.value.mapaCeldas[Pair(fila, columna)] ?: emptyList()
    }

    fun juegoCompletado(): Boolean {
        return _celdas.value
            .filter { !it.esNegra }
            .all { it.letraUsuario != null && it.esCorrecta }
    }

    // Función para activar una palabra por su número
    fun activarPalabraPorNumero(numero: Int) {
        viewModelScope.launch {
            // Buscar la palabra por número
            val palabra = _crucigramaEstado.value.mapaPalabrasPorNumero[numero]
            palabra?.let {
                // Si la misma palabra ya está activa, desactivarla
                if (_palabraActiva.value?.numero == numero) {
                    _palabraActiva.value = null
                } else {
                    // Activar la nueva palabra
                    _palabraActiva.value = it

                    // Pequeño delay para asegurar que la UI se actualice
                    delay(50)
                }
            }
        }
    }
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
    // Función para obtener la celda anterior en TODO el crucigrama (no solo en palabra activa)
    fun obtenerCeldaAnteriorCompleta(fila: Int, columna: Int): Pair<Int, Int>? {
        val ultimaColumna = 7 // Tu grid tiene 8 columnas (0-7)

        // 1. Primero intentar la columna anterior en la misma fila
        if (columna > 0) {
            val prevCol = columna - 1
            val celda = obtenerCelda(fila, prevCol)
            if (celda != null && !celda.esNegra && !celda.esCorrecta) {
                return Pair(fila, prevCol)
            }
        }

        // 2. Si es la primera columna, ir a la última columna de la fila anterior
        if (fila > 0) {
            val prevFila = fila - 1

            // Buscar desde la última columna hacia atrás
            for (prevCol in ultimaColumna downTo 0) {
                val celda = obtenerCelda(prevFila, prevCol)
                if (celda != null && !celda.esNegra && !celda.esCorrecta) {
                    return Pair(prevFila, prevCol)
                }
            }
        }

        // 3. Si no encontramos nada, buscar en toda la cuadrícula hacia atrás
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
            // Ir a la fila anterior, empezando por la última columna
            currentFila--
            currentColumna = ultimaColumna
        }

        return null
    }

    // Función que busca la celda vacía anterior en TODO el crucigrama


   
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

    fun moverFocoAnteriorDesdeBorrar(
        fila: Int,
        columna: Int,
        coroutineScope: CoroutineScope,
        focusRequesters: Map<Pair<Int, Int>, FocusRequester>
    ) {
        // Estrategia: SIEMPRE ir a la celda anterior, sin importar si está llena o vacía

        // 1. Primero intentar buscar cualquier celda anterior (priorizando celdas con letra)
        val anteriorLlena = obtenerAnteriorCeldaLlenaCompleta(fila, columna)

        // 2. Si no hay celda llena, buscar cualquier celda anterior (vacía o lo que sea)
        val anterior = anteriorLlena ?: obtenerCeldaAnteriorCompleta(fila, columna)

        anterior?.let { (f, c) ->
            coroutineScope.launch {
                delay(50)
                focusRequesters[Pair(f, c)]?.requestFocus()
            }
        }
    }



    fun onLetraCambiada(fila: Int, columna: Int, nuevoCaracter: Char?) {
        if (nuevoCaracter == null) return

        // 1. Actualizar letra
        actualizarCelda(fila, columna) { celda ->
            if (!celda.esNegra && !celda.esCorrecta) {
                celda.copy(
                    letraUsuario = nuevoCaracter.uppercaseChar(),
                    esCorrecta = false
                )
            } else celda
        }
    }



    fun obtenerSiguienteCeldaVacia(filaActual: Int, columnaActual: Int): Pair<Int, Int>? {
        val palabra = _palabraActiva.value ?: return null

        val posActual = if (palabra.direccion == "HORIZONTAL") {
            columnaActual - palabra.columnaInicio
        } else {
            filaActual - palabra.filaInicio
        }

        // Buscar la siguiente casilla VACÍA dentro de la palabra
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



    // Función para verificar respuestas


    // Funciones de verificación
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

    // Métodos para controlar UI
    fun cerrarInstrucciones() {
        _mostrarInstruccionesIniciales.value = false
    }

    fun cerrarDialogoExito() {
        _mostrarDialogoExito.value = false
    }

    fun desactivarPalabra() {
        _palabraActiva.value = null
    }

    // Función para crear celdas
    private fun crearCeldasEstado(): List<CeldaEstado> {
        val celdas = mutableListOf<CeldaEstado>()

        // Fila 0
        celdas.add(CeldaEstado(0, 0, esNegra = true))
        celdas.add(CeldaEstado(0, 1, esNegra = true))
        celdas.add(CeldaEstado(0, 2, esNegra = false, numeroPista = 1, letraCorrecta = 'L'))
        celdas.add(CeldaEstado(0, 3, esNegra = false, numeroPista = 2, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(0, 4, esNegra = false, letraCorrecta = 'I'))
        celdas.add(CeldaEstado(0, 5, esNegra = false, letraCorrecta = 'Z'))
        celdas.add(CeldaEstado(0, 6, esNegra = false, numeroPista = 3, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(0, 7, esNegra = false, letraCorrecta = 'A'))

        // Fila 1
        celdas.add(CeldaEstado(1, 0, esNegra = true))
        celdas.add(CeldaEstado(1, 1, esNegra = true))
        celdas.add(CeldaEstado(1, 2, esNegra = true))
        celdas.add(CeldaEstado(1, 3, esNegra = false, letraCorrecta = 'S'))
        celdas.add(CeldaEstado(1, 4, esNegra = true))
        celdas.add(CeldaEstado(1, 5, esNegra = true))
        celdas.add(CeldaEstado(1, 6, esNegra = false, letraCorrecta = 'S'))
        celdas.add(CeldaEstado(1, 7, esNegra = true))

        // Fila 2
        celdas.add(CeldaEstado(2, 0, esNegra = true))
        celdas.add(CeldaEstado(2, 1, esNegra = false, numeroPista = 4, letraCorrecta = 'R'))
        celdas.add(CeldaEstado(2, 2, esNegra = true))
        celdas.add(CeldaEstado(2, 3, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(2, 4, esNegra = true))
        celdas.add(CeldaEstado(2, 5, esNegra = true))
        celdas.add(CeldaEstado(2, 6, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(2, 7, esNegra = true))

        // Fila 3
        celdas.add(CeldaEstado(3, 0, esNegra = false, numeroPista = 5, letraCorrecta = 'Z'))
        celdas.add(CeldaEstado(3, 1, esNegra = false, letraCorrecta = 'U'))
        celdas.add(CeldaEstado(3, 2, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(3, 3, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(3, 4, esNegra = false, letraCorrecta = 'B'))
        celdas.add(CeldaEstado(3, 5, esNegra = false, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(3, 6, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(3, 7, esNegra = true))

        // Fila 4
        celdas.add(CeldaEstado(4, 0, esNegra = true))
        celdas.add(CeldaEstado(4, 1, esNegra = false, letraCorrecta = 'P'))
        celdas.add(CeldaEstado(4, 2, esNegra = true))
        celdas.add(CeldaEstado(4, 3, esNegra = false, letraCorrecta = 'L'))
        celdas.add(CeldaEstado(4, 4, esNegra = true))
        celdas.add(CeldaEstado(4, 5, esNegra = true))
        celdas.add(CeldaEstado(4, 6, esNegra = false, letraCorrecta = 'L'))
        celdas.add(CeldaEstado(4, 7, esNegra = true))

        // Fila 5
        celdas.add(CeldaEstado(5, 0, esNegra = true))
        celdas.add(CeldaEstado(5, 1, esNegra = false, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(5, 2, esNegra = true))
        celdas.add(CeldaEstado(5, 3, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(5, 4, esNegra = true))
        celdas.add(CeldaEstado(5, 5, esNegra = true))
        celdas.add(CeldaEstado(5, 6, esNegra = false, letraCorrecta = 'A'))
        celdas.add(CeldaEstado(5, 7, esNegra = true))

        // Fila 6
        celdas.add(CeldaEstado(6, 0, esNegra = true))
        celdas.add(CeldaEstado(6, 1, esNegra = false, letraCorrecta = 'S'))
        celdas.add(CeldaEstado(6, 2, esNegra = true))
        celdas.add(CeldaEstado(6, 3, esNegra = false, letraCorrecta = 'G'))
        celdas.add(CeldaEstado(6, 4, esNegra = true))
        celdas.add(CeldaEstado(6, 5, esNegra = true))
        celdas.add(CeldaEstado(6, 6, esNegra = false, letraCorrecta = 'K'))
        celdas.add(CeldaEstado(6, 7, esNegra = true))

        // Fila 7
        celdas.add(CeldaEstado(7, 0, esNegra = true))
        celdas.add(CeldaEstado(7, 1, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(7, 2, esNegra = true))
        celdas.add(CeldaEstado(7, 3, esNegra = false, letraCorrecta = 'M'))
        celdas.add(CeldaEstado(7, 4, esNegra = true))
        celdas.add(CeldaEstado(7, 5, esNegra = true))
        celdas.add(CeldaEstado(7, 6, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(7, 7, esNegra = true))

        // Fila 8
        celdas.add(CeldaEstado(8, 0, esNegra = true))
        celdas.add(CeldaEstado(8, 1, esNegra = false, letraCorrecta = 'R'))
        celdas.add(CeldaEstado(8, 2, esNegra = true))
        celdas.add(CeldaEstado(8, 3, esNegra = false, letraCorrecta = 'I'))
        celdas.add(CeldaEstado(8, 4, esNegra = true))
        celdas.add(CeldaEstado(8, 5, esNegra = true))
        celdas.add(CeldaEstado(8, 6, esNegra = false, letraCorrecta = 'I'))
        celdas.add(CeldaEstado(8, 7, esNegra = true))

        // Fila 9
        celdas.add(CeldaEstado(9, 0, esNegra = true))
        celdas.add(CeldaEstado(9, 1, esNegra = false, letraCorrecta = 'E'))
        celdas.add(CeldaEstado(9, 2, esNegra = true))
        celdas.add(CeldaEstado(9, 3, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(9, 4, esNegra = true))
        celdas.add(CeldaEstado(9, 5, esNegra = true))
        celdas.add(CeldaEstado(9, 6, esNegra = false, letraCorrecta = 'T'))
        celdas.add(CeldaEstado(9, 7, esNegra = true))

        // Fila 10
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