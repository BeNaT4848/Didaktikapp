package com.example.errenteriaapp.components

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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

/**
 * Jokoaren emaitzen dialogoa erakusten du (zuzena edo okerra).
 * Bi egoera kudeatzen ditu: arrakasta-dialogoa eta errore-dialogoa.
 *
 * @param showSuccess Arrakasta-dialogoa erakutsi behar den
 * @param showWrong Errore-dialogoa erakutsi behar den
 * @param onDismissSuccess Arrakasta-dialogoa ixtean deitzen den funtzioa
 * @param onDismissWrong Errore-dialogoa ixtean deitzen den funtzioa
 * @param onSuccessButton Arrakasta-dialogoko botoian klik egitean deitzen den funtzioa
 * @param onWrongButton Errore-dialogoko botoian klik egitean deitzen den funtzioa
 */
@Composable
fun GameResultDialogs(
    showSuccess: Boolean,
    showWrong: Boolean,
    onDismissSuccess: () -> Unit,
    onDismissWrong: () -> Unit,
    onSuccessButton: () -> Unit,
    onWrongButton: () -> Unit
) {
    // Arrakasta-dialogoa
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
    // Errore-dialogoa
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

/**
 * Emaitzen dialogoa erakusten du (zuzena edo okerra).
 * Audioa erreproduzitzen du eta irudi bat erakusten du.
 *
 * @param isSuccess Dialogoa arrakasta-dialogoa den (false bada, errore-dialogoa)
 * @param imageRes Erakutsiko den irudiaren baliabide-identifikadorea
 * @param audioRes Errreproduzituko den audioaren baliabide-identifikadorea
 * @param buttonText Botoian erakutsiko den testua
 * @param buttonColor Botoiaren kolorea
 * @param onDismiss Dialogoa ixtean deitzen den funtzioa
 * @param onButtonClick Botoian klik egitean deitzen den funtzioa
 * @param modifier Modifier gehigarria
 */
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

    // Audioa erreproduzitu dialogoa irekitzean
    LaunchedEffect(Unit) {
        mediaPlayer = MediaPlayer.create(context, audioRes).apply {
            setOnCompletionListener {
                it.release()
                mediaPlayer = null
            }
            start()
        }
    }

    // Audioa askatu dialogoa ixtean
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
                // Irudia
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

                // Botoia
                Button(
                    onClick = {
                        // Audioa gelditu dialogoa ixtean
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