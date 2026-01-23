// app/src/main/java/com/example/errenteriaapp/navigation/screens/BertsoJolasaScreen.kt
package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.errenteriaapp.components.textoBertsoa
import com.example.errenteriaapp.components.ClickableTextFunction
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.errenteriaapp.components.ParagraphCard
import com.example.errenteriaapp.database.viewModel.BertsoViewModel
import com.example.errenteriaapp.navigation.Routes
import com.example.errenteriaapp.components.GameResultDialogs

@Composable
fun BertsoJolasaScreen(
    navController: NavController,
    userName: String?,
    viewModel: BertsoViewModel
) {
    val attempt = viewModel.attempt
    val hasNavigated = viewModel.hasNavigated
    val showSuccess = viewModel.showSuccessDialog
    val showWrong = viewModel.showWrongDialog

    fun handleProgress() {
        viewModel.registerAnswer()
        viewModel.checkBertso1Completion {
            navController.navigate(Routes.BERTSOJOLASA2_SCREEN)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 60.dp)
    ) {
        item {
            ParagraphCard(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                borderColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                textoBertsoa("Milla zortziehun eta hirurogeita")
                ClickableTextFunction(
                    fulltext = "hamalau urte _____.",
                    clickableword = "_____",
                    act = "abenduan",
                    bct = "urrian",
                    cct = "martxoan",
                    correctAnswer = "urrian",
                    onCorrect = { viewModel.registerCorrect() },
                    onAnswered = { handleProgress() },
                    isLocked = hasNavigated,
                    attemptKey = attempt,
                    resetOnAttempt = true
                )
                textoBertsoa(
                    textobertsoa = "lehenengo plazan kantatu nuen\n" +
                            "nik Ernaniko lurrean,\n" +
                            "San Antonio deitzen diogun"
                )
                ClickableTextFunction(
                    fulltext = "ermita baten _____.",
                    clickableword = "_____",
                    act = "aurrian",
                    bct = "barruan",
                    cct = "atzean",
                    correctAnswer = "aurrian",
                    onCorrect = { viewModel.registerCorrect() },
                    onAnswered = { handleProgress() },
                    isLocked = hasNavigated,
                    attemptKey = attempt,
                    resetOnAttempt = true
                )
                textoBertsoa("lengo ohitura zaharrean.")
            }
        }
        item {
            ParagraphCard(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                borderColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            ) {
                textoBertsoa(
                    textobertsoa = "Joxe Migelek atera zuen\n" +
                            "oso izketa leguna:\n" +
                            "«Hau da, gazteak, prezisamente"
                )
                ClickableTextFunction(
                    fulltext = "guk egin behar _____.",
                    clickableword = "_____",
                    act = "doguna",
                    bct = "dituena",
                    cct = "zerbait",
                    correctAnswer = "doguna",
                    onCorrect = { viewModel.registerCorrect() },
                    onAnswered = { handleProgress() },
                    isLocked = hasNavigated,
                    attemptKey = attempt,
                    resetOnAttempt = true
                )
                textoBertsoa("altxa dezagun San Antonio.")
                ClickableTextFunction(
                    fulltext = "gaur da beraren _____!",
                    clickableword = "_____!",
                    act = "izuna",
                    bct = "ohorea",
                    cct = "eguna",
                    correctAnswer = "eguna",
                    onCorrect = { viewModel.registerCorrect() },
                    onAnswered = { handleProgress() },
                    isLocked = hasNavigated,
                    attemptKey = attempt,
                    resetOnAttempt = true
                )
                textoBertsoa(
                    textobertsoa = "Gaur Goiatz Txikin dago itxututa\n" +
                            "orduko nire laguna."
                )
            }
        }

        item {
            ParagraphCard(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                borderColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            ) {
                textoBertsoa("(…)")
                textoBertsoa("Hirurogeita hamar bat urte")
                ClickableTextFunction(
                    fulltext = "badut _____.",
                    clickableword = "_____",
                    act = "buruan",
                    bct = "bizkarrian",
                    cct = "bularrean",
                    correctAnswer = "bizkarrian",
                    onCorrect = { viewModel.registerCorrect() },
                    onAnswered = { handleProgress() },
                    isLocked = hasNavigated,
                    attemptKey = attempt,
                    resetOnAttempt = true
                )
                textoBertsoa("kargamenturik txarrena hau da,")
                ClickableTextFunction(
                    fulltext = "ezin utzi _____.",
                    clickableword = "_____",
                    act = "bidean",
                    bct = "etxean",
                    cct = "bazterrian",
                    correctAnswer = "bazterrian",
                    onCorrect = { viewModel.registerCorrect() },
                    onAnswered = { handleProgress() },
                    isLocked = hasNavigated,
                    attemptKey = attempt,
                    resetOnAttempt = true
                )
                textoBertsoa(
                    textobertsoa = "anka batetik kojoka nabil,\n" +
                            "reuma daukat iztarrean,\n" +
                            "baina baditut laguntzaileak,"
                )
                ClickableTextFunction(
                    fulltext = "ez nago modu _____.",
                    clickableword = "_____",
                    act = "onean",
                    bct = "txarrian",
                    cct = "erdian",
                    correctAnswer = "txarrian",
                    onCorrect = { viewModel.registerCorrect() },
                    onAnswered = { handleProgress() },
                    isLocked = hasNavigated,
                    attemptKey = attempt,
                    resetOnAttempt = true
                )
            }
        }
    }

    // Diálogo de error
    if (showWrong) {
        GameResultDialogs(
            showSuccess = false,
            showWrong = true,
            onDismissSuccess = { },
            onDismissWrong = { viewModel.dismissWrongDialog() },
            onSuccessButton = { },
            onWrongButton = {
                viewModel.dismissWrongDialog()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BertsoJolasaPreview() {
    val viewModel: BertsoViewModel = viewModel()
    BertsoJolasaScreen(navController = rememberNavController(), userName = "User", viewModel = viewModel)
}