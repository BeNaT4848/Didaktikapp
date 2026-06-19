package com.example.errenteriaapp.classes

/**
 * Gurutze-hitzean zelda bakoitzaren egoera gordetzen du.
 *
 * @property fila Zelda zein errenkadatan dagoen (0tik hasita)
 * @property columna Zelda zein zutabean dagoen (0tik hasita)
 * @property esNegra Zelda beltz bat den (hitzak bereizteko)
 * @property numeroPista Pisten zenbakia (null bada, ez du zenbakirik)
 * @property letraUsuario Erabiltzaileak sartutako letra
 * @property letraCorrecta Zelda honetako letra zuzena
 * @property esCorrecta Erabiltzaileak letra zuzena sartu duen
 * @property estaEditando Erabiltzailea zelda honetan editatzen ari den
 */
data class CeldaEstado(
    val fila: Int,
    val columna: Int,
    val esNegra: Boolean = false,
    val numeroPista: Int? = null,
    var letraUsuario: Char? = null,
    val letraCorrecta: Char? = null,
    var esCorrecta: Boolean = false,
    var estaEditando: Boolean = false
)

/**
 * Gurutze-hitz bateko hitz baten definizioa.
 *
 * @property numero Pisten zenbakia
 * @property texto Hitzaren testua (letrak jarraian)
 * @property direccion Hitzaren norabidea: "HORIZONTAL" edo "VERTICAL"
 * @property filaInicio Hasierako errenkada posizioa
 * @property columnaInicio Hasierako zutabe posizioa
 * @property longitud Hitzaren letra kopurua
 */
data class PalabraInfo(
    val numero: Int,
    val texto: String,
    val direccion: String, // "HORIZONTAL" edo "VERTICAL"
    val filaInicio: Int,
    val columnaInicio: Int,
    val longitud: Int
)

/**
 * Gurutze-hitzaren egoera orokorra gordetzen du.
 * Hitzak definitzen ditu eta mapa batzuk eraikitzen ditu sarbide azkarra izateko.
 */
class CrucigramaEstado {
    /**
     * Gurutze-hitzaren hitz guztien zerrenda.
     * @see PalabraInfo
     */
    val palabras = listOf(
        PalabraInfo(1, "LEIZEA", "HORIZONTAL", 0, 2, 6),
        PalabraInfo(3, "ESTALAKTITA", "VERTICAL", 0, 6, 11),
        PalabraInfo(5, "ZUTABEA", "HORIZONTAL", 3, 0, 7),
        PalabraInfo(2, "ESTALAGMITA", "VERTICAL", 0, 3, 11),
        PalabraInfo(4, "RUPESTRE", "VERTICAL", 2, 1, 8)
    )

    /**
     * Zelda bakoitzean ze hitz dauden gordetzen du.
     * Giltza: (errenkada, zutabea) bikotea
     * Balioa: zelda horretan dauden hitzen zerrenda
     */
    val mapaCeldas = mutableMapOf<Pair<Int, Int>, MutableList<PalabraInfo>>()

    /**
     * Zenbakiaren arabera hitz bat bilatzeko mapa.
     * Giltza: pisten zenbakia
     * Balioa: hitzaren informazioa
     */
    val mapaPalabrasPorNumero = mutableMapOf<Int, PalabraInfo>()

    init {
        // Mapak eraiki
        palabras.forEach { palabra ->
            mapaPalabrasPorNumero[palabra.numero] = palabra
            for (i in 0 until palabra.longitud) {
                val fila = if (palabra.direccion == "HORIZONTAL") palabra.filaInicio else palabra.filaInicio + i
                val columna = if (palabra.direccion == "HORIZONTAL") palabra.columnaInicio + i else palabra.columnaInicio
                val key = Pair(fila, columna)
                if (!mapaCeldas.containsKey(key)) {
                    mapaCeldas[key] = mutableListOf()
                }
                mapaCeldas[key]?.add(palabra)
            }
        }
    }
}