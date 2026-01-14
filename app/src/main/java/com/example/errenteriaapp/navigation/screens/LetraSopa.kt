package com.example.errenteriaapp.navigation.screens

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.errenteriaapp.components.GameResultDialogs
import com.example.errenteriaapp.components.SopaDeLetrasTablero
import com.example.errenteriaapp.components.SopaHeader
import com.example.errenteriaapp.components.SopaPalabrasList
import com.example.errenteriaapp.components.SopaProgressBar
import com.example.errenteriaapp.database.viewModel.SopaDeLetrasViewModel
import com.example.errenteriaapp.navigation.Routes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LetraSopaScreen(
    navController: NavController
) {
    val viewModel: SopaDeLetrasViewModel = viewModel()
    val gameState by viewModel.gameState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // MediaPlayer para el audio
    val mediaPlayer = remember { MediaPlayer() }
    var isAudioPrepared by remember { mutableStateOf(false) }

    // Animación de confeti
    val confettiScale = remember { mutableStateOf(0f) }

    // Inicializar y preparar el audio
    LaunchedEffect(Unit) {
        try {
            val audioResource = context.resources.getIdentifier(
                "zentenarioa_musika_audioa",
                "raw",
                context.packageName
            )

            if (audioResource != 0) {
                mediaPlayer.setDataSource(context.resources.openRawResourceFd(audioResource))
                mediaPlayer.prepareAsync()

                // Configurar para que se repita si es necesario
                mediaPlayer.isLooping = true // Cambia a false si no quieres que se repita

                // Configurar listener para cuando el audio esté preparado
                mediaPlayer.setOnPreparedListener {
                    isAudioPrepared = true
                    // Reproducir automáticamente cuando esté preparado
                    mediaPlayer.start()
                }
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

                // Esperar y navegar al siguiente juego
                delay(2000)

                // Opcional: Detener audio antes de navegar
                 if (mediaPlayer.isPlaying) mediaPlayer.stop()

                navController.navigate(Routes.CRUCIGRAMA_SCREEN)
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
                    navController.navigate(Routes.MAPA_SCREEN)
                },
                onWrongButton = { }
            )
        }
    }
}