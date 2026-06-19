package com.example.errenteriaapp.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Elkarrizketa-burbuila animatua erakusten du.
 * Testua letra-letra animatzen du idazte-efektua emateko.
 *
 * @param text Burbuilak erakutsiko duen testua
 * @param isFromXanti Xanti pertsonaikoak bidaltzen duen mezua den (false bada, Maialen pertsonaikoak bidaltzen du)
 * @param modifier Modifier gehigarria
 * @param isSpeaking Burbuilak testua idazten ari den (animazioa erakusteko)
 */
@Composable
fun SpeechBubble(
    text: String,
    isFromXanti: Boolean,
    modifier: Modifier = Modifier,
    isSpeaking: Boolean = true
) {
    // Animaziorako testua gordetzen duen egoera
    var displayedText by remember { mutableStateOf("") }

    // Idazte-animazioa
    LaunchedEffect(text) {
        if (text.isNotEmpty()) {
            displayedText = ""
            for (i in text.indices) {
                // Idazte-abiadura (doitu delay-a behar den arabera)
                delay(62L) // 30ms letra bakoitzeko
                displayedText = text.take(i + 1)
            }
        }
    }

    // Testua aldatu denean garbitu
    LaunchedEffect(text) {
        if (text.isEmpty()) {
            displayedText = ""
        }
    }

    Card(
        modifier = modifier,
        // Pertsonaiaren arabera burbuilaren forma aldatu
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
            text = displayedText,
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}