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
import androidx.navigation.NavController
import com.example.errenteriaapp.components.*
import com.example.errenteriaapp.components.video.InstruccionesVideoPapresa
import com.example.errenteriaapp.components.video.VideoDialogoa
import com.example.errenteriaapp.database.viewModel.PapresaViewModel
import com.example.errenteriaapp.navigation.Routes
import androidx.compose.ui.platform.LocalContext
import com.example.errenteriaapp.progress.KokapenaProgressRepository

/**
 * Papresa (paper birziklatze) jokoaren pantaila nagusia konposatzen du
 * @param navController Nabigazio kontrolatzailea
 * @param userName Erabiltzailearen izena (aukerakoa)
 * @param viewModel Papresa jokoaren ViewModela
 */
@Composable
fun PapresaScreen(
    navController: NavController,
    userName: String?,
    viewModel: PapresaViewModel,
) {
    val context = LocalContext.current
    val sessionPrefs = remember { context.getSharedPreferences("session", android.content.Context.MODE_PRIVATE) }
    val effectiveUserName = userName ?: sessionPrefs.getString("active_user_name", null)
    val progressRepo = remember(effectiveUserName) { KokapenaProgressRepository(context, effectiveUserName ?: "default") }

    LaunchedEffect(effectiveUserName) {
        effectiveUserName?.let { viewModel.setUsuario(it) }
    }
    val currentIndex = viewModel.currentIndex
    val allAnswered = viewModel.allAnswered
    val answeredCount = viewModel.answeredCount
    val showResults = viewModel.showResults
    val showSuccess = viewModel.showSuccessDialog
    val showWrong = viewModel.showWrongDialog
    val hasPassed = viewModel.hasPassed

    // Egoera zorrotzak kontrolatzeko egoerak
    var showVideoInstructionDialog by remember { mutableStateOf(false) }
    var showStrictVideoDialog by remember { mutableStateOf(false) }
    var hasWatchedVideo by remember { mutableStateOf(false) }

    // ZUZENDUTAKO logika bideoaren instrukzioa erakusteko soilik GARAITZEN BADA
    LaunchedEffect(showResults) {
        if (showResults && hasPassed && !showVideoInstructionDialog && !hasWatchedVideo) {
            // Bideoaren instrukzioa soilik erakutsi GARAITZEN BADA (hasPassed = true)
            showVideoInstructionDialog = true
        }
    }

    // Gainditzen ez badu, errore elkarrizketa berehala erakusten du (viewModel-ek kudeatzen du)

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

    // 1. Bideoaren instrukzio elkarrizketa (SOILIK GARAITZEN BADA)
    if (showVideoInstructionDialog) {
        InstruccionesVideoPapresa(
            onWatchVideo = {
                showVideoInstructionDialog = false
                showStrictVideoDialog = true
            }

        )
    }

    // 2. Bideoaren elkarrizketa (betea ikustea beharrezkoa)
    if (showStrictVideoDialog) {
        VideoDialogoa(
            onVideoCompleted = {
                showStrictVideoDialog = false
                hasWatchedVideo = true
                viewModel.onVideoWatched()
                progressRepo.markCompleted(Routes.BASURA_SCREEN)
                navController.navigate(Routes.GPS_SCREEN)
            }
        )
    }

    // 3. Arrakasta elkarrizketa BIDEOREN ONDOREN soilik
    if (showSuccess && hasWatchedVideo) {
        GameResultDialogs(
            showSuccess = true,
            showWrong = false,
            onDismissSuccess = { },
            onDismissWrong = {  },
            onSuccessButton = {
                viewModel.dismissSuccessDialog()
                progressRepo.markCompleted(Routes.BASURA_SCREEN)
                navController.navigate(Routes.GPS_SCREEN)
            },
            onWrongButton = { }
        )
    }

    // 4. Errore elkarrizketa GAINDITZEN EZ BADA - berehala erakusten da
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