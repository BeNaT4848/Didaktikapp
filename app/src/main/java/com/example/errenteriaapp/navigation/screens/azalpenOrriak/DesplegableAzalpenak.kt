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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

/**
 * CONTENIDO BÁSICO - Audio opcional
 */
data class AzalpenContenido(
    // Audio ahora es opcional
    val audioResource: Int? = null,
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
    // Si no hay audio, marcamos como terminado desde el inicio
    var audioFinished by remember {
        mutableStateOf(contenido.audioResource == null)
    }
    var currentImageIndex by remember { mutableStateOf(0) }
    var currentPosition by remember { mutableStateOf(0L) }
    var totalDuration by remember { mutableStateOf(0L) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    val tieneAudio = contenido.audioResource != null

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            // REDUCIDO: Espacio superior más pequeño
            Spacer(modifier = Modifier.height(8.dp))

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
                    // Si no está terminado Y hay audio, mostrar player
                    if (!finished && tieneAudio) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 4.dp) // REDUCIDO: menos padding superior
                        ) {
                            AudioPlayerCard(
                                isPlaying = isPlaying,
                                isLoading = isLoading,
                                currentPosition = currentPosition,
                                totalDuration = totalDuration,
                                audioFinished = audioFinished,
                                tieneAudio = tieneAudio,
                                onPlayPauseClick = {
                                    if (isPlaying) {
                                        mediaPlayer?.pause()
                                        isPlaying = false
                                    } else {
                                        if (mediaPlayer == null) {
                                            isLoading = true
                                            try {
                                                val mp = MediaPlayer.create(context, contenido.audioResource!!)
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

                            // REDUCIDO: menos espacio entre el player y las imágenes
                            Spacer(modifier = Modifier.height(8.dp))

                            // Solo mostrar imágenes si la lista no está vacía
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
                                                    .size(180.dp) // REDUCIDO: tamaño de imagen más pequeño
                                                    .clip(RoundedCornerShape(12.dp))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // MOSTRAR TEXTO Y BOTÓN (con o sin audio)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            // Solo mostrar título si no está vacío
                            if (contenido.tituloTexto.isNotEmpty()) {
                                Text(
                                    text = contenido.tituloTexto,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp)) // REDUCIDO: menos espacio
                            }

                            // Solo mostrar texto didáctico si no está vacío
                            if (contenido.textoDidactico.isNotEmpty()) {
                                Text(
                                    text = contenido.textoDidactico,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp)) // REDUCIDO: menos espacio
                            }

                            Button(
                                onClick = onNavigateToGame,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp), // REDUCIDO: altura del botón
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                ),
                                shape = RoundedCornerShape(24.dp) // REDUCIDO: bordes menos redondeados
                            ) {
                                Text(
                                    text = contenido.textoBoton,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 15.sp, // REDUCIDO: tamaño de texto
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // REDUCIDO: mucho menos espacio al final
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AudioPlayerCard(
    isPlaying: Boolean,
    isLoading: Boolean,
    currentPosition: Long,
    totalDuration: Long,
    audioFinished: Boolean,
    tieneAudio: Boolean,
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
        shape = RoundedCornerShape(20.dp) // REDUCIDO: bordes menos redondeados
    ) {
        Column(modifier = Modifier.padding(12.dp)) { // REDUCIDO: padding interno
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Solo mostrar botón de play si hay audio
                if (tieneAudio) {
                    Box(
                        modifier = Modifier
                            .size(48.dp) // REDUCIDO: tamaño del botón
                            .clip(RoundedCornerShape(50))
                            .background(Color.White)
                            .clickable(enabled = !isLoading && !audioFinished, onClick = onPlayPauseClick),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp), // REDUCIDO: tamaño del spinner
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            val iconRes = when {
                                isPlaying -> com.example.errenteriaapp.R.drawable.pause
                                audioFinished -> com.example.errenteriaapp.R.drawable.check
                                else -> com.example.errenteriaapp.R.drawable.play_arrow
                            }
                            Image(
                                painter = painterResource(id = iconRes),
                                contentDescription = stringResource(
                                    if (isPlaying) R.string.audio_pause else R.string.audio_play
                                ),
                                modifier = Modifier.size(24.dp) // REDUCIDO: tamaño del icono
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp)) // REDUCIDO: espacio horizontal
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (tieneAudio) {
                            stringResource(R.string.audio_didactic)
                        } else {
                            stringResource(R.string.azalpena_label)
                        },
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp, // REDUCIDO: tamaño de texto
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (tieneAudio) {
                            when {
                                isPlaying -> stringResource(
                                    R.string.audio_listening,
                                    formatTime(currentPosition)
                                )
                                audioFinished -> stringResource(R.string.audio_finished)
                                else -> stringResource(R.string.audio_listen)
                            }
                        } else {
                            stringResource(R.string.azalpena_read)
                        },
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 13.sp // REDUCIDO: tamaño de texto
                    )
                }

                if (tieneAudio && isPlaying) {
                    Text(
                        text = formatTime(totalDuration - currentPosition),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 13.sp, // REDUCIDO: tamaño de texto
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (tieneAudio && isPlaying && totalDuration > 0) {
                Spacer(modifier = Modifier.height(8.dp)) // REDUCIDO: espacio
                LinearProgressIndicator(
                    progress = currentPosition.toFloat() / totalDuration.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp), // REDUCIDO: altura de la barra
                    color = MaterialTheme.colorScheme.onPrimary,
                    trackColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}