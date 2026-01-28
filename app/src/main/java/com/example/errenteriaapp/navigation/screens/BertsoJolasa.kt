package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.errenteriaapp.components.*
import com.example.errenteriaapp.database.viewModel.BertsoViewModel
import com.example.errenteriaapp.navigation.Routes

@Composable
fun BertsoJolasaScreen(
    navController: NavController,
    userName: String?,
    viewModel: BertsoViewModel
) {
    val attempt = viewModel.attempt
    val hasNavigated = viewModel.hasNavigated
    val showWrong = viewModel.showWrongDialog

    fun handleProgress() {
        viewModel.registerAnswer()
        viewModel.checkBertso1Completion {
            navController.navigate(Routes.BERTSOJOLASA2_SCREEN)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            // Título principal
            item {
                Text(
                    text = "📝 Bertso Jolasa",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Primera card
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
                        textobertsoa = "lehenengo plazan kantatu nuen\nnik Ernaniko lurrean,\nSan Antonio deitzen diogun"
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

            // Segunda card
            item {
                ParagraphCard(
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    borderColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    textoBertsoa(
                        textobertsoa = "Joxe Migelek atera zuen\noso izketa leguna:\n«Hau da, gazteak, prezisamente"
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
                        textobertsoa = "Gaur Goiatz Txikin dago itxututa\norduko nire laguna."
                    )
                }
            }

            // Tercera card
            item {
                ParagraphCard(
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    borderColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
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
                        textobertsoa = "anka batetik kojoka nabil,\nreuma daukat iztarrean,\nbaina baditut laguntzaileak,"
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
                onWrongButton = { viewModel.dismissWrongDialog() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BertsoJolasaPreview() {
    val viewModel: BertsoViewModel = viewModel()
    BertsoJolasaScreen(navController = rememberNavController(), userName = "User", viewModel = viewModel)
}
