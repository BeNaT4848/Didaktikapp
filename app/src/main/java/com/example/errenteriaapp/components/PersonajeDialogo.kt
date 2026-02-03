package com.example.errenteriaapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.errenteriaapp.R

/**
 * Pertsonaia bat erakusten du burbuila batekin (hitz egiten badu).
 * Xanti edo Maialen pertsonaia erakusten du, egoeraren arabera.
 *
 * @param isSpeaking Pertsonaia hitz egiten ari den
 * @param isXanti Xanti pertsonaia den (false bada, Maialen da)
 * @param message Pertsonaiak esaten duen mezua (null bada, ez da mezurik)
 * @param modifier Modifier gehigarria
 * @param imageHeight Irudiaren altuera
 */
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
        // Burbuila erakutsi hitz egiten ari bada eta mezua badu
        if (isSpeaking && !message.isNullOrEmpty()) {
            SpeechBubble(
                text = message,
                isFromXanti = isXanti,
                isSpeaking = isSpeaking
            )
        }

        // Pertsonaiaren irudia (egoeraren arabera)
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