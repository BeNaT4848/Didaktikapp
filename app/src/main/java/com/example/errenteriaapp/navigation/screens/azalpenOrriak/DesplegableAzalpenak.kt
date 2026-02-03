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
 * AZALPEN OROKORRA - Audio aukerakoa
 * @property audioResource Audio baliabidearen IDa (aukerakoa)
 * @property imagenesAudio Audioaren denboralekuko irudien IDen zerrenda
 * @property timelineAudio Denboralekua: denbora (ms) -> irudi indizea
 * @property tituloTexto Azalpenaren titulua
 * @property textoDidactico Hezkuntza testua
 * @property textoBoton Botoian erakutsiko den testua
 */
data class AzalpenContenido(
    val audioResource: Int? = null,
    val imagenesAudio: List<Int>,
    val timelineAudio: List<Pair<Long, Int>>,
    val tituloTexto: String,
    val textoDidactico: String,
    val textoBoton: String,
)

/**
 * AZALPEN PANTALLA BERRIZ ERABILGARRIA
 * @param contenido Azalpenaren edukia
 * @param onClose Azalpena ixteko callback-a
 * @param onNavigateToGame Jokora nabigatzeko callback-a
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
    // Ez badago audiorik, hasieratik amaitu bezala markatzen da
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

                // Timeline soilik prozesatu hutsa ez bada
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
            // MURRIZTUA: Goiko espazio txikiagoa
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
                    // Ez badago amaitua ETA audioa badago, erreproduzitzailea erakutsi
                    if (!finished && tieneAudio) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 4.dp) // MURRIZTUA: goiko padding txikiagoa
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

                            // MURRIZTUA: espazio gutxiago erreproduzitzailea eta irudien artean
                            Spacer(modifier = Modifier.height(8.dp))

                            // Irudiak soilik erakutsi zerrenda hutsa ez bada
                            if (isPlaying && contenido.imagenesAudio.isNotEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Crossfade(
                                        targetState = currentImageIndex,
                                        animationSpec = tween(1000)
                                    ) { index ->
                                        // Indizearen egiaztapen gehigarria
                                        if (index < contenido.imagenesAudio.size) {
                                            Image(
                                                painter = painterResource(id = contenido.imagenesAudio[index]),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(180.dp) // MURRIZTUA: irudiaren tamaina txikiagoa
                                                    .clip(RoundedCornerShape(12.dp))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // TESTUA ETA BOTOIA ERKUTSI (audiorekin edo gabe)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            // Titulua soilik erakutsi hutsa ez bada
                            if (contenido.tituloTexto.isNotEmpty()) {
                                Text(
                                    text = contenido.tituloTexto,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp)) // MURRIZTUA: espazio gutxiago
                            }

                            // Hezkuntza testua soilik erakutsi hutsa ez bada
                            if (contenido.textoDidactico.isNotEmpty()) {
                                Text(
                                    text = contenido.textoDidactico,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp)) // MURRIZTUA: espazio gutxiago
                            }

                            Button(
                                onClick = onNavigateToGame,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp), // MURRIZTUA: botoiaren altuera
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                ),
                                shape = RoundedCornerShape(24.dp) // MURRIZTUA: ertzak gutxiago biribildu
                            ) {
                                Text(
                                    text = contenido.textoBoton,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 15.sp, // MURRIZTUA: testuaren tamaina
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // MURRIZTUA: askoz espazio gutxiago bukaeran
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Audio erreproduzitzailearen txartela konposatzen du
 * @param isPlaying Audio erreproduzitzen ari den ala ez
 * @param isLoading Audio kargatzen ari den ala ez
 * @param currentPosition Uneko posizioa milisegundutan
 * @param totalDuration Audioaren iraupen osoa milisegundutan
 * @param audioFinished Audioa amaitu den ala ez
 * @param tieneAudio Audio baliabidea baduen ala ez
 * @param onPlayPauseClick Play/pause botoiari klik egiterakoan deitzen den callback-a
 */
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
    /**
     * Denbora formatatzen du milisegundutatik MM:ss formatura
     * @param milliseconds Milisegundoak
     * @return Formateatutako denbora katea
     */
    fun formatTime(milliseconds: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
        shape = RoundedCornerShape(20.dp) // MURRIZTUA: ertzak gutxiago biribildu
    ) {
        Column(modifier = Modifier.padding(12.dp)) { // MURRIZTUA: barneko padding txikiagoa
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Play botoia soilik erakutsi audioa badago
                if (tieneAudio) {
                    Box(
                        modifier = Modifier
                            .size(48.dp) // MURRIZTUA: botoiaren tamaina
                            .clip(RoundedCornerShape(50))
                            .background(Color.White)
                            .clickable(enabled = !isLoading && !audioFinished, onClick = onPlayPauseClick),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp), // MURRIZTUA: spinnerraren tamaina
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
                                modifier = Modifier.size(24.dp) // MURRIZTUA: ikonoaren tamaina
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp)) // MURRIZTUA: espazio horizontala
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (tieneAudio) {
                            stringResource(R.string.audio_didactic)
                        } else {
                            stringResource(R.string.azalpena_label)
                        },
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp, // MURRIZTUA: testuaren tamaina
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
                        fontSize = 13.sp // MURRIZTUA: testuaren tamaina
                    )
                }

                if (tieneAudio && isPlaying) {
                    Text(
                        text = formatTime(totalDuration - currentPosition),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 13.sp, // MURRIZTUA: testuaren tamaina
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (tieneAudio && isPlaying && totalDuration > 0) {
                Spacer(modifier = Modifier.height(8.dp)) // MURRIZTUA: espazioa
                LinearProgressIndicator(
                    progress = currentPosition.toFloat() / totalDuration.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp), // MURRIZTUA: barraren altuera
                    color = MaterialTheme.colorScheme.onPrimary,
                    trackColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}