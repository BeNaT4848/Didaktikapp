package com.example.errenteriaapp.components.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.errenteriaapp.R

/**
 * Papresa jokoaren bideoaren argibideen dialogoa erakusten du.
 * Erabiltzaileari bideo bat ikusteko eskatzen dio.
 * Dialogo hau ezin da itxi - erabiltzaileak botoia sakatu behar du.
 *
 * @param onWatchVideo Bideoa ikusteko botoian klik egitean deitzen den funtzioa
 */
@Composable
fun InstruccionesVideoPapresa(
    onWatchVideo: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            // EZ DEZU EGIN EZER - Ezin da itxi
        },
        properties = DialogProperties(
            dismissOnBackPress = false,      // Atzeko botoiaz ezin da itxi
            dismissOnClickOutside = false    // Kanpoan klik egitean ezin da itxi
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(320.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Trofeoaren ikonoa
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Color(0xFFFFF3CD), // Hori argia
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 40.sp
                    )
                }

                // Testu edukia
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Izenburua
                    Text(
                        text = stringResource(R.string.papresa_video_title),
                        color = Color(0xFF1A2C4A),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Argibide-kutxa
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD) // Urdin argia
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.papresa_video_hint),
                                color = Color(0xFF2196F3),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Bideoa ikusteko botoia
                Button(
                    onClick = onWatchVideo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.papresa_video_watch),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}