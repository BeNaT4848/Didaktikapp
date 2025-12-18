package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.errenteriaapp.components.ClickableTextFunction
import com.example.errenteriaapp.components.ParagraphCard
import com.example.errenteriaapp.components.showFeedbackToast
import com.example.errenteriaapp.components.textoBertsoa
import com.example.errenteriaapp.database.viewModel.PuntuakViewModel

@Composable
fun BertsoJolasaScreen2(
    navController: NavController,
    puntuakViewModel: PuntuakViewModel = viewModel()
) {
    val context = LocalContext.current
    val totalItems = 5
    val attempt = puntuakViewModel.attempt

    fun handleProgress(onAfterAnswer: (() -> Unit)? = null) {
        val answered = puntuakViewModel.registerAnswer()
        onAfterAnswer?.invoke()
        if (answered == totalItems) {
            if (puntuakViewModel.correctCount > 3) {
                showFeedbackToast(context, "Zorionak! Bertsoa osatu duzu", true)
                puntuakViewModel.restartAttempt()
            } else {
                showFeedbackToast(context, "Saiatu berriro!", false)
                puntuakViewModel.restartAttempt()
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        item {
            ParagraphCard(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                borderColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
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
                    onCorrect = { puntuakViewModel.registerCorrect() },
                    onAnswered = { handleProgress() },
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
                    onCorrect = { puntuakViewModel.registerCorrect() },
                    onAnswered = { handleProgress() },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
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
                    onCorrect = { puntuakViewModel.registerCorrect() },
                    onAnswered = { handleProgress() },
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
                    onCorrect = { puntuakViewModel.registerCorrect() },
                    onAnswered = { handleProgress() },
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
                    onCorrect = { puntuakViewModel.registerCorrect() },
                    onAnswered = { handleProgress() },
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