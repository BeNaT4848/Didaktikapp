package com.example.errenteriaapp.components

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.errenteriaapp.R

@Composable
fun GameResultDialogs(
    showSuccess: Boolean,
    showWrong: Boolean,
    onDismissSuccess: () -> Unit,
    onDismissWrong: () -> Unit,
    onSuccessButton: () -> Unit,
    onWrongButton: () -> Unit
) {
    if (showSuccess) {
        ResultDialog(
            isSuccess = true,
            imageRes = R.drawable.ondo_egina,
            audioRes = R.raw.oso_ondo_audioa,
            buttonText = stringResource(R.string.game_result_success_button),
            buttonColor = Color(0xFF4CAF50),
            onDismiss = onDismissSuccess,
            onButtonClick = onSuccessButton
        )
    }
    if (showWrong) {
        ResultDialog(
            isSuccess = false,
            imageRes = R.drawable.saiatu_berriro,
            audioRes = R.raw.saiatu_berriro_audioa,
            buttonText = stringResource(R.string.game_result_retry_button),
            buttonColor = Color(0xFFC62828),
            onDismiss = onDismissWrong,
            onButtonClick = onWrongButton
        )
    }
}

@Composable
fun ResultDialog(
    isSuccess: Boolean,
    imageRes: Int,
    audioRes: Int,
    buttonText: String,
    buttonColor: Color,
    onDismiss: () -> Unit,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        mediaPlayer = MediaPlayer.create(context, audioRes).apply {
            setOnCompletionListener {
                it.release()
                mediaPlayer = null
            }
            start()
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .aspectRatio(0.8f),
            shape = RoundedCornerShape(32.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(if (isSuccess) 0.8f else 0.6f)
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = stringResource(
                            if (isSuccess) R.string.game_result_success_cd else R.string.game_result_retry_cd
                        ),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Button(
                    onClick = {
                        // Detener el audio antes de cerrar
                        mediaPlayer?.release()
                        mediaPlayer = null
                        onButtonClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(bottom = 24.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
