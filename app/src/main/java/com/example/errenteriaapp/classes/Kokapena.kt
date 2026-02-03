package com.example.errenteriaapp.classes

import com.example.errenteriaapp.navigation.Routes

/**
 * Kokaleku bat irudikatzen du Errenterian.
 *
 * @property izena Kokalekuaren izena
 * @property deskribapena Kokalekuaren deskribapen laburra
 * @property latitudea Kokalekuaren latitudea
 * @property longitudea Kokalekuaren longitudea
 * @property route Navegatzeko pantaila (aukerezkoa, null bada ez du nabigaziorik)
 */
data class Kokapena(
    val izena: String,
    val deskribapena: String,
    val latitudea: Double,
    val longitudea: Double,
    val route: String? = null // BERRIA: nabigatzeko pantaila (aukerezkoa)
)

/**
 * Errenteriako kokaleku adibideak nabigazio-rutekin.
 * @see Routes Nabigazio-rutak
 */
val nireKokapenak = listOf(
    Kokapena(
        "Koldo Mitxelena Ikastetxea",
        "Errenteria",
        43.31404559064167,
        -1.8999739279278425,
        route = Routes.TAULAARRASTRAR_SCRENN
        // Oharra: hau benetan aplikazioa irekitzean agertzen den hasierako azalpena da
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
        route = Routes.SANMARKOS_SCREEN
        // Kontuz: bikoitza da - SANMARKOS_SCREEN -> CRUCIGRAMA_SCREEN
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
        route = Routes.TAULAARRASTRAR_SCRENN
    )
)