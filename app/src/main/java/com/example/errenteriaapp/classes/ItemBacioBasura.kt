package com.example.errenteriaapp.classes



import androidx.compose.ui.graphics.Color
import com.example.errenteriaapp.R

data class WasteItem(
    val id: Int,
    val name: String,
    val correctCategory: WasteCategory,
    val imageResId: Int
)

enum class WasteCategory(
    val color: Color,
    val displayName: String,
    val description: String,
    val imageResId: Int
) {
    YELLOW(
        Color(0xFFFFEB3B),
        "🟡 Ontzi horia",
        "Plastikoa, latak",
        R.drawable.ontzi_horia
    ),
    BLUE(
        Color(0xFF2196F3),
        "🔵 Ontzi urdina",
        "Papera eta kartoi",
        R.drawable.ontzi_urdina
    ),
    BROWN(
        Color(0xFF795548),
        "🟤 Hondakin organikoak",
        "Janari hondarrak",
        R.drawable.hondakin_organikoak
    ),
    BLACK(
        Color(0xFF424242),
        "⚫ Hondakin inorganikoak",
        "Birziklatu ezinak",
        R.drawable.hondakin_organikoak
    )
}