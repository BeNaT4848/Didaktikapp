package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.errenteriaapp.components.ClickableTextFunction
import com.example.errenteriaapp.components.GameResultDialogs
import com.example.errenteriaapp.components.ParagraphCard
import com.example.errenteriaapp.components.textoBertsoa
import com.example.errenteriaapp.database.viewModel.PuntuakViewModel
import com.example.errenteriaapp.navigation.Routes

@Composable
@Suppress("UNUSED_PARAMETER")
fun BertsoJolasaScreen2(
    navController: NavController,
    puntuakViewModel: PuntuakViewModel = viewModel()
) {
    val totalItems = 5
    val requiredCorrect = 3
    val attempt = puntuakViewModel.attempt
    val showSuccess = remember { mutableStateOf(false) }
    val showWrong = remember { mutableStateOf(false) }

    fun handleAnswer(isCorrect: Boolean) {
        if (showSuccess.value || showWrong.value) return
        if (isCorrect) {
            puntuakViewModel.registerCorrect()
        }
        val answered = puntuakViewModel.registerAnswer()
        if (answered == totalItems) {
            if (puntuakViewModel.correctCount >= requiredCorrect) {
                showSuccess.value = true
            } else {
                showWrong.value = true
            }
        }
    }

    GameResultDialogs(
        showSuccess = showSuccess.value,
        showWrong = showWrong.value,
        onDismissSuccess = { showSuccess.value = false },
        onDismissWrong = { showWrong.value = false },
        onSuccessButton = {
            showSuccess.value = false
            puntuakViewModel.restartAttempt()
            navController.navigate(Routes.MAPA_SCREEN)
        },
        onWrongButton = {
            showWrong.value = false
            puntuakViewModel.restartAttempt()
            navController.navigate(Routes.BERTSOJOLASA2_SCREEN)
        }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        // más espacio entre cards
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            ParagraphCard(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                borderColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                textoBertsoa("Iparragirre abila dela")
                textoBertsoa("askori diot aditzen")
                textoBertsoa("eskola ona eta musika")
                ClickableTextFunction(
                    fulltext = "hori hoiekin _____.",
                    clickableword = "_____",
                    act = "zerbitzen",
                    bct = "serbitzen",
                    cct = "zerbitzaz",
                    correctAnswer = "serbitzen",
                    onCorrect = { handleAnswer(true) },
                    onWrong = { handleAnswer(false) },
                    onAnswered = {},
                    isLocked = showSuccess.value || showWrong.value,
                    attemptKey = attempt,
                    resetOnAttempt = true
                )
                textoBertsoa("ni ez nazu ibiltzen")
                ClickableTextFunction(
                    fulltext = "kantuz dirua _____.",
                    clickableword = "_____",
                    act = "biltzen",
                    bct = "biltzar",
                    cct = "biltzer",
                    correctAnswer = "biltzen",
                    onCorrect = { handleAnswer(true) },
                    onWrong = { handleAnswer(false) },
                    onAnswered = {},
                    isLocked = showSuccess.value || showWrong.value,
                    attemptKey = attempt,
                    resetOnAttempt = true
                )
                textoBertsoa("komeriante moduan")
                textoBertsoa("debalde festa preparatzen det")
                textoBertsoa("gogua dedan orduan.")
            }
        }
        item {
            ParagraphCard(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                borderColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                textoBertsoa("Eskola ona eta musika")
                textoBertsoa("bertsolaria gainera")
                ClickableTextFunction(
                    fulltext = "gu ere zerbait izango _____.",
                    clickableword = "_____",
                    act = "gera",
                    bct = "gara",
                    cct = "geure",
                    correctAnswer = "gera",
                    onCorrect = { handleAnswer(true) },
                    onWrong = { handleAnswer(false) },
                    onAnswered = {},
                    isLocked = showSuccess.value || showWrong.value,
                    attemptKey = attempt,
                    resetOnAttempt = true
                )
                textoBertsoa("horla hornitzen bagera")
                ClickableTextFunction(
                    fulltext = "atoz gure _____.",
                    clickableword = "_____",
                    act = "kalera",
                    bct = "etxera",
                    cct = "plazara",
                    correctAnswer = "kalera",
                    onCorrect = { handleAnswer(true) },
                    onWrong = { handleAnswer(false) },
                    onAnswered = {},
                    isLocked = showSuccess.value || showWrong.value,
                    attemptKey = attempt,
                    resetOnAttempt = true
                )
                textoBertsoa("baserritar legera")
                textoBertsoa("musika hoiek utzita")
                textoBertsoa("Errenterian bizi naiz eta")
                ClickableTextFunction(
                    fulltext = "egin zaidazu _____.",
                    clickableword = "_____",
                    act = "bisita",
                    bct = "bisigu",
                    cct = "bisitan",
                    correctAnswer = "bisita",
                    onCorrect = { handleAnswer(true) },
                    onWrong = { handleAnswer(false) },
                    onAnswered = {},
                    isLocked = showSuccess.value || showWrong.value,
                    attemptKey = attempt,
                    resetOnAttempt = true
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun BertsoJolasa2Preview() {
    BertsoJolasaScreen2(rememberNavController())
}