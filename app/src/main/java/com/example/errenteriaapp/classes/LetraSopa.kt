package com.example.errenteriaapp.classes

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color

/**
 * Sopa-letren joko bateko hitz bat irudikatzen du.
 *
 * @property texto Hitzaren testua
 * @property posiciones Letra bakoitzaren posizioak taulan (errenkada, zutabea)
 * @property encontrada Hitzaren letrak markatuta dauden
 */
data class PalabraSopa(
    val texto: String,
    val posiciones: List<Pair<Int, Int>>,
    val encontrada: Boolean = false,
)

/**
 * Sopa-letren jokoaren taulako zelda bat.
 *
 * @property fila Zelda zein errenkadatan dagoen (0tik hasita)
 * @property columna Zelda zein zutabean dagoen (0tik hasita)
 * @property letra Zelda honetan dagoen letra
 * @property palabraAsociada Zelda hau zein hitzen parte den (null bada, ez da hitz baten parte)
 * @property encontrada Zelda markatuta dagoen (erabiltzaileak letra hautatu duen)
 */
data class CeldaSopa(
    val fila: Int,
    val columna: Int,
    val letra: Char,
    val palabraAsociada: String? = null,
    val encontrada: Boolean = false
)

/**
 * Sopa-letren jokoaren egoera orokorra.
 *
 * @property palabras Jokoaren hitzen zerrenda
 * @property tablero Letraz osatutako taula (array bikoitza)
 * @property palabrasEncontradas Erabiltzaileak topatutako hitzen zerrenda
 */
data class SopaDeLetrasEstado(
    val palabras: List<PalabraSopa>,
    val tablero: Array<CharArray>,
    val palabrasEncontradas: MutableList<String> = mutableStateListOf()
) {
    /**
     * Objektuen berdintasuna egiaztatzen du.
     * @param other Konparatzeko beste objektua
     * @return true objektu berdinak badira
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SopaDeLetrasEstado

        if (palabras != other.palabras) return false
        if (!tablero.contentDeepEquals(other.tablero)) return false
        if (palabrasEncontradas != other.palabrasEncontradas) return false

        return true
    }

    /**
     * Objektuaren hash kodea kalkulatzen du.
     * @return Objektuaren hash kodea
     */
    override fun hashCode(): Int {
        var result = palabras.hashCode()
        result = 31 * result + tablero.contentDeepHashCode()
        result = 31 * result + palabrasEncontradas.hashCode()
        return result
    }
}