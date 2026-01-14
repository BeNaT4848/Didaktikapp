package com.example.errenteriaapp.classes


data class Kokapena(
    val izena: String,
    val deskribapena: String,
    val latitudea: Double,
    val longitudea: Double
)

// Datos de ejemplo
val nireKokapenak = listOf(
    Kokapena("Koldo Mitxelena Ikastetxea", "Errenteria", 43.31404559064167, -1.8999739279278425),
    Kokapena("Xenpelar etxea", "Errenteria", 43.31192190936388, -1.9004965765201356),
    Kokapena("Arramendi iturria", "Errenteria", 43.30762280468109, -1.9007732320843966),
    Kokapena("Fanderiako errota", "Errenteria", 43.30890295279845, -1.8880250016062692),
    Kokapena("Papresa", "Errenteria", 43.31252420825588, -1.89559828878411),
    Kokapena("Herriko Plaza", "Errenteria", 43.3125029994187, -1.9012615941295046)

)