package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import androidx.compose.runtime.Composable
import com.example.errenteriaapp.R

@Composable
fun AzalpenLetraZopa(
    onClose: () -> Unit,
    onNavigateToGame: () -> Unit
) {
    val contenidoLetraZopa = AzalpenContenido(
        audioResource = R.raw.zentenarioa_audioa,

        imagenesAudio = listOf(
            R.drawable.txupinazoa
        ),
        timelineAudio = listOf(
            1000L to 0
        ),

        tituloTexto = "",

        textoDidactico = "",

        textoBoton = "Jolasten hasi"
    )

    AzalpenBase(
        contenido = contenidoLetraZopa,
        onClose = onClose,
        onNavigateToGame = onNavigateToGame
    )
}