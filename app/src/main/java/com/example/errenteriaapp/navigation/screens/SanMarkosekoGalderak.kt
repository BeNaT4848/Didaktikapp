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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SanMarkosekoGalderak(
    navController: NavController,
    userName: String?,
    viewModel: SanMarkosViewModel
) {
    val context = LocalContext.current
    val sessionPrefs = remember { context.getSharedPreferences("session", android.content.Context.MODE_PRIVATE) }
    val effectiveUserName = userName ?: sessionPrefs.getString("active_user_name", null)
    val progressRepo = remember(effectiveUserName) { KokapenaProgressRepository(context, effectiveUserName ?: "default") }

    LaunchedEffect(effectiveUserName) {
        effectiveUserName?.let {
            viewModel.setUsuario(it)
        }
    }
    // Observar el estado del ViewModel
    val galderaIndex by remember { derivedStateOf { viewModel.galderaIndex } }
    val aukeraHautatua by remember { derivedStateOf { viewModel.aukeraHautatua } }
    val erantzunZuzenak by remember { derivedStateOf { viewModel.erantzunZuzenak } }
    val puntuacionTotal by remember { derivedStateOf { viewModel.puntuacionTotal } }
    val galderakErantzunda by remember { derivedStateOf { viewModel.galderakErantzunda } }
    val erantzunak by remember { derivedStateOf { viewModel.erantzunak } }
    val showSuccessDialog by remember { derivedStateOf { viewModel.showSuccessDialog } }
    val showWrongDialog by remember { derivedStateOf { viewModel.showWrongDialog } }

    val currentPregunta = viewModel.currentPregunta

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
            // Cabecera
            QuizHeader()

            Spacer(modifier = Modifier.height(30.dp))


            // Progreso visual
            ProgressIndicator(
                galderaIndex = galderaIndex,
                totalGalderak = viewModel.galderak.size,
                erantzunak = erantzunak
            )

            Spacer(modifier = Modifier.height(7.dp))

            // Tarjeta de pregunta - USANDO opcionesMezcladas
            QuestionCard(
                galderaIndex = galderaIndex,
                galderaText = currentPregunta.texto,
                aukerak = currentPregunta.opcionesMezcladas,  // Usar opciones mezcladas
                erantzunZuzena = currentPregunta.respuestaCorrectaMezclada,  // Usar respuesta correcta en opciones mezcladas
                aukeraHautatua = aukeraHautatua,
                galderakErantzunda = galderakErantzunda,
                erantzunak = erantzunak,
                onOptionSelected = { index ->
                    viewModel.onOptionSelected(index)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Botón para continuar o terminar
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

            // Información sobre los requisitos
            RequirementInfo(erantzunZuzenak = erantzunZuzenak)
        }
    }

    // Diálogo de éxito
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
                // Encadenado automático: al terminar SanMarkos, entra al Crucigrama
                navController.navigate(Routes.CRUCIGRAMA_SCREEN)
            },
            onWrongButton = { }
        )
    }

    // Diálogo de error
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