// app/src/main/java/com/example/errenteriaapp/database/viewModel/PapresaViewModel.kt
package com.example.errenteriaapp.database.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.errenteriaapp.R
import com.example.errenteriaapp.classes.WasteCategory
import com.example.errenteriaapp.classes.WasteItem
import kotlin.math.ceil
import kotlin.random.Random

class PapresaViewModel : ViewModel() {

    private val successThreshold = 0.8

    val wasteItems = mutableStateListOf<WasteItem>()

    var currentIndex by mutableStateOf(0)
        private set

    val userAnswers = mutableMapOf<Int, WasteCategory>()

    var showResults by mutableStateOf(false)
        private set
    var showSuccessDialog by mutableStateOf(false)
        private set
    var showWrongDialog by mutableStateOf(false)
        private set

    val allAnswered: Boolean
        get() = wasteItems.all { userAnswers.containsKey(it.id) }

    val answeredCount: Int
        get() = userAnswers.size

    val totalCount: Int
        get() = wasteItems.size

    val currentItem: WasteItem?
        get() = wasteItems.getOrNull(currentIndex)

    init {
        generarOrdenAleatorio()
    }

    private fun generarOrdenAleatorio() {
        val itemsOriginales = listOf(
            WasteItem(1, "Ur botila", WasteCategory.YELLOW, R.drawable.botella_agua),
            WasteItem(2, "Freskagarri lata", WasteCategory.YELLOW, R.drawable.lata_refresco),
            WasteItem(3, "Patata poltsa", WasteCategory.YELLOW, R.drawable.bolsa_patatas),
            WasteItem(4, "Plastikozko tapoia", WasteCategory.YELLOW, R.drawable.tapones_plastico),
            WasteItem(5, "Iogurta", WasteCategory.YELLOW, R.drawable.yogurt),
            WasteItem(6, "Zereal kutxa", WasteCategory.BLUE, R.drawable.caja_cereales),
            WasteItem(7, "Egunkaria", WasteCategory.BLUE, R.drawable.periodico),
            WasteItem(8, "Paper koadernoa", WasteCategory.BLUE, R.drawable.cuaderno_papel),
            WasteItem(9, "Kartoi hodia", WasteCategory.BLUE, R.drawable.tubo_carton),
            WasteItem(10, "Gutun-azala", WasteCategory.BLUE, R.drawable.sobre),
            WasteItem(11, "Fruta azala", WasteCategory.BROWN, R.drawable.piel_fruta),
            WasteItem(12, "Barazki hondarrak", WasteCategory.BROWN, R.drawable.restos_verduras),
            WasteItem(13, "Ogia", WasteCategory.BROWN, R.drawable.pan),
            WasteItem(14, "Hezurrak", WasteCategory.BROWN, R.drawable.huesos),
            WasteItem(15, "Janari hondarrak", WasteCategory.BROWN, R.drawable.sobras),
            WasteItem(16, "Txikleak", WasteCategory.BLACK, R.drawable.chicle),
            WasteItem(17, "Zigarro puntak", WasteCategory.BLACK, R.drawable.colillas),
            WasteItem(18, "Konpresa", WasteCategory.BLACK, R.drawable.gorro_sanitario),
            WasteItem(19, "Tiritak", WasteCategory.BLACK, R.drawable.tiritas),
            WasteItem(20, "Pixoihala", WasteCategory.BLACK, R.drawable.panal)
        )

        wasteItems.clear()
        wasteItems.addAll(itemsOriginales.shuffled(Random))
    }

    fun onContainerClick(category: WasteCategory) {
        currentItem?.let {
            userAnswers[it.id] = category
            currentIndex = (currentIndex + 1) % wasteItems.size
        }
    }

    fun onPreviousClick() {
        currentIndex = (currentIndex - 1 + wasteItems.size) % wasteItems.size
    }

    fun onNextClick() {
        currentIndex = (currentIndex + 1) % wasteItems.size
    }

    fun onVerifyClick() {
        if (!allAnswered) return
        val correctAnswers = wasteItems.count { userAnswers[it.id] == it.correctCategory }
        val requiredCorrect = ceil(wasteItems.size * successThreshold).toInt().coerceAtLeast(1)

        showResults = true
        if (correctAnswers >= requiredCorrect) {
            showSuccessDialog = true
            showWrongDialog = false
        } else {
            showWrongDialog = true
            showSuccessDialog = false
        }
    }

    fun onDismissResults() {
        showResults = false
    }

    fun dismissSuccessDialog() {
        showSuccessDialog = false
    }

    fun dismissWrongDialog() {
        showWrongDialog = false
    }

    fun onSuccessDialogConfirmed() {
        showSuccessDialog = false
    }

    fun onWrongDialogRetry() {
        showWrongDialog = false
        resetGame()
    }

    fun resetGame() {
        generarOrdenAleatorio()
        currentIndex = 0
        userAnswers.clear()
        showResults = false
        showSuccessDialog = false
        showWrongDialog = false
    }
}
