package com.example.errenteriaapp.classes



// Estado para cada celda del crucigrama
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

// Definición de palabras del crucigrama
data class PalabraInfo(
    val numero: Int,
    val texto: String,
    val direccion: String, // "HORIZONTAL" o "VERTICAL"
    val filaInicio: Int,
    val columnaInicio: Int,
    val longitud: Int
)

class CrucigramaEstado {
    val palabras = listOf(
        PalabraInfo(1, "LEIZEA", "HORIZONTAL", 0, 2, 6),
        PalabraInfo(3, "ESTALAKTITA", "VERTICAL", 0, 6, 11),
        PalabraInfo(5, "ZUTABEA", "HORIZONTAL", 3, 0, 7),
        PalabraInfo(2, "ESTALAGMITA", "VERTICAL", 0, 3, 11),
        PalabraInfo(4, "RUPESTRE", "VERTICAL", 2, 1, 8)
    )

    // Mapa para acceso rápido a celdas por coordenadas
    val mapaCeldas = mutableMapOf<Pair<Int, Int>, MutableList<PalabraInfo>>()

    // Mapa para acceso rápido a palabras por número
    val mapaPalabrasPorNumero = mutableMapOf<Int, PalabraInfo>()

    init {
        // Construir mapa de celdas (una celda puede tener múltiples palabras)
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