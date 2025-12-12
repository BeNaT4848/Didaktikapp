package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.errenteriaapp.components.GameResultDialogs
import com.example.errenteriaapp.components.InstructionText
import com.example.errenteriaapp.components.PapresaTitle
import com.example.errenteriaapp.components.PhotoCarousel
import com.example.errenteriaapp.components.ProgressCounter
import com.example.errenteriaapp.components.ResultsDialog
import com.example.errenteriaapp.components.VerifyButton
import com.example.errenteriaapp.components.WasteContainersRow
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

    GameResultDialogs(
        showSuccess = showSuccess,
        showWrong = showWrong,
        onDismissSuccess = viewModel::dismissSuccessDialog,
        onDismissWrong = viewModel::dismissWrongDialog,
        onSuccessButton = {
            viewModel.onSuccessDialogConfirmed()
            navController.navigate(Routes.MAPA_SCREEN)
        },
        onWrongButton = viewModel::onWrongDialogRetry
    )

    if (showResults && !showSuccess && !showWrong) {
        ResultsDialog(
            wasteItems = viewModel.wasteItems,
            userAnswers = viewModel.userAnswers,
            onDismiss = viewModel::onDismissResults,
            onNext = {
                viewModel.onDismissResults()
                navController.navigate(Routes.MAPA_SCREEN)
            }
        )
    }
}