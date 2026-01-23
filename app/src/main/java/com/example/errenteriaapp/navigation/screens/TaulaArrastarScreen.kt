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
import com.example.errenteriaapp.database.viewModel.ArropaBuruHandiakViewModel


@Composable
fun TaulaArrastrarScreen(
    navController: NavController,
    userName: String?,
    viewModel: ArropaBuruHandiakViewModel
) {
    LaunchedEffect(userName) {
        userName?.let {
            viewModel.setUsuario(it)
        }
    }
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

        VerifyButtonnn(
            allSlotsFilled = uiState.allSlotsFilled,
            onVerifyClick = {
                val (haAprobado, esPerfecto) = viewModel.checkAnswers() // Recibir ambos valores
                if (haAprobado) {
                    // Si aprobó, mostrar éxito (aunque no sea perfecto)
                    viewModel.showSuccessDialog(true)
                    // No necesitas modificar el estado aquí, solo en el ViewModel
                    // Si necesitas saber si fue perfecto, el ViewModel debería guardarlo
                } else {
                    viewModel.showErrorDialog(true)
                }
            }
        )
    }

    DraggingWordOverlay(dragState)

    if (uiState.showSuccessDialog) {
        GameResultDialogs(
            showSuccess = true,
            showWrong = false,
            onDismissSuccess = { viewModel.showSuccessDialog(false) },
            onDismissWrong = { },
            onSuccessButton = {
                viewModel.showSuccessDialog(false)
                navController.navigate("MAPA_SCREEN")
            },
            onWrongButton = { }
        )
    }

    if (uiState.showErrorDialog) {
        GameResultDialogs(
            showSuccess = false,
            showWrong = true,
            onDismissSuccess = { },
            onDismissWrong = { viewModel.showErrorDialog(false) },
            onSuccessButton = { },
            onWrongButton = {
                viewModel.showErrorDialog(false)
                viewModel.resetGame()
            }
        )
    }
}