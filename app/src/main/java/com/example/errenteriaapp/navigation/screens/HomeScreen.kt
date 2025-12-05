package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.errenteriaapp.R // Necesitas importar R para las imágenes
import com.example.errenteriaapp.navigation.Routes
import com.example.errenteriaapp.viewModel.ConversacionViewModel

// ConversationScreen.kt
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ConversacionViewModel = viewModel(),
    navController: NavController,

) {
    val state by viewModel.state.collectAsState()

    // Iniciar conversación cuando se carga la pantalla
    LaunchedEffect(Unit) {
        viewModel.startConversation()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()

    ) {
        Image(
            painter = painterResource(id = R.drawable.iglesia),
            contentDescription = null,
            contentScale = ContentScale.FillBounds, // Ajusta la imagen a toda la pantalla
            modifier = Modifier.matchParentSize()
        )

        // Fila principal con los personajes
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // XANTI (izquierda)
            CharacterWithSpeech(
                isSpeaking = state.currentMessage?.isFromXanti == true,
                isXanti = true,
                message = if (state.currentMessage?.isFromXanti == true)
                    state.currentMessage?.text else null
            )

            // MAIALEN (derecha)
            CharacterWithSpeech(
                isSpeaking = state.currentMessage?.isFromXanti == false,
                isXanti = false,
                message = if (state.currentMessage?.isFromXanti == false)
                    state.currentMessage?.text else null
            )
        }

        // Botón "Empezar Juego" (solo visible al final)
        if (state.showStartButton) {
            Button(
                onClick = {navController.navigate(Routes.GAME_SCREEN)},
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800), // Naranja
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "¡EMPEZAR EL JUEGO!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun CharacterWithSpeech(
    isSpeaking: Boolean,
    isXanti: Boolean,
    message: String?
) {
    // Declara aquí los recursos de imagen
    val xantiOpen = painterResource(id = R.drawable.xanti_silla_hablando) // Cambia por el nombre real
    val xantiClosed = painterResource(id = R.drawable.xanti_silla) // Cambia por el nombre real
    val maialenOpen = painterResource(id = R.drawable.maialen_silla_hablando) // Cambia por el nombre real
    val maialenClosed = painterResource(id = R.drawable.maialen_silla) // Cambia por el nombre real

    Column(
        horizontalAlignment = if (isXanti) Alignment.Start else Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.widthIn(max = 200.dp)
    ) {
        // Burbuja de diálogo (solo si está hablando)
        if (isSpeaking && !message.isNullOrEmpty()) {
            SpeechBubble(
                text = message,
                isFromXanti = isXanti,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .widthIn(min = 100.dp, max = 250.dp)
            )
        }

        // Imagen del personaje
        Image(
            painter = if (isXanti) {
                if (isSpeaking) xantiOpen else xantiClosed
            } else {
                if (isSpeaking) maialenOpen else maialenClosed
            },
            contentDescription = if (isXanti) "Xanti" else "Maialen",
            modifier = Modifier
                .height(450.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun SpeechBubble(
    text: String,
    isFromXanti: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(
            topStart = if (isFromXanti) 0.dp else 16.dp,
            topEnd = 16.dp,
            bottomStart = 16.dp,
            bottomEnd = if (isFromXanti) 16.dp else 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}
