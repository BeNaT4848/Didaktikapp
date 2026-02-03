// app/src/main/java/com/example/errenteriaapp/navigation/screens/SanMarkosekoGalderak.kt
package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.errenteriaapp.components.*
import com.example.errenteriaapp.database.viewModel.SanMarkosViewModel
import com.example.errenteriaapp.navigation.Routes
import com.example.errenteriaapp.progress.KokapenaProgressRepository

/**
 * San Markos galdetegiaren pantaila nagusia.
 * San Markosen museoari buruzko galderak erakusten eta ebazten ditu.
 *
 * @param navController Nabigazio kontroladorea
 * @param userName Erabiltzaile izena (hautazkoa)
 * @param viewModel San Markos galdetegiaren ViewModel
 *
 * @see SanMarkosViewModel
 * @see KokapenaProgressRepository
 * @see QuizHeader
 * @see ProgressIndicator
 * @see QuestionCard
 * @see QuizNextButton
 * @see RequirementInfo
 * @see GameResultDialogs
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SanMarkosekoGalderak(
    navController: NavController,
    userName: String?,
    viewModel: SanMarkosViewModel
) {
    // Testuingurua eta erabiltzailearen datuak lortu
    val context = LocalContext.current
    val sessionPrefs = remember { context.getSharedPreferences("session", android.content.Context.MODE_PRIVATE) }
    val effectiveUserName = userName ?: sessionPrefs.getString("active_user_name", null)
    val progressRepo = remember(effectiveUserName) {
        KokapenaProgressRepository(context, effectiveUserName ?: "default")
    }

    // Erabiltzailea ViewModel-ean ezarri
    LaunchedEffect(effectiveUserName) {
        effectiveUserName?.let {
            viewModel.setUsuario(it)
        }
    }

    // ViewModel-eko egoerak behatu (derivedStateOf erabiliz eraginkortasuna hobetzeko)
    val galderaIndex by remember { derivedStateOf { viewModel.galderaIndex } }
    val aukeraHautatua by remember { derivedStateOf { viewModel.aukeraHautatua } }
    val erantzunZuzenak by remember { derivedStateOf { viewModel.erantzunZuzenak } }
    val puntuacionTotal by remember { derivedStateOf { viewModel.puntuacionTotal } }
    val galderakErantzunda by remember { derivedStateOf { viewModel.galderakErantzunda } }
    val erantzunak by remember { derivedStateOf { viewModel.erantzunak } }
    val showSuccessDialog by remember { derivedStateOf { viewModel.showSuccessDialog } }
    val showWrongDialog by remember { derivedStateOf { viewModel.showWrongDialog } }

    // Uneko galdera lortu
    val currentPregunta = viewModel.currentPregunta

    // Pantaila nagusiaren egitura
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Goiburu atala
            QuizHeader()

            Spacer(modifier = Modifier.height(30.dp))

            // Aurrerapenaren ikusizko adierazlea
            ProgressIndicator(
                galderaIndex = galderaIndex,
                totalGalderak = viewModel.galderak.size,
                erantzunak = erantzunak
            )

            Spacer(modifier = Modifier.height(7.dp))

            // Galdera txartela - opcionesMezcladas erabiliz
            QuestionCard(
                galderaIndex = galderaIndex,
                galderaText = currentPregunta.texto,
                aukerak = currentPregunta.opcionesMezcladas,  // Nahastutako aukerak erabili
                erantzunZuzena = currentPregunta.respuestaCorrectaMezclada,  // Zuzena nahastutako aukeren artean
                aukeraHautatua = aukeraHautatua,
                galderakErantzunda = galderakErantzunda,
                erantzunak = erantzunak,
                onOptionSelected = { index ->
                    viewModel.onOptionSelected(index)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Jarraitu edo amaitzeko botoia
            QuizNextButton(
                currentQuestionIndex = galderaIndex,
                totalQuestions = viewModel.galderak.size,
                correctAnswers = erantzunZuzenak,
                isAnswered = galderakErantzunda.contains(galderaIndex),
                onNextClick = {
                    viewModel.onNextQuestion()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Baldintzei buruzko informazioa
            RequirementInfo(erantzunZuzenak = erantzunZuzenak)
        }
    }

    // Arrakastaren elkarrizketa
    if (showSuccessDialog) {
        GameResultDialogs(
            showSuccess = true,
            showWrong = false,
            onDismissSuccess = {
                viewModel.dismissSuccessDialog()
            },
            onDismissWrong = { },
            onSuccessButton = {
                viewModel.dismissSuccessDialog()
                progressRepo.markCompleted(Routes.SANMARKOS_SCREEN)
                // KATEATZE AUTOMATIKOA: SanMarkos amaitutakoan, Crucigrama-ra joan
                navController.navigate(Routes.CRUCIGRAMA_SCREEN)
            },
            onWrongButton = { }
        )
    }

    // Akatsaren elkarrizketa
    if (showWrongDialog) {
        GameResultDialogs(
            showSuccess = false,
            showWrong = true,
            onDismissSuccess = { },
            onDismissWrong = { viewModel.dismissWrongDialog() },
            onSuccessButton = { },
            onWrongButton = {
                viewModel.dismissWrongDialog()
                viewModel.resetGame()
            }
        )
    }
}