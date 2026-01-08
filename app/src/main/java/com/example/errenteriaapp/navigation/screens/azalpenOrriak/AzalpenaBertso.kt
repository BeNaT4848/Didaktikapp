package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Composable
fun AzalpenBertso(
    onClose: () -> Unit,
    onNavigateToGame: () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    // Estados
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var audioFinished by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableStateOf(0) }
    var showContent by remember { mutableStateOf(false) }
    var showImages by remember { mutableStateOf(true) }
    var showAudioPlayer by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableStateOf(0L) }
    var totalDuration by remember { mutableStateOf(0L) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    // IDs de imágenes
    val imageResources = listOf(
        R.drawable.xenpelar_etxea,
        R.drawable.xenpelar_eta_txirrita_1,
        R.drawable.xenpela_eta_txirrita_2
    )

    val imageTimeline = listOf(
        5000L to 0,
        15000L to 1,
        25000L to 2
    )

    // Gestionar ciclo de vida
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    // Controlar tiempo e imágenes
    LaunchedEffect(isPlaying, mediaPlayer) {
        if (isPlaying && mediaPlayer != null) {
            currentImageIndex = 0
            totalDuration = mediaPlayer?.duration?.toLong() ?: 0L

            while (isPlaying && mediaPlayer != null) {
                val currentPos = mediaPlayer?.currentPosition ?: 0
                currentPosition = currentPos.toLong()

                imageTimeline.forEach { (timeMs, imageIndex) ->
                    if (currentPos >= timeMs && currentImageIndex < imageIndex) {
                        currentImageIndex = imageIndex
                    }
                }

                if (!mediaPlayer!!.isPlaying && currentPos > 0) {
                    audioFinished = true
                    showImages = false
                    break
                }

                delay(100)
            }
        }
    }

    // Cuando audio termina
    LaunchedEffect(audioFinished) {
        if (audioFinished) {
            showImages = false
            delay(300)
            showAudioPlayer = false
            delay(300)
            showContent = true
        }
    }

    // Función para formatear tiempo
    fun formatTime(milliseconds: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0E1B14),
            Color(0xFF08110C)
        )
    )

    // CONTENIDO PRINCIPAL CON ALTURA MÍNIMA
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgGradient)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // CONTENIDO INICIAL (para forzar altura mínima)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(configuration.screenHeightDp.dp * 0.45f) // 45% mínimo
        ) {
            Column {
                Spacer(modifier = Modifier.height(20.dp))

                // Audio Player
                if (showAudioPlayer) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(500)),
                        exit = fadeOut(tween(300))
                    ) {
                        AudioPlayerCard(
                            isPlaying = isPlaying,
                            isLoading = isLoading,
                            currentPosition = currentPosition,
                            totalDuration = totalDuration,
                            onPlayPauseClick = {
                                if (isPlaying) {
                                    mediaPlayer?.pause()
                                    isPlaying = false
                                } else {
                                    if (mediaPlayer == null) {
                                        isLoading = true
                                        try {
                                            val mp = MediaPlayer.create(context, R.raw.xenpelar_etxea_audioa)
                                            mp.setOnPreparedListener {
                                                isLoading = false
                                                it.start()
                                                isPlaying = true
                                                audioFinished = false
                                                showContent = false
                                                showAudioPlayer = true
                                                showImages = true
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
                                        showContent = false
                                        showAudioPlayer = true
                                    }
                                }
                            },
                            audioFinished = audioFinished,
                            formatTime = ::formatTime
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Carrusel de imágenes
                if (showImages && isPlaying) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(1000)),
                        exit = fadeOut(tween(300))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Crossfade(
                                targetState = currentImageIndex,
                                animationSpec = tween(1000)
                            ) { index ->
                                Image(
                                    painter = painterResource(id = imageResources[index]),
                                    contentDescription = "Imagen ${index + 1}",
                                    modifier = Modifier
                                        .size(200.dp) // Reducido para Bottom Sheet
                                        .clip(RoundedCornerShape(12.dp))
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Contenido final (se añade al espacio ya ocupado)
        if (showContent) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(800)) + slideInVertically(
                    initialOffsetY = { 20 }
                )
            ) {
                Column {
                    Text(
                        text = "Testu didaktikoa",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Bertsoak ahoz egiten diren arren, bertso-paperei esker (bertsoak biltzen zituzten panfletoak) " +
                                "Euskal Herri osoan zehar banatu zitezkeen. \n" +
                                "Gainera, bertso-paperei esker, gaur bi bertsolari hauen bertsoak irakurri eta osatu ahal izango dituzue.\n" +
                                "Bertso hauek osatzeko, errimetan eta kontatzen ari diren istorioan fijatu zaitezte!\n",
                        color = Color(0xFFB7CFC3),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botón para continuar
                    Button(
                        onClick = onNavigateToGame,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3DFF8F)
                        ),
                        shape = RoundedCornerShape(28.dp),
                        enabled = audioFinished
                    ) {
                        Text(
                            text = "Jolasten hasi",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// AudioPlayerCard se mantiene IGUAL
@Composable
fun AudioPlayerCard(
    isPlaying: Boolean,
    isLoading: Boolean,
    currentPosition: Long,
    totalDuration: Long,
    audioFinished: Boolean,
    onPlayPauseClick: () -> Unit,
    formatTime: (Long) -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A2F)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (audioFinished) Color(0xFF2D8A5F) else Color(0xFF3DFF8F))
                        .clickable(enabled = !isLoading && !audioFinished, onClick = onPlayPauseClick),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black,
                            strokeWidth = 3.dp
                        )
                    } else {
                        val iconRes = if (isPlaying) R.drawable.pause
                        else if (audioFinished) R.drawable.check
                        else R.drawable.play_arrow

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
                        color = if (audioFinished) Color(0xFF2D8A5F) else Color(0xFF7CFFB2),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isPlaying) "Entzuten... ${formatTime(currentPosition)}"
                        else if (audioFinished) "Audioa amaitu da"
                        else "Xenpelar eta Txirrita",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                if (isPlaying) {
                    Text(
                        text = formatTime(totalDuration - currentPosition),
                        color = Color(0xFF7CFFB2),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (isPlaying && totalDuration > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = currentPosition.toFloat() / totalDuration.toFloat(),
                    modifier = Modifier.fillMaxWidth().height(3.dp),
                    color = Color(0xFF3DFF8F),
                    trackColor = Color(0xFF2A3D35)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AzalpenXenpelarPreview() {
    AzalpenBertso(
        onClose = { },
        onNavigateToGame = { }
    )
}