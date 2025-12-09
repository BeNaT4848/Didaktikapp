package com.example.errenteriaapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.errenteriaapp.R

@Composable
fun CharacterWithSpeech(
    isSpeaking: Boolean,
    isXanti: Boolean,
    message: String?,
    modifier: Modifier = Modifier,
    imageHeight: Dp
) {
    Column(
        modifier = modifier.widthIn(max = 220.dp),
        horizontalAlignment = if (isXanti) Alignment.Start else Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (isSpeaking && !message.isNullOrEmpty()) {
            SpeechBubble(text = message, isFromXanti = isXanti)
        }

        Image(
            painter = if (isXanti) {
                if (isSpeaking) painterResource(R.drawable.xanti_silla_hablando)
                else painterResource(R.drawable.xanti_silla)
            } else {
                if (isSpeaking) painterResource(R.drawable.maialen_silla_hablando)
                else painterResource(R.drawable.maialen_silla)
            },
            contentDescription = if (isXanti) "Xanti" else "Maialen",
            modifier = Modifier
                .height(imageHeight)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }
}