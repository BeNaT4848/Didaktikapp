package com.example.errenteriaapp.classes



import androidx.compose.ui.graphics.Color
import com.example.errenteriaapp.R

data class WasteItem(
    val id: Int,
    val nameResId: Int,
    val correctCategory: WasteCategory,
    val imageResId: Int
)

enum class WasteCategory(
    val color: Color,
    val displayNameResId: Int,
    val descriptionResId: Int,
    val imageResId: Int
) {
    YELLOW(
        Color(0xFFFFEB3B),
        R.string.papresa_category_yellow,
        R.string.papresa_category_yellow_desc,
        R.drawable.ontzi_horia
    ),
    BLUE(
        Color(0xFF2196F3),
        R.string.papresa_category_blue,
        R.string.papresa_category_blue_desc,
        R.drawable.ontzi_urdina
    ),
    BROWN(
        Color(0xFF795548),
        R.string.papresa_category_brown,
        R.string.papresa_category_brown_desc,
        R.drawable.hondakin_organikoak
    ),
    BLACK(
        Color(0xFF424242),
        R.string.papresa_category_black,
        R.string.papresa_category_black_desc,
        R.drawable.hondakin_organikoak
    )
}