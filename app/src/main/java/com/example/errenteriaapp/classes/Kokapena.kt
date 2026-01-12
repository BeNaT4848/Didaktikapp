package com.example.errenteriaapp.classes


data class Kokapena(
    val izena: String,
    val deskribapena: String,
    val latitudea: Double,
    val longitudea: Double
)

// Datos de ejemplo
val nireKokapenak = listOf(
    Kokapena("Guggenheim Museoa", "Arte modernoko museoa", 43.2687, -2.9337),
    Kokapena("San Mames", "Athletic Club-en futbol zelaia", 43.2642, -2.9497),
    Kokapena("Alde Zaharra", "Bilboko erdigune historikoa", 43.2570, -2.9229),
    Kokapena("Artxandako Funikularra", "Bista panoramikoak lortzeko", 43.2709, -2.9272)
)