package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

/**
 * CONTENIDO BÁSICO - Solo los campos necesarios
 */
data class AzalpenContenido(
    // Audio obligatorio
    val audioResource: Int,
    val imagenesAudio: List<Int>,
    val timelineAudio: List<Pair<Long, Int>>,

    // Texto final obligatorio
    val tituloTexto: String,
    val textoDidactico: String,

    // Botón obligatorio
    val textoBoton: String,
)

/**
 * PANTALLA BASE REUTILIZABLE
 */
@Composable
fun AzalpenBase(
    contenido: AzalpenContenido,
    onClose: () -> Unit,
    onNavigateToGame: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var audioFinished by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableStateOf(0) }
    var currentPosition by remember { mutableStateOf(0L) }
    var totalDuration by remember { mutableStateOf(0L) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    LaunchedEffect(isPlaying, mediaPlayer) {
        if (isPlaying && mediaPlayer != null) {
            currentImageIndex = 0
            totalDuration = mediaPlayer?.duration?.toLong() ?: 0L

            while (isPlaying && mediaPlayer != null) {
                val currentPos = mediaPlayer?.currentPosition ?: 0
                currentPosition = currentPos.toLong()

                // Solo procesar timeline si no está vacío
                if (contenido.timelineAudio.isNotEmpty()) {
                    contenido.timelineAudio.forEach { (timeMs, imageIndex) ->
                        if (currentPos >= timeMs && currentImageIndex < imageIndex) {
                            currentImageIndex = imageIndex
                        }
                    }
                }

                if (!mediaPlayer!!.isPlaying && currentPos > 0) {
                    audioFinished = true
                    break
                }

                delay(100)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(configuration.screenHeightDp.dp * 0.45f)
        ) {
            AnimatedContent(
                targetState = audioFinished,
                transitionSpec = { fadeIn(tween(500)) togetherWith fadeOut(tween(300)) },
                label = "PlayerToText"
            ) { finished ->
                if (!finished) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp)
                    ) {
                        AudioPlayerCard(
                            isPlaying = isPlaying,
                            isLoading = isLoading,
                            currentPosition = currentPosition,
                            totalDuration = totalDuration,
                            audioFinished = audioFinished,
                            onPlayPauseClick = {
                                if (isPlaying) {
                                    mediaPlayer?.pause()
                                    isPlaying = false
                                } else {
                                    if (mediaPlayer == null) {
                                        isLoading = true
                                        try {
                                            val mp = MediaPlayer.create(context, contenido.audioResource)
                                            mp.setOnPreparedListener {
                                                isLoading = false
                                                it.start()
                                                isPlaying = true
                                                audioFinished = false
                                                totalDuration = it.duration.toLong()
                                            }
                                            mp.setOnCompletionListener {
                                                isPlaying = false
                                                audioFinished = true
                                                it.release()
                                                mediaPlayer = null
                                            }
                                            mediaPlayer = mp
                                        } catch (e: Exception) {
                                            isLoading = false
                                            e.printStackTrace()
                                        }
                                    } else {
                                        mediaPlayer?.start()
                                        isPlaying = true
                                        audioFinished = false
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // SOLUCIÓN: Solo mostrar imágenes si la lista no está vacía
                        if (isPlaying && contenido.imagenesAudio.isNotEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Crossfade(
                                    targetState = currentImageIndex,
                                    animationSpec = tween(1000)
                                ) { index ->
                                    // Verificación adicional de índice
                                    if (index < contenido.imagenesAudio.size) {
                                        Image(
                                            painter = painterResource(id = contenido.imagenesAudio[index]),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(200.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp)
                    ) {
                        // Solo mostrar título si no está vacío
                        if (contenido.tituloTexto.isNotEmpty()) {
                            Text(
                                text = contenido.tituloTexto,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Solo mostrar texto didáctico si no está vacío
                        if (contenido.textoDidactico.isNotEmpty()) {
                            Text(
                                text = contenido.textoDidactico,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Button(
                            onClick = onNavigateToGame,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text(
                                text = contenido.textoBoton,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun AudioPlayerCard(
    isPlaying: Boolean,
    isLoading: Boolean,
    currentPosition: Long,
    totalDuration: Long,
    audioFinished: Boolean,
    onPlayPauseClick: () -> Unit
) {
    fun formatTime(milliseconds: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .clickable(enabled = !isLoading && !audioFinished, onClick = onPlayPauseClick),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                    } else {
                        val iconRes = when {
                            isPlaying -> com.example.errenteriaapp.R.drawable.pause
                            audioFinished -> com.example.errenteriaapp.R.drawable.check
                            else -> com.example.errenteriaapp.R.drawable.play_arrow
                        }
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AUDIO DIDAKTIKOA",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isPlaying) "Entzuten... ${formatTime(currentPosition)}"
                        else if (audioFinished) "Audioa amaitu da"
                        else "Audioa entzun",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp
                    )
                }

                if (isPlaying) {
                    Text(
                        text = formatTime(totalDuration - currentPosition),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (isPlaying && totalDuration > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = currentPosition.toFloat() / totalDuration.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    trackColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}