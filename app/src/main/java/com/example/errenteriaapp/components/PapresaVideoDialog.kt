package com.example.errenteriaapp.components.video

import androidx.compose.ui.draw.blur
import android.content.pm.ActivityInfo

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

import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.errenteriaapp.R
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.media3.common.PlaybackException

@OptIn(UnstableApi::class)
@Composable
fun VideoDialogoa(
    onVideoCompleted: () -> Unit
) {
    val context = LocalContext.current
    fun Context.findActivity(): Activity {
        var ctx = this
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        throw IllegalStateException("Activity not found")
    }
    val activity = context.findActivity()

    // 🔄 GIRAR PANTALLA SOLO AQUÍ
    DisposableEffect(Unit) {
        val previousOrientation = activity.requestedOrientation

        // 👉 Forzar horizontal real
        activity.requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        onDispose {
            // 🔒 Volver a vertical
            activity.requestedOrientation = previousOrientation
        }
    }

    var hasPlaybackError by remember { mutableStateOf(false) }
    var isReady by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val rawUri = RawResourceDataSource.buildRawResourceUri(R.raw.papresa_bideoa)
            setMediaItem(MediaItem.fromUri(rawUri))

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
                    hasPlaybackError = true
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


                        // Evita bordes blancos
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT


                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .fillMaxHeight(0.70f)

            )
        }

        if (hasPlaybackError) {
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

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

}
