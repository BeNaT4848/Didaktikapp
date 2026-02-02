package com.example.errenteriaapp.components.video

import androidx.compose.ui.draw.blur
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.C
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultRenderersFactory

import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.errenteriaapp.R
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.media3.common.PlaybackException
import androidx.media3.common.MimeTypes

@OptIn(UnstableApi::class)
@Composable
fun VideoDialogoa(
    onVideoCompleted: () -> Unit
) {
    val context = LocalContext.current

    var playbackError by remember { mutableStateOf<PlaybackException?>(null) }
    var isReady by remember { mutableStateOf(false) }
    var playerKey by remember { mutableStateOf(0) }

    val exoPlayer = remember(playerKey) {
        val renderersFactory = DefaultRenderersFactory(context)
            .setEnableDecoderFallback(true)

        ExoPlayer.Builder(context, renderersFactory).build().apply {
            val rawUri = RawResourceDataSource.buildRawResourceUri(R.raw.papresa_bideoa)
            val mediaItem = MediaItem.Builder()
                .setUri(rawUri)
                .setMimeType(MimeTypes.VIDEO_MP4)
                .build()

            setMediaItem(mediaItem)

            repeatMode = Player.REPEAT_MODE_OFF
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY) {
                        isReady = true
                    }
                    if (state == Player.STATE_ENDED) {
                        onVideoCompleted()
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    playbackError = error
                }
            })

            prepare()
            playWhenReady = true
        }
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        // Fondo borroso
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(25.dp) // Intensidad del blur
                .background(Color.Black.copy(alpha = 0.4f)) // Semi-transparencia para oscurecer
        )

        // Contenido central: video
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        keepScreenOn = true

                        // Mantener video en vertical sin depender de rotación
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.70f)

            )
        }

        if (playbackError != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .background(Color(0xCC000000), RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.papresa_video_error),
                        color = Color.White,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            playbackError = null
                            isReady = false
                            playerKey += 1
                        }
                    ) {
                        Text(text = stringResource(R.string.papresa_video_retry))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onVideoCompleted) {
                        Text(text = stringResource(R.string.papresa_video_continue))
                    }
                }
            }
        } else if (!isReady) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.papresa_video_loading),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

}
