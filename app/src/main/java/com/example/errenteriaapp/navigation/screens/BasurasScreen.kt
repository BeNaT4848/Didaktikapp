package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.errenteriaapp.components.*

import com.example.errenteriaapp.components.video.InstruccionesVideoPapresa

import com.example.errenteriaapp.components.video.VideoDialogoa
import com.example.errenteriaapp.database.viewModel.PapresaViewModel
import com.example.errenteriaapp.navigation.Routes


@Composable
fun PapresaScreen(
    navController: NavController,
    userName: String?,
    viewModel: PapresaViewModel = viewModel()
) {
    val currentIndex = viewModel.currentIndex
    val allAnswered = viewModel.allAnswered
    val answeredCount = viewModel.answeredCount
    val showResults = viewModel.showResults
    val showSuccess = viewModel.showSuccessDialog
    val showWrong = viewModel.showWrongDialog
    val hasPassed = viewModel.hasPassed

    // Estados para controlar el flujo estricto
    var showVideoInstructionDialog by remember { mutableStateOf(false) }
    var showStrictVideoDialog by remember { mutableStateOf(false) }
    var hasWatchedVideo by remember { mutableStateOf(false) }

    // Lógica CORREGIDA para mostrar instrucción del video solo si APRUEBA
    LaunchedEffect(showResults) {
        if (showResults && hasPassed && !showVideoInstructionDialog && !hasWatchedVideo) {
            // Solo mostrar instrucción del video si APRUEBA (hasPassed = true)
            showVideoInstructionDialog = true
        }
    }

    // Si suspende, muestra diálogo de error inmediatamente (ya lo maneja el viewModel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PapresaTitle()
        ProgressCounter(currentIndex = currentIndex, totalItems = viewModel.totalCount)
        PhotoCarousel(
            wasteItems = viewModel.wasteItems,
            currentIndex = currentIndex,
            userAnswers = viewModel.userAnswers,
            onPreviousClick = viewModel::onPreviousClick,
            onNextClick = viewModel::onNextClick
        )
        InstructionText()
        WasteContainersRow(
            currentWasteItem = viewModel.currentItem,
            userAnswers = viewModel.userAnswers,
            onContainerClick = viewModel::onContainerClick
        )
        VerifyButton(
            allAnswered = allAnswered,
            answeredCount = answeredCount,
            totalCount = viewModel.totalCount,
            onVerifyClick = viewModel::onVerifyClick
        )
    }

    // 1. Diálogo de instrucción del video (SOLO si APRUEBA)
    if (showVideoInstructionDialog) {
        InstruccionesVideoPapresa(
            onWatchVideo = {
                showVideoInstructionDialog = false
                showStrictVideoDialog = true
            }

        )
    }

    // 2. Diálogo del video (obligatorio ver completo)
    if (showStrictVideoDialog) {
        VideoDialogoa(
            onVideoCompleted = {
                showStrictVideoDialog = false
                hasWatchedVideo = true
                viewModel.onVideoWatched()
            }
        )
    }


    // 3. Diálogo de éxito SOLO después de ver el video completo
    if (showSuccess && hasWatchedVideo) {
        GameResultDialogs(
            showSuccess = true,
            showWrong = false,
            onDismissSuccess = { },
            onDismissWrong = {  },
            onSuccessButton = {
                viewModel.dismissSuccessDialog()
                navController.navigate(Routes.MAPA_SCREEN)
            },
            onWrongButton = { }
        )
    }

    // 4. Diálogo de error si SUSPENDE - se muestra inmediatamente
    if (showWrong) {
        GameResultDialogs(
            showSuccess = false,
            showWrong = true,
            onDismissSuccess = { },
            onDismissWrong = {  },
            onSuccessButton = {  },
            onWrongButton = {
                viewModel.onWrongDialogRetry()
            }
        )
    }
}