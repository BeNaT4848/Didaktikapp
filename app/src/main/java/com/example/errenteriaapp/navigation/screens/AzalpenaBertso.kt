package com.example.errenteriaapp.navigation.screens

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import java.io.IOException

@Composable
fun EchoesGameScreen(
    navController: NavController
) {
    val context = LocalContext.current

    // Estado para controlar la reproducción
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    // Gestionar el ciclo de vida del MediaPlayer
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
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

        // Title
        Text(
            text = "Xenpelar Etxea",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Audio intro (con funcionalidad)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1E3A2F))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF3DFF8F))
                    .clickable {
                        if (isPlaying) {
                            // Pausar audio
                            mediaPlayer?.pause()
                            isPlaying = false
                        } else {
                            // Reproducir audio
                            if (mediaPlayer == null) {
                                // Cargar y reproducir por primera vez
                                isLoading = true
                                try {
                                    val mp =
                                        MediaPlayer.create(context, R.raw.xenpelar_etxea_audioa)
                                    mp.setOnPreparedListener {
                                        isLoading = false
                                        it.start()
                                        isPlaying = true
                                    }
                                    mp.setOnCompletionListener {
                                        isPlaying = false
                                        it.release()
                                        mediaPlayer = null
                                    }
                                    mediaPlayer = mp
                                } catch (e: IOException) {
                                    isLoading = false
                                    e.printStackTrace()
                                }
                            } else {
                                // Reanudar audio
                                mediaPlayer?.start()
                                isPlaying = true
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                } else {
                    if (isPlaying) {
                        Image(
                            painter = painterResource(id = R.drawable.pause),
                            contentDescription = "Pausar audio",
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.play_arrow),
                            contentDescription = "Iniciar audio",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "AUDIO DIDAKTIKOA",
                    color = Color(0xFF7CFFB2),
                    fontSize = 18.sp
                )
                Text(
                    text = "Xenpelar eta Txirrita",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.xenpelar_etxea),
                contentDescription = null,
                modifier = Modifier
                    .size(220.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {

            Image(
                painter = painterResource(id = R.drawable.xenpelar_eta_txirrita_1),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.xenpela_eta_txirrita_2),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

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

        // Botón (solo visual)
        Button(
            onClick = { navController.navigate(Routes.BERTSOJOLASA_SCREEN) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3DFF8F)
            ),
            shape = RoundedCornerShape(28.dp)
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

@Preview(showBackground = true)
@Composable
fun EchoesGameScreenPreview() {
    EchoesGameScreen(
        navController = rememberNavController()
    )
}