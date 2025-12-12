package com.example.errenteriaapp.classes



import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color

data class PalabraSopa(
    val texto: String,
    val posiciones: List<Pair<Int, Int>>,
    val encontrada: Boolean = false,
    val color: Color = Color(0xFFFFD700)
)

data class CeldaSopa(
    val fila: Int,
    val columna: Int,
    val letra: Char,
    val palabraAsociada: String? = null,
    val encontrada: Boolean = false
)

data class SopaDeLetrasEstado(
    val palabras: List<PalabraSopa>,
    val tablero: Array<CharArray>,
    val palabrasEncontradas: MutableList<String> = mutableStateListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SopaDeLetrasEstado

        if (palabras != other.palabras) return false
        if (!tablero.contentDeepEquals(other.tablero)) return false
        if (palabrasEncontradas != other.palabrasEncontradas) return false

        return true
    }

    override fun hashCode(): Int {
        var result = palabras.hashCode()
        result = 31 * result + tablero.contentDeepHashCode()
        result = 31 * result + palabrasEncontradas.hashCode()
        return result
    }
}