package com.example.errenteriaapp.navigation.screens

import android.R.attr.maxHeight
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.errenteriaapp.R // Necesitas importar R para las imágenes
import com.example.errenteriaapp.components.CharacterWithSpeech
import com.example.errenteriaapp.navigation.Routes
import com.example.errenteriaapp.viewModel.ConversacionViewModel
import kotlin.times

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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        val imageHeight = maxHeight * 0.6f

        Image(
            painter = painterResource(id = R.drawable.iglesia),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Button(
            onClick = { navController.navigate(Routes.LOGIN_SCREEN) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Skip",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        // Fila principal con los personajes
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .offset(y = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            // XANTI (izquierda)
            CharacterWithSpeech(
                isSpeaking = state.currentMessage?.isFromXanti == true,
                isXanti = true,
                message = state.currentMessage?.text,
                modifier = Modifier.weight(1f),
                imageHeight = imageHeight
            )
            // MAIALEN (derecha)
            CharacterWithSpeech(
                isSpeaking = state.currentMessage?.isFromXanti == false,
                isXanti = false,
                message = state.currentMessage?.text,
                modifier = Modifier.weight(1f),
                imageHeight = imageHeight
            )
        }

        // Botón "Empezar Juego" (solo visible al final)
        if (state.showStartButton) {
            Button(
                onClick = {
                    viewModel.onStartButtonClicked()
                    navController.navigate(Routes.LOGIN_SCREEN)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),
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