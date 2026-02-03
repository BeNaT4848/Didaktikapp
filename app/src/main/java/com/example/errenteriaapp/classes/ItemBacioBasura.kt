package com.example.errenteriaapp.classes

import androidx.compose.ui.graphics.Color
import com.example.errenteriaapp.R

/**
 * Hondakin elementu bat irudikatzen du.
 *
 * @property id Elementuaren identifikadore bakarra
 * @property nameResId Elementuaren izena duen baliabide-identifikadorea
 * @property correctCategory Elementua zein kategoriatan joan behar den
 * @property imageResId Elementuaren irudia duen baliabide-identifikadorea
 */
data class WasteItem(
    val id: Int,
    val nameResId: Int,
    val correctCategory: WasteCategory,
    val imageResId: Int
)

/**
 * Hondakinen kategoria bakoitza irudikatzen duen enumerazioa.
 *
 * @property color Kategoriaren kolorea UI elementuetarako
 * @property displayNameResId Kategoriaren izena duen baliabide-identifikadorea
 * @property descriptionResId Kategoriaren deskribapena duen baliabide-identifikadorea
 * @property imageResId Kategoriaren irudia duen baliabide-identifikadorea
 */
enum class WasteCategory(
    val color: Color,
    val displayNameResId: Int,
    val descriptionResId: Int,
    val imageResId: Int
) {
    /**
     * Horia kategoria - papera, kartoiak, eta plastikoak.
     */
    YELLOW(
        Color(0xFFFFEB3B),
        R.string.papresa_category_yellow,
        R.string.papresa_category_yellow_desc,
        R.drawable.ontzi_horia
    ),

    /**
     * Urdina kategoria - papera eta kartoi garbiak.
     */
    BLUE(
        Color(0xFF2196F3),
        R.string.papresa_category_blue,
        R.string.papresa_category_blue_desc,
        R.drawable.ontzi_urdina
    ),

    /**
     * Marroia kategoria - hondakin organikoak.
     */
    BROWN(
        Color(0xFF795548),
        R.string.papresa_category_brown,
        R.string.papresa_category_brown_desc,
        R.drawable.hondakin_organikoak
    ),

    /**
     * Beltza kategoria - ezin birziklatu daitezkeen hondakinak.
     */
    BLACK(
        Color(0xFF424242),
        R.string.papresa_category_black,
        R.string.papresa_category_black_desc,
        R.drawable.hondakin_organikoak
    )
}