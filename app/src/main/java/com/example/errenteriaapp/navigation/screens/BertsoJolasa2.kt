package com.example.errenteriaapp.navigation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.errenteriaapp.components.ClickableTextFunction
import com.example.errenteriaapp.components.ParagraphCard
import com.example.errenteriaapp.components.textoBertsoa
import com.example.errenteriaapp.components.GameResultDialogs
import com.example.errenteriaapp.database.viewModel.BertsoViewModel
import com.example.errenteriaapp.navigation.Routes
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.errenteriaapp.progress.KokapenaProgressRepository

@Composable
@Suppress("UNUSED_PARAMETER")
fun BertsoJolasaScreen2(
    navController: NavController,
    userName: String?,
    viewModel: BertsoViewModel
) {
    val context = LocalContext.current
    val sessionPrefs = remember { context.getSharedPreferences("session", android.content.Context.MODE_PRIVATE) }
    val effectiveUserName = userName ?: sessionPrefs.getString("active_user_name", null)

    val progressRepo = remember(effectiveUserName) {
        KokapenaProgressRepository(context, effectiveUserName ?: "default")
    }
    val attempt = viewModel.attempt
    val showSuccess = viewModel.showSuccessDialog
    val showWrong = viewModel.showWrongDialog

    LaunchedEffect(effectiveUserName) {
        effectiveUserName?.let { viewModel.setUsuario(it) }
    }

    fun handleProgress() {
        viewModel.registerAnswer()
        viewModel.checkBertso2Completion()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 16.dp)
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
                    onCorrect = { viewModel.registerCorrect() },
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
                    onCorrect = { viewModel.registerCorrect() },
                    onAnswered = { handleProgress() },
                    attemptKey = attempt,
                    resetOnAttempt = true
                )
                textoBertsoa("komeriante moduan")
                textoBertsoa("debalde festa preparatzen det")
                textoBertsoa("gogua dedan orduan.")
            }
        }

        // Segunda card
        item {
            ParagraphCard(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                borderColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
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
                    onCorrect = { viewModel.registerCorrect() },
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
                    onCorrect = { viewModel.registerCorrect() },
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
                    onCorrect = { viewModel.registerCorrect() },
                    onAnswered = { handleProgress() },
                    attemptKey = attempt,
                    resetOnAttempt = true
                )
            }
        }
    }

    // Diálogo de éxito
    if (showSuccess) {
        GameResultDialogs(
            showSuccess = true,
            showWrong = false,
            onDismissSuccess = { viewModel.dismissSuccessDialog() },
            onDismissWrong = { },
            onSuccessButton = {
                viewModel.dismissSuccessDialog()
                progressRepo.markCompleted(Routes.BERTSOJOLASA2_SCREEN)
                navController.navigate(Routes.GPS_SCREEN)
            },
            onWrongButton = { }
        )
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

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun BertsoJolasa2Preview() {
    val viewModel = BertsoViewModel(null)
    BertsoJolasaScreen2(rememberNavController(), userName = "User", viewModel = viewModel)
}
