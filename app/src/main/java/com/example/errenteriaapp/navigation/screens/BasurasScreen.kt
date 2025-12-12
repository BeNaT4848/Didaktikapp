package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    viewModel: PapresaViewModel = viewModel()
) {
    val currentIndex = viewModel.currentIndex
    val allAnswered = viewModel.allAnswered
    val answeredCount = viewModel.answeredCount
    val showResults = viewModel.showResults
    val showSuccess = viewModel.showSuccessDialog
    val showWrong = viewModel.showWrongDialog

    // Estados para controlar el flujo estricto
    var showVideoInstructionDialog by remember { mutableStateOf(false) }
    var showStrictVideoDialog by remember { mutableStateOf(false) }
    var hasWatchedVideo by remember { mutableStateOf(false) }

    // Lógica para mostrar instrucción del video solo si aprueba
    LaunchedEffect(showResults) {
        if (showResults && !showVideoInstructionDialog && !hasWatchedVideo) {
            // Suponiendo que showResults significa que aprobó
            // Si necesitas otra lógica para determinar aprobación, ajústala
            showVideoInstructionDialog = true
        }
    }

    // Si suspende, muestra diálogo de error inmediatamente


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(Color(0xFFF4A460)),
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

    // 1. Diálogo de instrucción del video (NO se puede cerrar)
    if (showVideoInstructionDialog) {
        InstruccionesVideoPapresa(
            onWatchVideo = {
                showVideoInstructionDialog = false
                showStrictVideoDialog = true
            }
            // No tiene onDismiss, no se puede cerrar
        )
    }


    if (showStrictVideoDialog) {
        VideoDialogoa(
            onDismiss = { showStrictVideoDialog = false },  // showVideoDialog -> showStrictVideoDialog
            onVideoCompleted = {
                showStrictVideoDialog = false
                hasWatchedVideo = true
                viewModel.showSuccessDialog = true  // showSuccessDialog ya está en el ViewModel
            }
        )
    }

    // 3. Diálogo de éxito solo después de ver el video
    if (showSuccess && hasWatchedVideo) {
        Emaitza2Papresa(
            showSuccess = true,
            onContinue = {
                viewModel.dismissSuccessDialog()
                navController.navigate(Routes.MAPA_SCREEN)
            },

        )
    }
}