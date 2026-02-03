package com.example.errenteriaapp.classes

/**
 * Elkarrizketa bateko mezu bat irudikatzen du.
 *
 * @property textResId Mezuaren testua daukan baliabide-identifikadorea (strings.xml)
 * @property isFromXanti Mezua Xanti pertsonaikoak bidaltzen duen (false bada, Maialen pertsonaikoak bidaltzen du)
 * @property duration Mezua pantailan erakusteko denbora (milisegundotan)
 */
data class Dialogo(
    val textResId: Int,
    val isFromXanti: Boolean,
    val duration: Long
)