package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.errenteriaapp.components.*
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SanMarkosekoGalderak(
    navController: NavController
) {
    var galderaIndex by remember { mutableIntStateOf(0) }
    var aukeraHautatua by remember { mutableIntStateOf(-1) }
    var erantzunZuzenak by remember { mutableIntStateOf(0) }
    val galderakErantzunda = remember { mutableStateListOf<Int>() }
    val erantzunak = remember { mutableStateMapOf<Int, Pair<Int, Boolean>>() }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showWrongDialog by remember { mutableStateOf(false) }

    // Galderas con las preguntas
    val galderak = remember {
        listOf(
            Triple(
                "Zertarako eraiki zen San Markos gotorlekua?",
                listOf(
                    "Erregeak bizitzeko",
                    "Lurraldea babesteko eta zaintzeko",
                    "Janaria gordetzeko"
                ),
                1  // La respuesta correcta es la B (índice 1)
            ),
            Triple(
                "Zein mendetan eraiki zen gotorlekua?",
                listOf(
                    "XIX. mendean",
                    "XXI. mendean",
                    "XVI. mendean"
                ),
                0  // La respuesta correcta es la A (índice 0)
            ),
            Triple(
                "Gaur egun, gotorlekua erabiltzen da...",
                listOf(
                    "Bisitak eta kultur jarduerak egiteko",
                    "Soldaduak bizitzeko",
                    "Armak gordetzeko"
                ),
                0  // La respuesta correcta es la A (índice 0)
            )
        ).shuffled()  // Preguntas en orden aleatorio
    }

    val (galderaText, aukerak, erantzunZuzena) = galderak[galderaIndex]

    // Usa tu componente GameResultDialogs
    GameResultDialogs(
        showSuccess = showSuccessDialog,
        showWrong = showWrongDialog,
        onDismissSuccess = { showSuccessDialog = false },
        onDismissWrong = { showWrongDialog = false },
        onSuccessButton = {
            showSuccessDialog = false
            navController.navigate("mapa_screen")
        },
        onWrongButton = {
            showWrongDialog = false
            // Reiniciar el cuestionario
            galderaIndex = 0
            aukeraHautatua = -1
            erantzunZuzenak = 0
            galderakErantzunda.clear()
            erantzunak.clear()
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabecera
            QuizHeader()

            Spacer(modifier = Modifier.height(60.dp))

            // Progreso visual
            ProgressIndicator(
                galderaIndex = galderaIndex,
                totalGalderak = galderak.size,
                erantzunak = erantzunak
            )

            Spacer(modifier = Modifier.height(7.dp))

            // Tarjeta de pregunta
            QuestionCard(
                galderaIndex = galderaIndex,
                galderaText = galderaText,
                aukerak = aukerak,
                erantzunZuzena = erantzunZuzena,
                aukeraHautatua = aukeraHautatua,
                galderakErantzunda = galderakErantzunda,
                erantzunak = erantzunak,
                onOptionSelected = { index ->
                    if (!galderakErantzunda.contains(galderaIndex)) {
                        aukeraHautatua = index
                        val correct = index == erantzunZuzena

                        erantzunak[galderaIndex] = Pair(index, correct)

                        if (correct) {
                            erantzunZuzenak++
                        }

                        galderakErantzunda.add(galderaIndex)
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Botón para continuar o terminar
            QuizNextButton(
                currentQuestionIndex = galderaIndex,
                totalQuestions = galderak.size,
                correctAnswers = erantzunZuzenak,
                isAnswered = galderakErantzunda.contains(galderaIndex),
                onNextClick = {
                    if (galderaIndex < galderak.size - 1) {
                        // Ir a la siguiente pregunta
                        galderaIndex++
                        aukeraHautatua = -1
                    } else {
                        // Verificar si tiene al menos 2 respuestas correctas
                        if (erantzunZuzenak >= 2) {
                            // Mostrar diálogo de éxito
                            showSuccessDialog = true
                        } else {
                            // Mostrar diálogo de fallo
                            showWrongDialog = true
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Información sobre los requisitos
            RequirementInfo(erantzunZuzenak = erantzunZuzenak)
        }
    }
}