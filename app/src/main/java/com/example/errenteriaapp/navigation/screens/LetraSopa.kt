package com.example.errenteriaapp.navigation.screens

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.errenteriaapp.R
import com.example.errenteriaapp.database.viewModel.SopaDeLetrasViewModel
import com.example.errenteriaapp.navigation.Routes
import com.example.errenteriaapp.components.*
import com.example.errenteriaapp.progress.KokapenaProgressRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Sopa de letras jokoaren pantaila nagusia konposatzen du
 * @param navController Nabigazio kontrolatzailea
 * @param userName Erabiltzailearen izena (aukerakoa)
 * @param viewModel Sopa de letras jokoaren ViewModela
 */
@Composable
fun LetraSopaScreen(
    navController: NavController,
    userName: String?,
    viewModel: SopaDeLetrasViewModel
) {
    val context = LocalContext.current
    val progressRepo = remember(userName) { KokapenaProgressRepository(context, userName ?: "default") }

    LaunchedEffect(userName) {
        userName?.let {
            viewModel.setUsuario(it)
        }
    }
    val gameState by viewModel.gameState.collectAsState()
    val scope = rememberCoroutineScope()

    // Audioarentzako MediaPlayer
    val mediaPlayer = remember { MediaPlayer() }

    // Konfetiaren animazioa
    val confettiScale = remember { mutableStateOf(0f) }

    // Audioa hasieratu eta prestatu
    LaunchedEffect(Unit) {
        try {
            val audioResource = R.raw.zentenarioa_musika_audioa
            mediaPlayer.setDataSource(context.resources.openRawResourceFd(audioResource))
            mediaPlayer.prepareAsync()

            mediaPlayer.isLooping = true // Aldatu false-ra ez baduzu errepikatu nahi

            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // MediaPlayer gelditu eta askatu pantaila uzterakoan
    DisposableEffect(Unit) {
        onDispose {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
        }
    }

    // Jokoa osatzerakoan efektua
    LaunchedEffect(gameState.mostrarExito) {
        if (gameState.mostrarExito) {
            scope.launch {
                // Aukerakoa: Audioa gelditu jokoa osatzerakoan
                mediaPlayer.pause()

                delay(500)

                // Konfetiaren animazioa
                confettiScale.value = 0f
                repeat(3) {
                    confettiScale.value = 1f
                    delay(300)
                    confettiScale.value = 0.8f
                    delay(300)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Goiburua
            SopaHeader()

            // Aurrerapen barra
            SopaProgressBar(
                encontradas = gameState.palabrasEncontradas.size,
                total = viewModel.palabras.size
            )

            // Sopa de letrasen taula
            SopaDeLetrasTablero(
                tablero = viewModel.tablero,
                palabras = viewModel.palabras,
                palabrasEncontradas = gameState.palabrasEncontradas,
                onCeldaClick = { fila, columna, palabraAsociada ->
                    palabraAsociada?.let { palabra ->
                        viewModel.marcarPalabraEncontrada(palabra)
                    }
                },
                modifier = Modifier.padding(bottom = 18.dp)
            )

            // Hitz zerrenda
            SopaPalabrasList(
                palabras = viewModel.palabras,
                palabrasEncontradas = gameState.palabrasEncontradas
            )
        }

        // Arrakasta elkarrizketa
        if (gameState.mostrarExito) {
            GameResultDialogs(
                showSuccess = true,
                showWrong = false,
                onDismissSuccess = {
                    viewModel.hideSuccessDialog()
                },
                onDismissWrong = { },
                onSuccessButton = {
                    viewModel.hideSuccessDialog()
                    progressRepo.markCompleted(Routes.SOPALETRA_SCREEN)
                    // Amaitzerakoan, Ranking-era joan
                    navController.navigate(Routes.RANKIN_SCREEN)
                },
                onWrongButton = { }
            )
        }
    }
}