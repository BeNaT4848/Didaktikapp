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
import com.example.errenteriaapp.components.textoBertsoa
import com.example.errenteriaapp.components.ClickableTextFunction
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.errenteriaapp.components.viewModel.PuntuakViewModel
import com.example.errenteriaapp.navigation.Routes
import com.example.errenteriaapp.components.showFeedbackToast

@Composable
fun BertsoJolasaScreen(
    navController: NavController,
    puntuakViewModel: PuntuakViewModel = viewModel()
) {
    val context = LocalContext.current
    val hasNavigated = puntuakViewModel.hasNavigated
    val totalItems = 7
    val attempt = puntuakViewModel.attempt

    fun handleProgress(onAfterAnswer: (() -> Unit)? = null) {
        val answered = puntuakViewModel.registerAnswer()
        onAfterAnswer?.invoke()
        if (answered == totalItems) {
            if (puntuakViewModel.correctCount > 4 && !hasNavigated) {
                puntuakViewModel.markNavigated()
                navController.navigate(Routes.BERTSOJOLASA2_SCREEN)
            } else if (!hasNavigated) {
                showFeedbackToast(context, "Saiatu berriro!", false)
                puntuakViewModel.restartAttempt()
            }
        }
    }

    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            Spacer(modifier = Modifier.padding(6.dp))
            textoBertsoa(
                textobertsoa = "Milla zortziehun eta hirurogeita"
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "hamalau urte _____.",
                clickableword = "_____",
                act = "abenduan",
                bct = "urrian",
                cct = "martxoan",
                colorBox = 0xFFFFC1C1,
                correctAnswer = "urrian",
                onCorrect = { puntuakViewModel.registerCorrect() },
                onAnswered = { handleProgress() },
                isLocked = hasNavigated,
                attemptKey = attempt,
                resetOnAttempt = true
            )
        }
        item {
            textoBertsoa(
                textobertsoa = "lehenengo plazan kantatu nuen\n" +
                        "nik Ernaniko lurrean,\n" +
                        "San Antonio deitzen diogun"
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "ermita baten _____.",
                clickableword = "_____",
                act = "aurrian",
                bct = "barruan",
                cct = "atzean",
                colorBox = 0xFFBDFFC0,
                correctAnswer = "aurrian",
                onCorrect = { puntuakViewModel.registerCorrect() },
                onAnswered = { handleProgress() },
                isLocked = hasNavigated,
                attemptKey = attempt,
                resetOnAttempt = true
            )
        }
        item {
            textoBertsoa(
                textobertsoa = "lengo ohitura zaharrean.\n" +
                        "\n" + "\n" +
                        "Joxe Migelek atera zuen\n" +
                        "oso izketa leguna:\n" +
                        "«Hau da, gazteak, prezisamente"
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "guk egin behar _____.",
                clickableword = "_____",
                act = "deguna",
                bct = "daukaguna",
                cct = "zerbait",
                colorBox = 0xFFBDCEFF,
                correctAnswer = "deguna",
                onCorrect = { puntuakViewModel.registerCorrect() },
                onAnswered = { handleProgress() },
                isLocked = hasNavigated,
                attemptKey = attempt,
                resetOnAttempt = true
            )
        }
        item {
            textoBertsoa(
                textobertsoa = "altxa dezagun San Antonio."
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "gaur da beraren _____!",
                clickableword = "_____!",
                act = "ospakizuna",
                bct = "ohorea",
                cct = "eguna",
                colorBox = 0xFFC5904E,
                correctAnswer = "eguna",
                onCorrect = { puntuakViewModel.registerCorrect() },
                onAnswered = { handleProgress() },
                isLocked = hasNavigated,
                attemptKey = attempt,
                resetOnAttempt = true
            )
        }
        item {
            textoBertsoa(
                textobertsoa = "Gaur Goiatz Txikin dago itxututa\n" +
                    "orduko nire laguna."
            )
            Spacer(modifier = Modifier.padding(10.dp))
            textoBertsoa(
                textobertsoa = "(…)"
            )
            Spacer(modifier = Modifier.padding(10.dp))
            textoBertsoa(
                textobertsoa = "Hirurogeita hamar bat urte"
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "badut _____.",
                clickableword = "_____",
                act = "sorbaldan",
                bct = "bizkarrian",
                cct = "bularrean",
                colorBox = 0xFFB6B6B6,
                correctAnswer = "bizkarrian",
                onCorrect = { puntuakViewModel.registerCorrect() },
                onAnswered = { handleProgress() },
                isLocked = hasNavigated,
                attemptKey = attempt,
                resetOnAttempt = true
            )
        }
        item {
            textoBertsoa(
                textobertsoa = "kargamenturik txarrena hau da,"
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "ezin utzi _____.",
                clickableword = "_____",
                act = "bidean",
                bct = "kanpoan",
                cct = "bazterrian",
                colorBox = 0xFFE0FF6F,
                correctAnswer = "bazterrian",
                onCorrect = { puntuakViewModel.registerCorrect() },
                onAnswered = { handleProgress() },
                isLocked = hasNavigated,
                attemptKey = attempt,
                resetOnAttempt = true
            )
        }
        item {
            textoBertsoa(
                textobertsoa = "anka batetik kojoka nabil,\n" +
                        "reuma daukat iztarrean,\n" +
                        "baina baditut laguntzaileak,"
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "ez nago modu _____.",
                clickableword = "_____",
                act = "onean",
                bct = "txarrian",
                cct = "erdian",
                colorBox = 0xFFFFE4C4,
                correctAnswer = "txarrian",
                onCorrect = { puntuakViewModel.registerCorrect() },
                onAnswered = { handleProgress() },
                isLocked = hasNavigated,
                attemptKey = attempt,
                resetOnAttempt = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BertsoJolasaPreview() {
    BertsoJolasaScreen(navController = rememberNavController()
    )
}
