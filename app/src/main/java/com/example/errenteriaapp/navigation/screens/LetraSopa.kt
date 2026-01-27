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

    // MediaPlayer para el audio
    val mediaPlayer = remember { MediaPlayer() }

    // Animación de confeti
    val confettiScale = remember { mutableStateOf(0f) }

    // Inicializar y preparar el audio
    LaunchedEffect(Unit) {
        try {
            val audioResource = R.raw.zentenarioa_musika_audioa
            mediaPlayer.setDataSource(context.resources.openRawResourceFd(audioResource))
            mediaPlayer.prepareAsync()

            mediaPlayer.isLooping = true // Cambia a false si no quieres que se repita

            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Pausar y liberar el MediaPlayer cuando se sale de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
        }
    }

    // Efecto para cuando se completa el juego
    LaunchedEffect(gameState.mostrarExito) {
        if (gameState.mostrarExito) {
            scope.launch {
                // Opcional: Detener el audio cuando se completa el juego
                mediaPlayer.pause()

                delay(500)

                // Animación de confeti
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
            // Encabezado
            SopaHeader()

            // Barra de progreso
            SopaProgressBar(
                encontradas = gameState.palabrasEncontradas.size,
                total = viewModel.palabras.size
            )

            // Tablero de sopa de letras
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

            // Lista de palabras
            SopaPalabrasList(
                palabras = viewModel.palabras,
                palabrasEncontradas = gameState.palabrasEncontradas
            )
        }

        // Diálogo de éxito
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
                    // Al terminar, ir a Ranking
                    navController.navigate(Routes.RANKIN_SCREEN)
                },
                onWrongButton = { }
            )
        }
    }
}