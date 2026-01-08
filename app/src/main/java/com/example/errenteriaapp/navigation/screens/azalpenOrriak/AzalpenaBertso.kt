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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.errenteriaapp.R
import com.example.errenteriaapp.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun AzalpenBertso(
    navController: NavController
) {
    val context = LocalContext.current

    // Estados
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var audioFinished by remember { mutableStateOf(false) }
    var currentImageIndex by remember { mutableStateOf(0) }
    var showContent by remember { mutableStateOf(false) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    // IDs de imágenes para el carrusel
    val imageResources = listOf(
        R.drawable.xenpelar_etxea,
        R.drawable.xenpelar_eta_txirrita_1,
        R.drawable.xenpela_eta_txirrita_2
    )

    // Imágenes que se muestran en cada momento
    val imageTimeline = listOf(
        5000L to 0,    // A los 5 segundos, imagen 1
        15000L to 1,   // A los 15 segundos, imagen 2
        25000L to 2    // A los 25 segundos, imagen 3
    )

    // Gestionar ciclo de vida
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    // Controlar imágenes durante reproducción
    LaunchedEffect(isPlaying, mediaPlayer) {
        if (isPlaying && mediaPlayer != null) {
            // Reiniciar índice de imágenes
            currentImageIndex = 0

            // Monitorear posición del audio para cambiar imágenes
            while (isPlaying && mediaPlayer != null) {
                val currentPos = mediaPlayer?.currentPosition ?: 0

                // Verificar si es tiempo de mostrar siguiente imagen
                imageTimeline.forEach { (timeMs, imageIndex) ->
                    if (currentPos >= timeMs && currentImageIndex < imageIndex) {
                        currentImageIndex = imageIndex
                    }
                }

                // Verificar si audio terminó
                if (!mediaPlayer!!.isPlaying && currentPos > 0) {
                    audioFinished = true
                    showContent = true
                    break
                }

                delay(100) // Revisar cada 100ms
            }
        }
    }

    // Cuando audio termina, mostrar contenido
    LaunchedEffect(audioFinished) {
        if (audioFinished) {
            // Pequeño delay antes de mostrar contenido
            delay(500)
            showContent = true
        }
    }

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0E1B14),
            Color(0xFF08110C)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Title (solo visible después del audio)
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(1000)) + slideInVertically(
                initialOffsetY = { -40 }
            )
        ) {
            Text(
                text = "Xenpelar Etxea",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Audio Player (siempre visible)
        AudioPlayerCard(
            isPlaying = isPlaying,
            isLoading = isLoading,
            currentImageIndex = currentImageIndex, // Pasar el índice
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
                    }
                }
            },
            audioFinished = audioFinished
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Carrusel de imágenes que aparece durante audio
        AnimatedVisibility(
            visible = isPlaying || audioFinished,
            enter = fadeIn(tween(1000))
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Imagen actual
                Crossfade(
                    targetState = currentImageIndex,
                    animationSpec = tween(1000)
                ) { index ->
                    Image(
                        painter = painterResource(id = imageResources[index]),
                        contentDescription = "Imagen ${index + 1}",
                        modifier = Modifier
                            .size(240.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }

                // Indicador de progreso (puntos)
                if (isPlaying) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(top = 260.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(imageResources.size) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (index <= currentImageIndex)
                                            Color(0xFF3DFF8F)
                                        else
                                            Color(0xFF3A4A42)
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Contenido que aparece SOLO después del audio
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(1500)) + slideInVertically(
                initialOffsetY = { 50 }
            )
        ) {
            Column {
                // About section
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

                Spacer(modifier = Modifier.height(24.dp))

                // Botón para continuar
                Button(
                    onClick = { navController.navigate(Routes.BERTSOJOLASA_SCREEN) },
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
                        text = if (audioFinished) "Jolasten hasi" else "Audioa entzun behar da",
                        color = if (audioFinished) Color.Black else Color(0xFF666666),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Mensaje informativo
                if (!audioFinished) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Audioa amaitu arte itxaron behar duzu",
                        color = Color(0xFF7CFFB2),
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun AudioPlayerCard(
    isPlaying: Boolean,
    isLoading: Boolean,
    currentImageIndex: Int, // Parámetro añadido
    audioFinished: Boolean,
    onPlayPauseClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E3A2F)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón de play/pause
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (audioFinished) Color(0xFF2D8A5F) else Color(0xFF3DFF8F)
                    )
                    .clickable(
                        enabled = !isLoading && !audioFinished,
                        onClick = onPlayPauseClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black,
                        strokeWidth = 3.dp
                    )
                } else {
                    val iconRes = if (isPlaying) {
                        R.drawable.pause
                    } else if (audioFinished) {
                        R.drawable.check // Icono de completado
                    } else {
                        R.drawable.play_arrow
                    }

                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "AUDIO DIDAKTIKOA",
                    color = if (audioFinished) Color(0xFF2D8A5F) else Color(0xFF7CFFB2),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (audioFinished)
                        "Audioa amaitu da - Jarraitu dezakezu"
                    else if (isPlaying)
                        "Entzuten... (${currentImageIndex + 1}/3 irudiak)" // Usar el parámetro
                    else
                        "Xenpelar eta Txirrita",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AzalpenXenpelarPreview() {
    AzalpenBertso(
        navController = rememberNavController()
    )
}