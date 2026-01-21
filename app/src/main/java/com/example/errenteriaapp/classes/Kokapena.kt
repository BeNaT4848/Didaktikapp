package com.example.errenteriaapp.classes

import com.example.errenteriaapp.navigation.Routes

data class Kokapena(
    val izena: String,
    val deskribapena: String,
    val latitudea: Double,
    val longitudea: Double,
    val route: String? = null // NUEVO: pantalla a la que navegar (opcional)
)

// Datos de ejemplo con rutas asignadas
val nireKokapenak = listOf(
    Kokapena(
        "Koldo Mitxelena Ikastetxea",
        "Errenteria",
        43.31404559064167,
        -1.8999739279278425,
        route = Routes.BERTSOJOLASA_SCREEN
        //este de aqui realmente es la explicacion inicial que sale al abrir la app
    ),
    Kokapena(
        "Xenpelar etxea",
        "Errenteria",
        43.31192190936388,
        -1.9004965765201356,
        route = Routes.BERTSOJOLASA_SCREEN
    ),
    Kokapena(
        "Arramendi iturria",
        "Errenteria",
        43.30762280468109,
        -1.9007732320843966,
        route = Routes.CRUCIGRAMA_SCREEN
        //tambien deberia esta el juego de SANMARKOS_SCREEN en la ubicacion
    ),
    Kokapena(
        "Fanderiako errota",
        "Errenteria",
        43.30890295279845,
        -1.8880250016062692,
        route = Routes.ORDENATUJOLASA_SCREEN
    ),
    Kokapena(
        "Papresa",
        "Errenteria",
        43.31252420825588,
        -1.89559828878411,
        route = Routes.BASURA_SCREEN
    ),
    Kokapena(
        "Herriko Plaza",
        "Errenteria",
        43.3125029994187,
        -1.9012615941295046,
        route = Routes.SOPALETRA_SCREEN
        //tambien deberia esta el juego de TAULAARRASTRAR_SCRENN en la ubicacion
    )
)
