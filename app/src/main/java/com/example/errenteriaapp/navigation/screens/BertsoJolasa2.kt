package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.errenteriaapp.components.ClickableTextFunction
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

    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            Spacer(modifier = Modifier.padding(6.dp))
            textoBertsoa("Iparragirre abila dela")
            textoBertsoa("askori diot aditzen")
            textoBertsoa("eskola ona eta musika")
        }
        item {
            ClickableTextFunction(
                fulltext = "hori hoiekin _____.",
                clickableword = "_____",
                act = "zerbitzen",
                bct = "serbitzen",
                cct = "zerbitzaz",
                colorBox = 0xFFF9D9D9,
                correctAnswer = "serbitzen",
                onCorrect = { puntuakViewModel.registerCorrect() },
                onAnswered = { handleProgress() },
                attemptKey = attempt,
                resetOnAttempt = true
            )
        }
        item {
            textoBertsoa("ni ez nazu ibiltzen")
        }
        item {
            ClickableTextFunction(
                fulltext = "kantuz dirua _____.",
                clickableword = "_____",
                act = "biltzen",
                bct = "biltzar",
                cct = "biltzer",
                colorBox = 0xFFE0ECFF,
                correctAnswer = "biltzen",
                onCorrect = { puntuakViewModel.registerCorrect() },
                onAnswered = { handleProgress() },
                attemptKey = attempt,
                resetOnAttempt = true
            )
        }
        item {
            textoBertsoa("komeriante moduan")
            textoBertsoa("debalde festa preparatzen det")
            textoBertsoa("gogua dedan orduan.")
            Spacer(modifier = Modifier.padding(6.dp))
            textoBertsoa("Eskola ona eta musika")
            textoBertsoa("bertsolaria gainera")
        }
        item {
            ClickableTextFunction(
                fulltext = "gu ere zerbait izango _____.",
                clickableword = "_____",
                act = "gera",
                bct = "gara",
                cct = "geure",
                colorBox = 0xFFD1F5D3,
                correctAnswer = "gera",
                onCorrect = { puntuakViewModel.registerCorrect() },
                onAnswered = { handleProgress() },
                attemptKey = attempt,
                resetOnAttempt = true
            )
        }
        item {
            textoBertsoa("horla hornitzen bagera")
        }
        item {
            ClickableTextFunction(
                fulltext = "atoz gure _____.",
                clickableword = "_____",
                act = "kalera",
                bct = "etxera",
                cct = "plazara",
                colorBox = 0xFFFFEDC2,
                correctAnswer = "kalera",
                onCorrect = { puntuakViewModel.registerCorrect() },
                onAnswered = { handleProgress() },
                attemptKey = attempt,
                resetOnAttempt = true
            )
        }
        item {
            textoBertsoa("baserritar legera")
            textoBertsoa("musika hoiek utzita")
            textoBertsoa("Errenterian bizi naiz eta")
        }
        item {
            ClickableTextFunction(
                fulltext = "egin zaidazu _____.",
                clickableword = "_____",
                act = "bisita",
                bct = "bisigu",
                cct = "bisitan",
                colorBox = 0xFFE6CCFF,
                correctAnswer = "bisita",
                onCorrect = { puntuakViewModel.registerCorrect() },
                onAnswered = { handleProgress() },
                attemptKey = attempt,
                resetOnAttempt = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BertsoJolasa2Preview() {
    BertsoJolasaScreen2(navController = rememberNavController())
}