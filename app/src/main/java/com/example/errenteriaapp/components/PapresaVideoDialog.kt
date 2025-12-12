package com.example.errenteriaapp.components.video

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.errenteriaapp.R

@OptIn(UnstableApi::class)
@Composable
fun VideoDialogoa(
    onDismiss: () -> Unit,
    onVideoCompleted: () -> Unit
) {
    val context = LocalContext.current

    // Crear y configurar el reproductor ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            // Configurar el video desde recursos raw
            val rawUri = RawResourceDataSource.buildRawResourceUri(R.raw.papresa_bideoa)
            val mediaItem = MediaItem.fromUri(rawUri)

            setMediaItem(mediaItem)

            // Configurar para reproducir una sola vez
            repeatMode = Player.REPEAT_MODE_OFF

            // Detectar cuando el video termina
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        onVideoCompleted()
                    }
                }
            })

            prepare()
            playWhenReady = true
        }
    }

    // Controlar si se puede adelantar
    LaunchedEffect(Unit) {
        // Deshabilitar controles de timeline
        // Nota: Esto requiere un controlador personalizado
    }

    // Limpiar el reproductor
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Dialog(
        onDismissRequest = {
            exoPlayer.release()
            onDismiss()
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .aspectRatio(16f / 9f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Reproductor de video SIN controles
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false  // Sin controles
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                            // Mostrar buffering
                            setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )


            }
        }
    }
}