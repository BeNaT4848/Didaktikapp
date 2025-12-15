package com.example.errenteriaapp.components.video

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

    // Limpiar el reproductor
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Dialog(
        onDismissRequest = {
            // NO HACER NADA - No se puede cerrar
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {

        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false  // Sin controles
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL

                    // Configuración para evitar que se detenga
                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)

                    // Asegurar que el video se siga reproduciendo
                    setKeepContentOnPlayerReset(true)
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)  // Más ancho
                .fillMaxHeight(0.6f)   // Más alto
                .background(Color.Black)
        )
    }
}