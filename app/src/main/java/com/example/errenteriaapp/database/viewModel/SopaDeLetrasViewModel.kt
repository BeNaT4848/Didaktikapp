// app/src/main/java/com/example/errenteriaapp/database/viewModel/SopaDeLetrasViewModel.kt
package com.example.errenteriaapp.database.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.errenteriaapp.classes.PalabraSopa
import com.example.errenteriaapp.database.Puntuazioa
import com.example.errenteriaapp.database.PuntuazioaDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Sopa de letras jokoaren egoera gordetzeko datu-klasea
 * @property palabrasEncontradas Aurkitu diren hitzen zerrenda
 * @property showSuccessDialog Arrakasta elkarrizketa erakutsi behar den ala ez
 * @property showWrongDialog Errore elkarrizketa erakutsi behar den ala ez
 * @property mostrarExito Arrakasta mezu orokorra erakutsi behar den ala ez
 * @property mostrarPista Pista bat erakutsi behar den ala ez
 * @property puntos Lortutako puntu kopurua
 */
data class SopaGameState(
    val palabrasEncontradas: List<String> = emptyList(),
    val showSuccessDialog: Boolean = false,
    val showWrongDialog: Boolean = false,
    val mostrarExito: Boolean = false,
    val mostrarPista: Boolean = false,
    val puntos: Int = 0
)

/**
 * ViewModela erabili sopa de letras jokoaren egoera kudeatzeko
 * @see ViewModel
 * @param puntuazioaDao Puntuazioak datu-basean gordetzeko erabiltzen den DAOa
 * @param configJuego Jokoaren konfigurazioa, ConfigJuego.DEFAULT_SOPA balioa erabiltzen du berez
 */
class SopaDeLetrasViewModel(
    private val puntuazioaDao: PuntuazioaDao?,
    private val configJuego: ConfigJuego = ConfigJuego.DEFAULT_SOPA
) : ViewModel() {

    /**
     * Jokoaren konfigurazioaren datu-klasea
     * @property minPalabrasRequeridas Aurkitu beharreko hitz kopuru minimoa
     * @property puntosPorPalabra Hitz aurkitu bakoitzeko puntuak
     * @property puntosPorLetra Letra aurkitu bakoitzeko puntuak
     * @property puntosExtraPerfecto Partida perfektuarentzako puntu gehigarriak
     * @property puntosExtraTodasPalabras Hitz guztiak aurkituz gero puntu gehigarriak
     */
    data class ConfigJuego(
        val minPalabrasRequeridas: Int,
        val puntosPorPalabra: Int = 3,
        val puntosPorLetra: Int = 0,
        val puntosExtraPerfecto: Int = 1,
        val puntosExtraTodasPalabras: Int = 2
    ) {
        companion object {
            /**
             * Sopa de letras jokoarentzako konfigurazio lehenetsia
             */
            val DEFAULT_SOPA = ConfigJuego(
                minPalabrasRequeridas = 5, // 8 hitzetatik gutxienez 5
                puntosPorPalabra = 3,
                puntosPorLetra = 0,
                puntosExtraPerfecto = 1,
                puntosExtraTodasPalabras = 2
            )
        }
    }

    /**
     * Oraingo erabiltzailearen izena gordetzeko
     */
    var currentUserName: String? = null

    /**
     * Jokoaren egoera pribatua
     */
    private val _gameState = MutableStateFlow(SopaGameState())

    /**
     * Jokoaren egoera publikoa (irakurgarria soilik)
     */
    val gameState = _gameState.asStateFlow()

    /**
     * Sopa de letrasen aurkitu beharreko hitzen zerrenda
     */
    val palabras = listOf(
        PalabraSopa(
            texto = "SAXOFOIA",
            posiciones = listOf(
                Pair(0, 3), Pair(0, 4), Pair(0, 5), Pair(0, 6),
                Pair(0, 7), Pair(0, 8), Pair(0, 9), Pair(0, 10)
            )
        ),
        PalabraSopa(
            texto = "ZEHARTXIRULA",
            posiciones = listOf(
                Pair(6, 0), Pair(6, 1), Pair(6, 2), Pair(6, 3),
                Pair(6, 4), Pair(6, 5), Pair(6, 6), Pair(6, 7),
                Pair(6, 8), Pair(6, 9), Pair(6, 10), Pair(6, 11)
            )
        ),
        PalabraSopa(
            texto = "KLARINETEA",
            posiciones = listOf(
                Pair(10, 0), Pair(10, 1), Pair(10, 2), Pair(10, 3),
                Pair(10, 4), Pair(10, 5), Pair(10, 6), Pair(10, 7),
                Pair(10, 8), Pair(10, 9)
            )
        ),
        PalabraSopa(
            texto = "DANBORRA",
            posiciones = listOf(
                Pair(11, -1), Pair(11, 0), Pair(11, 1), Pair(11, 2),
                Pair(11, 3), Pair(11, 4), Pair(11, 5), Pair(11, 6), Pair(11, 7)
            )
        ),
        PalabraSopa(
            texto = "TXINDATAK",
            posiciones = listOf(
                Pair(0, 12), Pair(1, 12), Pair(2, 12), Pair(3, 12),
                Pair(4, 12), Pair(5, 12), Pair(6, 12), Pair(7, 12), Pair(8, 12)
            )
        ),
        PalabraSopa(
            texto = "TRONPETA",
            posiciones = listOf(
                Pair(8, 3), Pair(8, 4), Pair(8, 5), Pair(8, 6),
                Pair(8, 7), Pair(8, 8), Pair(8, 9), Pair(8, 10)
            )
        ),
        PalabraSopa(
            texto = "TRONPA",
            posiciones = listOf(
                Pair(3, 6), Pair(3, 7), Pair(3, 8), Pair(3, 9),
                Pair(3, 10), Pair(3, 11)
            )
        ),
        PalabraSopa(
            texto = "TRONBOIA",
            posiciones = listOf(
                Pair(13, 5), Pair(13, 6), Pair(13, 7), Pair(13, 8),
                Pair(13, 9), Pair(13, 10), Pair(13, 11), Pair(13, 12)
            )
        )
    )

    /**
     * Sopa de letrasen taula (14x14)
     */
    val tablero = arrayOf(
        charArrayOf('T', 'H', 'H', 'S', 'A', 'X', 'O', 'F', 'O', 'I', 'A', 'Z', 'T', 'A'),
        charArrayOf('Q', 'B', 'P', 'B', 'W', 'U', 'G', 'S', 'Y', 'P', 'R', 'J', 'X', 'A'),
        charArrayOf('P', 'M', 'K', 'J', 'G', 'X', 'E', 'G', 'K', 'O', 'E', 'R', 'I', 'C'),
        charArrayOf('I', 'Q', 'Q', 'L', 'B', 'S', 'T', 'R', 'O', 'N', 'P', 'A', 'N', 'K'),
        charArrayOf('U', 'H', 'M', 'L', 'N', 'E', 'R', 'F', 'I', 'G', 'M', 'D', 'D', 'I'),
        charArrayOf('O', 'A', 'G', 'R', 'H', 'G', 'M', 'G', 'X', 'D', 'H', 'X', 'A', 'M'),
        charArrayOf('Z', 'E', 'H', 'A', 'R', 'T', 'X', 'I', 'R', 'U', 'L', 'A', 'T', 'C'),
        charArrayOf('X', 'C', 'Y', 'O', 'U', 'E', 'A', 'K', 'V', 'V', 'T', 'B', 'A', 'J'),
        charArrayOf('C', 'N', 'C', 'T', 'R', 'O', 'N', 'P', 'E', 'T', 'A', 'A', 'K', 'C'),
        charArrayOf('I', 'L', 'V', 'Y', 'G', 'G', 'E', 'Y', 'G', 'S', 'L', 'G', 'J', 'I'),
        charArrayOf('K', 'L', 'A', 'R', 'I', 'N', 'E', 'T', 'E', 'A', 'J', 'I', 'D', 'U'),
        charArrayOf('D', 'A', 'N', 'B', 'O', 'R', 'R', 'A', 'U', 'F', 'X', 'H', 'I', 'D'),
        charArrayOf('I', 'B', 'A', 'W', 'E', 'W', 'H', 'K', 'C', 'X', 'C', 'S', 'W', 'X'),
        charArrayOf('M', 'Z', 'X', 'C', 'T', 'T', 'R', 'O', 'N', 'B', 'O', 'I', 'A', 'T')
    )

    /**
     * Puntuazio totala kalkulatzen du
     * @return Puntuazio totala
     */
    private fun calcularPuntuacion(): Int {
        val palabrasEncontradas = _gameState.value.palabrasEncontradas.size
        var puntos = 0

        // Hitz aurkitu bakoitzeko puntuak
        puntos += palabrasEncontradas * configJuego.puntosPorPalabra

        // Aurkitu diren hitzetako letra bakoitzeko puntuak
        val totalLetras = palabras.sumOf { if (it.texto in _gameState.value.palabrasEncontradas) it.texto.length else 0 }
        puntos += totalLetras * configJuego.puntosPorLetra

        // Bonus hitz guztiak aurkitzeagatik
        if (palabrasEncontradas == palabras.size) {
            puntos += configJuego.puntosExtraTodasPalabras
        }

        // Bonus gehigarria kantitate esanguratsua aurkitzeagatik
        if (palabrasEncontradas >= configJuego.minPalabrasRequeridas * 1.5) {
            puntos += configJuego.puntosExtraPerfecto
        }

        return puntos
    }

    /**
     * Hitz bat aurkitu dela markatzen du
     * @param palabra Aurkitu den hitza
     */
    fun marcarPalabraEncontrada(palabra: String) {
        if (!_gameState.value.palabrasEncontradas.contains(palabra)) {
            val nuevasEncontradas = _gameState.value.palabrasEncontradas + palabra

            // Puntu berriak kalkulatu
            val nuevosPuntos = calcularPuntuacionParaPalabra(palabra)

            _gameState.update {
                it.copy(
                    palabrasEncontradas = nuevasEncontradas,
                    puntos = it.puntos + nuevosPuntos
                )
            }

            // Jokoa osatu den egiaztatu
            if (nuevasEncontradas.size == palabras.size) {
                // Puntuak gorde hitz guztiak aurkitzen direnean
                guardarPuntuacionFinal()
                _gameState.update { it.copy(mostrarExito = true) }
            } else if (nuevasEncontradas.size >= configJuego.minPalabrasRequeridas) {
                // Gutxieneko iritsiz gero, puntu partzialak gorde
                guardarPuntuacionParcial(nuevasEncontradas.size)
            }
        }
    }

    /**
     * Hitz baten puntuazioa kalkulatzen du
     * @param palabra Kalkulatu beharreko hitza
     * @return Hitzaren puntuazioa
     */
    private fun calcularPuntuacionParaPalabra(palabra: String): Int {
        val palabraInfo = palabras.find { it.texto == palabra }
        return if (palabraInfo != null) {
            // Puntuak hitzagatik + puntuak letragatik
            configJuego.puntosPorPalabra + (palabraInfo.texto.length * configJuego.puntosPorLetra)
        } else {
            0
        }
    }

    /**
     * Puntuazio partziala datu-basean gordetzen du
     * @param palabrasEncontradas Aurkitu diren hitz kopurua
     */
    private fun guardarPuntuacionParcial(palabrasEncontradas: Int) {
        viewModelScope.launch {
            currentUserName?.let { nombreUsuario ->
                puntuazioaDao?.let { dao ->
                    val puntuazioActual = dao.getByName(nombreUsuario)
                    val puntosParciales = calcularPuntuacion()

                    if (puntuazioActual != null) {
                        // Puntuak badauzka, puntu berriak gehitu
                        val nuevaPuntuazio = puntuazioActual.copy(
                            puntuazioaSopaLetra = puntosParciales
                        )
                        dao.insert(nuevaPuntuazio)
                    } else {
                        val nuevaPuntuazio = Puntuazioa(
                            izenaAbizena = nombreUsuario,
                            puntuazioaBertso = 0,
                            puntuazioaGalderak = 0,
                            puntuazioaGurutzegrama = 0,
                            puntuazioaArropaBuruHandiak = 0,
                            puntuazioaPapresa = 0,
                            puntuazioaArrastrar = 0,
                            puntuazioaSopaLetra = puntosParciales
                        )
                        dao.insert(nuevaPuntuazio)
                    }
                }
            }
        }
    }

    /**
     * Puntuazio finala datu-basean gordetzen du
     */
    private fun guardarPuntuacionFinal() {
        viewModelScope.launch {
            currentUserName?.let { nombreUsuario ->
                puntuazioaDao?.let { dao ->
                    val puntuazioActual = dao.getByName(nombreUsuario)
                    val puntosFinales = calcularPuntuacion()

                    if (puntuazioActual != null) {
                        val nuevaPuntuazio = puntuazioActual.copy(
                            puntuazioaSopaLetra = puntosFinales
                        )
                        dao.insert(nuevaPuntuazio)
                    } else {
                        val nuevaPuntuazio = Puntuazioa(
                            izenaAbizena = nombreUsuario,
                            puntuazioaBertso = 0,
                            puntuazioaGalderak = 0,
                            puntuazioaGurutzegrama = 0,
                            puntuazioaArropaBuruHandiak = 0,
                            puntuazioaPapresa = 0,
                            puntuazioaArrastrar = 0,
                            puntuazioaSopaLetra = puntosFinales
                        )
                        dao.insert(nuevaPuntuazio)
                    }
                }
            }
        }
    }

    /**
     * Arrakasta elkarrizketa ezkutatzeko
     */
    fun hideSuccessDialog() {
        _gameState.update { it.copy(mostrarExito = false) }
    }

    /**
     * Erabiltzailea ezartzen du
     * @param nombre Erabiltzailearen izena
     */
    fun setUsuario(nombre: String) {
        currentUserName = nombre
    }
}