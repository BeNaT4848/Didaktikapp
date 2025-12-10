package com.example.errenteriaapp.viewModel



import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.errenteriaapp.R
import com.example.errenteriaapp.classes.WasteCategory
import com.example.errenteriaapp.classes.WasteItem


class PapresaViewModel : ViewModel() {

    // Datos del juego
    val wasteItems = mutableStateListOf(
        WasteItem(1, "Botella de agua", WasteCategory.YELLOW, R.drawable.botella_agua),
        WasteItem(2, "Lata de refresco", WasteCategory.YELLOW, R.drawable.lata_refresco),
        WasteItem(3, "Bolsa de patatas", WasteCategory.YELLOW, R.drawable.bolsa_patatas),
        WasteItem(4, "Tapon de plastico", WasteCategory.YELLOW, R.drawable.tapones_plastico),
        WasteItem(5, "Yogurt", WasteCategory.YELLOW, R.drawable.yogurt),
        WasteItem(6, "Caja de cereales", WasteCategory.BLUE, R.drawable.caja_cereales),
        WasteItem(7, "Periodico", WasteCategory.BLUE, R.drawable.periodico),
        WasteItem(8, "Cuaderno de papel", WasteCategory.BLUE, R.drawable.cuaderno_papel),
        WasteItem(9, "Tubo de carton", WasteCategory.BLUE, R.drawable.tubo_carton),
        WasteItem(10, "Sobre", WasteCategory.BLUE, R.drawable.sobre),
        WasteItem(11, "Piel de fruta", WasteCategory.BROWN, R.drawable.piel_fruta),
        WasteItem(12, "Restos de verduras", WasteCategory.BROWN, R.drawable.restos_verduras),
        WasteItem(13, "Pan", WasteCategory.BROWN, R.drawable.pan),
        WasteItem(14, "Huesos", WasteCategory.BROWN, R.drawable.huesos),
        WasteItem(15, "Sobras de comida", WasteCategory.BROWN, R.drawable.sobras),
        WasteItem(16, "Chicle", WasteCategory.BLACK, R.drawable.chicle),
        WasteItem(17, "Colillas", WasteCategory.BLACK, R.drawable.colillas),
        WasteItem(18, "Compresa", WasteCategory.BLACK, R.drawable.gorro_sanitario),
        WasteItem(19, "Tiritas", WasteCategory.BLACK, R.drawable.tiritas),
        WasteItem(20, "Panal", WasteCategory.BLACK, R.drawable.panal)
    )

    // Estados
    var currentIndex by mutableStateOf(0)
        private set

    val userAnswers = mutableMapOf<Int, WasteCategory>()

    var showResults by mutableStateOf(false)
        private set

    // Propiedades computadas
    val allAnswered: Boolean
        get() = wasteItems.all { userAnswers.containsKey(it.id) }

    val answeredCount: Int
        get() = userAnswers.size

    val totalCount: Int
        get() = wasteItems.size

    val currentItem: WasteItem?
        get() = wasteItems.getOrNull(currentIndex)

    val score: Int
        get() {
            val correctAnswers = wasteItems.count { item ->
                userAnswers[item.id] == item.correctCategory
            }
            return ((correctAnswers.toFloat() / wasteItems.size) * 100).toInt()
        }

    val correctAnswersCount: Int
        get() = wasteItems.count { item ->
            userAnswers[item.id] == item.correctCategory
        }

    // Funciones de acción
    fun onContainerClick(category: WasteCategory) {
        val currentItem = currentItem
        currentItem?.let {
            userAnswers[it.id] = category
            if (currentIndex < wasteItems.size - 1) {
                currentIndex++
            }
        }
    }

    fun onChangeAnswer() {
        val currentItem = currentItem
        currentItem?.let {
            userAnswers.remove(it.id)
        }
    }

    fun onPreviousClick() {
        currentIndex = (currentIndex - 1 + wasteItems.size) % wasteItems.size
    }

    fun onNextClick() {
        currentIndex = (currentIndex + 1) % wasteItems.size
    }

    fun onVerifyClick() {
        if (allAnswered) {
            showResults = true
        }
    }

    fun onDismissResults() {
        showResults = false
    }

    fun resetGame() {
        currentIndex = 0
        userAnswers.clear()
        showResults = false
    }

    fun isContainerSelected(category: WasteCategory): Boolean {
        val currentItem = currentItem
        return currentItem?.let { userAnswers[it.id] == category } ?: false
    }

    fun getCurrentAnswer(): WasteCategory? {
        val currentItem = currentItem
        return currentItem?.let { userAnswers[it.id] }
    }
}