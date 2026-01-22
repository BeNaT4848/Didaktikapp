package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.errenteriaapp.classes.rememberDragState
import com.example.errenteriaapp.components.*
import com.example.errenteriaapp.viewmodel.DragGameViewModel

@Composable
fun TaulaArrastrarScreen(
    navController: NavController,
    userName: String?,
    viewModel: DragGameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dragState = rememberDragState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderTaulaArrastrar()
        ArgazkiaTaulaArrastrar()

        CharactersRow(
            xantiAssignments = uiState.xantiAssignments,
            maialenAssignments = uiState.maialenAssignments,
            onXantiSlotPositioned = viewModel::updateXantiDropZone,
            onMaialenSlotPositioned = viewModel::updateMaialenDropZone,
            dragState = dragState,
            onDrop = { dropPoint ->
                viewModel.handleDrop(
                    dropPoint = dropPoint,
                    draggingWord = dragState.draggingWord,
                    draggingSlotCharacter = dragState.draggingSlotCharacter,
                    draggingSlotIndex = dragState.draggingSlotIndex,
                    isDraggingFromSlot = dragState.isDraggingFromSlot
                )
            }
        )

        AvailableWordsSection(
            availableWords = uiState.availableWords,
            dragState = dragState,
            onDrop = { dropPoint ->
                viewModel.handleDrop(
                    dropPoint = dropPoint,
                    draggingWord = dragState.draggingWord,
                    draggingSlotCharacter = dragState.draggingSlotCharacter,
                    draggingSlotIndex = dragState.draggingSlotIndex,
                    isDraggingFromSlot = dragState.isDraggingFromSlot
                )
            }
        )

        VerifyButton(
            allSlotsFilled = uiState.allSlotsFilled,
            onVerifyClick = {
                val isCorrect = viewModel.checkAnswers()
                if (isCorrect) {
                    viewModel.showSuccessDialog(true)
                } else {
                    viewModel.showErrorDialog(true)
                }
            }
        )
    }

    DraggingWordOverlay(dragState)

    GameResultDialogs(
        showSuccess = uiState.showSuccessDialog,
        showWrong = uiState.showErrorDialog,
        onDismissSuccess = { viewModel.showSuccessDialog(false) },
        onDismissWrong = { viewModel.showErrorDialog(false) },
        onSuccessButton = {
            viewModel.showSuccessDialog(false)
            navController.navigate("MAPA_SCREEN")
        },
        onWrongButton = {
            viewModel.showErrorDialog(false)
            viewModel.resetGame()
        }
    )
}