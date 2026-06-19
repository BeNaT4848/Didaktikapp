package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.errenteriaapp.classes.rememberDragState
import com.example.errenteriaapp.components.*
import com.example.errenteriaapp.database.viewModel.ArropaBuruHandiakViewModel
import com.example.errenteriaapp.navigation.Routes
import com.example.errenteriaapp.progress.KokapenaProgressRepository

/**
 * Taula arrastratzeko pantaila nagusia.
 * "Arropa buru handiak" jokoaren pantaila non hitzak pertsonaien gainean arrastratu behar diren.
 *
 * @param navController Nabigazio kontroladorea
 * @param userName Erabiltzaile izena (hautazkoa)
 * @param viewModel Arropa buru handiak jokoaren ViewModel
 *
 * @see ArropaBuruHandiakViewModel
 * @see KokapenaProgressRepository
 * @see rememberDragState
 * @see HeaderTaulaArrastrar
 * @see ArgazkiaTaulaArrastrar
 * @see CharactersRow
 * @see AvailableWordsSection
 * @see VerifyButtonnn
 * @see DraggingWordOverlay
 * @see GameResultDialogs
 */
@Composable
fun TaulaArrastrarScreen(
    navController: NavController,
    userName: String?,
    viewModel: ArropaBuruHandiakViewModel
) {
    // Testuingurua eta aurrerapen errepisitorioa lortu
    val context = LocalContext.current
    val progressRepo = remember(userName) {
        KokapenaProgressRepository(context, userName ?: "default")
    }

    // Erabiltzailea ViewModel-ean ezarri
    LaunchedEffect(userName) {
        userName?.let {
            viewModel.setUsuario(it)
        }
    }

    // ViewModel-eko UI egoera behatu
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Arraste egoera memorizatu
    val dragState = rememberDragState()

    // Pantaila nagusiaren edukia
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Goiburua
        HeaderTaulaArrastrar()
        // Irudia
        ArgazkiaTaulaArrastrar()

        // Pertsonaien errenkada (Xanti eta Maialen)
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

        // Erabilgarri dauden hitzen atala
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

        // Egiaztatzeko botoia
        VerifyButtonnn(
            allSlotsFilled = uiState.allSlotsFilled,
            onVerifyClick = {
                // Erantzunak egiaztatu (bi balioak jaso)
                val (haAprobado, esPerfecto) = viewModel.checkAnswers()
                if (haAprobado) {
                    // Onartuta badago, arrakasta erakutsi (perfektua ez bada ere)
                    viewModel.showSuccessDialog(true)
                    // Ez duzu egoera hemen aldatu behar, ViewModel-ean bakarrik
                    // Perfektua den jakin behar baduzu, ViewModel-ean gorde beharko litzateke
                } else {
                    viewModel.showErrorDialog(true)
                }
            }
        )
    }

    // Arrastean dagoen hitzaren gainjartzea
    DraggingWordOverlay(dragState)

    // Arrakasta elkarrizketa erakutsi
    if (uiState.showSuccessDialog) {
        GameResultDialogs(
            showSuccess = true,
            showWrong = false,
            onDismissSuccess = { viewModel.showSuccessDialog(false) },
            onDismissWrong = { },
            onSuccessButton = {
                viewModel.showSuccessDialog(false)
                progressRepo.markCompleted(Routes.TAULAARRASTRAR_SCRENN)
                // KATEATUA: Taula Arrastrar amaitutakoan, Sopa letra joan
                navController.navigate(Routes.SOPALETRA_SCREEN)
            },
            onWrongButton = { }
        )
    }

    // Errore elkarrizketa erakutsi
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