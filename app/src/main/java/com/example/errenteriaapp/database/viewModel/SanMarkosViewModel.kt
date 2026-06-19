// app/src/main/java/com/example/errenteriaapp/database/viewModel/SanMarkosViewModel.kt
package com.example.errenteriaapp.database.viewModel

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.R
import com.example.errenteriaapp.database.Puntuazioa
import com.example.errenteriaapp.database.PuntuazioaDao
import kotlinx.coroutines.launch

/**
 * Galdera baten datuak aukera nahastuekin gordetzeko klasea
 * @property texto Galderaren testua (string baliabidearen IDa)
 * @property opcionesOriginales Aukera originalen zerrenda (string baliabideen IDak)
 * @property opcionesMezcladas Aukera nahastuen zerrenda (string baliabideen IDak)
 * @property respuestaCorrectaOriginal Erantzun zuzenaren indizea aukera originaletan
 * @property respuestaCorrectaMezclada Erantzun zuzenaren indizea aukera nahastuetan
 */
data class PreguntaConOpcionesMezcladas(
    @StringRes val texto: Int,
    @StringRes val opcionesOriginales: List<Int>,
    @StringRes val opcionesMezcladas: List<Int>,
    val respuestaCorrectaOriginal: Int,  // Aukera originaletan indizea
    val respuestaCorrectaMezclada: Int   // Aukera nahastuetan indizea
)

/**
 * ViewModela erabili San Markos galdera-jokoaren egoera kudeatzeko
 * @see ViewModel
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 * @param configJuego Jokoaren konfigurazioa, ConfigJuego.DEFAULT_SAN_MARKOS balioa erabiltzen du berez
 */
class SanMarkosViewModel(
    private val puntuazioaDao: PuntuazioaDao?,
    private val configJuego: ConfigJuego = ConfigJuego.DEFAULT_SAN_MARKOS
) : ViewModel() {

    /**
     * Jokoaren konfigurazioaren datu-klasea
     * @property minCorrectosRequeridos Onargarriak diren erantzun kopuru minimoa
     * @property puntosPorRespuestaCorrecta Erantzun zuzen bakoitzeko puntuak
     * @property puntosExtraPerfecto Partida perfektuarentzako puntu gehigarriak
     * @property puntosPorTodasCorrectas Erantzun guztiak zuzenak izateagatik puntu gehigarriak
     */
    data class ConfigJuego(
        val minCorrectosRequeridos: Int,
        val puntosPorRespuestaCorrecta: Int = 5,
        val puntosExtraPerfecto: Int = 5,
        val puntosPorTodasCorrectas: Int = 5
    ) {
        companion object {
            /**
             * San Markos jokoarentzako konfigurazio lehenetsia
             */
            val DEFAULT_SAN_MARKOS = ConfigJuego(
                minCorrectosRequeridos = 2,
                puntosPorRespuestaCorrecta = 5,
                puntosExtraPerfecto = 5,
                puntosPorTodasCorrectas = 5
            )
        }
    }

    /**
     * Oraingo erabiltzailearen izena gordetzeko
     */
    var currentUserName: String? = null

    // Jokoaren egoera
    /**
     * Unean erakusten ari den galderaren indizea
     */
    var galderaIndex by mutableIntStateOf(0)
        private set

    /**
     * Hautatutako aukeraren indizea (-1 ezer hautatu gabe)
     */
    var aukeraHautatua by mutableIntStateOf(-1)
        private set

    /**
     * Erantzun zuzen kopurua
     */
    var erantzunZuzenak by mutableIntStateOf(0)
        private set

    /**
     * Puntuazio totala
     */
    var puntuacionTotal by mutableIntStateOf(0)
        private set

    /**
     * Erantzundako galderen indizeen zerrenda
     */
    val galderakErantzunda = mutableStateListOf<Int>()

    /**
     * Erantzunen mapa: galdera-indizea -> (hautatutako indizea, zuzena den ala ez)
     */
    val erantzunak = mutableStateMapOf<Int, Pair<Int, Boolean>>()

    /**
     * Arrakasta elkarrizketaren bistaratzea kontrolatzeko
     */
    var showSuccessDialog by mutableStateOf(false)
        private set

    /**
     * Errore elkarrizketaren bistaratzea kontrolatzeko
     */
    var showWrongDialog by mutableStateOf(false)
        private set

    /**
     * Galdera baseak erantzun finkoekin
     */
    private val preguntasBase: List<Triple<Int, List<Int>, Int>> = listOf(
        Triple(
            R.string.sanmarkos_q1_text,
            listOf(
                R.string.sanmarkos_q1_opt1,
                R.string.sanmarkos_q1_opt2,
                R.string.sanmarkos_q1_opt3
            ),
            1
        ),
        Triple(
            R.string.sanmarkos_q2_text,
            listOf(
                R.string.sanmarkos_q2_opt1,
                R.string.sanmarkos_q2_opt2,
                R.string.sanmarkos_q2_opt3
            ),
            0
        ),
        Triple(
            R.string.sanmarkos_q3_text,
            listOf(
                R.string.sanmarkos_q3_opt1,
                R.string.sanmarkos_q3_opt2,
                R.string.sanmarkos_q3_opt3
            ),
            0
        )
    )

    /**
     * Aukera nahastuekin dauden galderak
     */
    var galderak by mutableStateOf<List<PreguntaConOpcionesMezcladas>>(emptyList())
        private set

    init {
        mezclarTodo()
    }

    /**
     * Galderak ETA erantzunak nahasten ditu
     */
    private fun mezclarTodo() {
        // 1. Galderen ordena nahastu
        val preguntasMezcladas = preguntasBase.shuffled()

        // 2. Galdera bakoitzean, aukerak nahastu
        val nuevasGalderak = mutableListOf<PreguntaConOpcionesMezcladas>()

        preguntasMezcladas.forEach { (texto, opciones, respuestaCorrecta) ->
            // Nahasteko indizeen zerrenda sortu
            val indices = opciones.indices.toList().shuffled()

            // Aukera nahastuak sortu
            val opcionesMezcladas = indices.map { opciones[it] }

            // Erantzun zuzenaren kokapena aurkitu aukera nahastuetan
            val nuevaPosicionCorrecta = indices.indexOf(respuestaCorrecta)

            nuevasGalderak.add(
                PreguntaConOpcionesMezcladas(
                    texto = texto,
                    opcionesOriginales = opciones,
                    opcionesMezcladas = opcionesMezcladas,
                    respuestaCorrectaOriginal = respuestaCorrecta,
                    respuestaCorrectaMezclada = nuevaPosicionCorrecta
                )
            )
        }

        galderak = nuevasGalderak

        // Egoera berrabiarazi
        resetGame()

        println("DEBUG - Galderak eta erantzunak nahastuak:")
        galderak.forEachIndexed { index, pregunta ->
            println("Galdera $index: ${pregunta.texto}")
            println("Aukera nahastuak: ${pregunta.opcionesMezcladas}")
            println("Erantzun zuzena (kokapen nahastua): ${pregunta.respuestaCorrectaMezclada}")
        }
    }

    /**
     * Aukera bat hautatzerakoan deitzen da
     * @param optionIndex Hautatutako aukeraren indizea
     */
    fun onOptionSelected(optionIndex: Int) {
        if (!galderakErantzunda.contains(galderaIndex)) {
            aukeraHautatua = optionIndex
            val preguntaActual = galderak[galderaIndex]
            val correct = optionIndex == preguntaActual.respuestaCorrectaMezclada

            erantzunak[galderaIndex] = Pair(optionIndex, correct)

            if (correct) {
                erantzunZuzenak++
                println("DEBUG - Erantzun ZUZENA!")
            } else {
                println("DEBUG - Erantzun OKERRA. Zuzena zen: ${preguntaActual.respuestaCorrectaMezclada}")
            }

            galderakErantzunda.add(galderaIndex)
        }
    }

    /**
     * Hurrengo galderara joateko edo jokoa amaitzeko deitzen da
     */
    fun onNextQuestion() {
        if (galderaIndex < galderak.size - 1) {
            // Hurrengo galderara joan
            galderaIndex++
            aukeraHautatua = -1
        } else {
            // Emaitzak egiaztatu
            verificarResultados()
        }
    }

    /**
     * Emaitzak egiaztatzen ditu eta puntuak kalkulatzen ditu
     */
    private fun verificarResultados() {
        val haAprobado = erantzunZuzenak >= configJuego.minCorrectosRequeridos

        println("DEBUG === EMAITZA FINALAK ===")
        println("DEBUG - Erantzun zuzenak: $erantzunZuzenak/${galderak.size}")
        println("DEBUG - Gutxieneko beharrezkoa: ${configJuego.minCorrectosRequeridos}")
        println("DEBUG - Gainditu du: $haAprobado")

        if (haAprobado) {
            // Puntuazioa kalkulatu
            puntuacionTotal = calcularPuntuacion()

            // Puntuak datu-basean gorde
            guardarPuntuacion()

            // Arrakasta elkarrizketa erakutsi
            showSuccessDialog = true
        } else {
            // Errore elkarrizketa erakutsi
            showWrongDialog = true
        }
    }

    /**
     * Puntuazioa kalkulatzen du
     * @return Puntuazio totala
     */
    private fun calcularPuntuacion(): Int {
        var puntos = 0

        // Erantzun zuzen bakoitzeko puntuak
        val puntosBase = erantzunZuzenak * configJuego.puntosPorRespuestaCorrecta
        puntos += puntosBase

        println("DEBUG - Puntu oinarrizkoak: $erantzunZuzenak × ${configJuego.puntosPorRespuestaCorrecta} = $puntosBase")

        // Bonus erantzun guztiak zuzenak izateagatik
        if (erantzunZuzenak == galderak.size) {
            puntos += configJuego.puntosPorTodasCorrectas
            println("DEBUG - Bonus guztiak zuzenak: +${configJuego.puntosPorTodasCorrectas}")
        }

        // Bonus gehigarria partida perfektuarentzat
        if (erantzunZuzenak == galderak.size) {
            puntos += configJuego.puntosExtraPerfecto
            println("DEBUG - Bonus perfektua: +${configJuego.puntosExtraPerfecto}")
        }

        println("DEBUG - Puntu guztira: $puntos")
        return puntos
    }

    /**
     * Puntuazioa datu-basean gordetzen du - ZUZENDUTA
     */
    private fun guardarPuntuacion() {
        viewModelScope.launch {
            try {
                println("DEBUG === PUNTUAZIOA GORDETZEN ===")
                println("DEBUG - Oraingo erabiltzailea: $currentUserName")
                println("DEBUG - Gorde beharreko puntuazioa: $puntuacionTotal")

                currentUserName?.let { nombreUsuario ->
                    println("DEBUG - Erabiltzaile balioduna: $nombreUsuario")

                    puntuazioaDao?.let { dao ->
                        println("DEBUG - DAO eskuragarri")

                        // Oraingo puntuazioa lortu
                        val puntuazioActual = dao.getByName(nombreUsuario)
                        println("DEBUG - Oraingo puntuazioa DBn: ${puntuazioActual?.puntuazioaGalderak}")

                        val nuevaPuntuazio = if (puntuazioActual != null) {
                            // Puntu berriak existitzen direnei gehitu
                            val puntosTotales = puntuazioActual.puntuazioaGalderak + puntuacionTotal
                            println("DEBUG - Puntuak gehitzen: ${puntuazioActual.puntuazioaGalderak} + $puntuacionTotal = $puntosTotales")

                            puntuazioActual.copy(
                                puntuazioaGalderak = puntosTotales
                            )
                        } else {
                            println("DEBUG - Erregistro berria sortzen")
                            Puntuazioa(
                                izenaAbizena = nombreUsuario,
                                puntuazioaBertso = 0,
                                puntuazioaGalderak = puntuacionTotal,
                                puntuazioaGurutzegrama = 0,
                                puntuazioaArropaBuruHandiak = 0,
                                puntuazioaPapresa = 0,
                                puntuazioaArrastrar = 0,
                                puntuazioaSopaLetra = 0
                            )
                        }

                        // Sartu edo eguneratu
                        dao.insert(nuevaPuntuazio)
                        println("DEBUG - Puntuazioa arrakastaz gordeta: ${nuevaPuntuazio.puntuazioaGalderak}")

                    } ?: run {
                        println("DEBUG - ERROREA: puntuazioaDao null da")
                    }
                } ?: run {
                    println("DEBUG - ERROREA: currentUserName null da")
                }
            } catch (e: Exception) {
                println("DEBUG - ERROREA puntuazioa gordetzean: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Jokoa berrabiarazten du
     */
    fun resetGame() {
        galderaIndex = 0
        aukeraHautatua = -1
        erantzunZuzenak = 0
        puntuacionTotal = 0
        galderakErantzunda.clear()
        erantzunak.clear()
        showSuccessDialog = false
        showWrongDialog = false
        println("DEBUG - Jokoa berrabiarazita")
    }

    /**
     * Elkarrizketak kontrolatzeko metodoak
     */

    /**
     * Arrakasta elkarrizketa ixteko
     */
    fun dismissSuccessDialog() {
        showSuccessDialog = false
    }

    /**
     * Errore elkarrizketa ixteko
     */
    fun dismissWrongDialog() {
        showWrongDialog = false
    }

    /**
     * Erabiltzailea ezartzen du
     * @param nombre Erabiltzailearen izena
     */
    fun setUsuario(nombre: String) {
        currentUserName = nombre
        println("DEBUG - Erabiltzailea ezarrita: $nombre")
    }

    /**
     * Kalkulaturiko propietateak
     */

    /**
     * Uneko galdera lortzen du
     * @return Uneko galdera edo lehenetsitako galdera errore bat baldin bada
     */
    val currentPregunta: PreguntaConOpcionesMezcladas
        get() = galderak.getOrNull(galderaIndex) ?: galderak.firstOrNull() ?:
        PreguntaConOpcionesMezcladas(
            texto = R.string.sanmarkos_q_error_text,
            opcionesOriginales = listOf(R.string.sanmarkos_q_error_opt),
            opcionesMezcladas = listOf(R.string.sanmarkos_q_error_opt),
            respuestaCorrectaOriginal = 0,
            respuestaCorrectaMezclada = 0
        )
}