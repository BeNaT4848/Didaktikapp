package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.errenteriaapp.R

@Composable
fun AzalpenOrdenatu(
    onClose: () -> Unit,
    onNavigateToGame: () -> Unit
) {
    val contenidoOrdenatu = AzalpenContenido(
        audioResource = R.raw.errota_audioa,

        imagenesAudio = listOf(
            R.drawable.errota
        ),
        timelineAudio = listOf(
            1000L to 0
        ),

        tituloTexto = "",

        textoDidactico = "",

        textoBoton = stringResource(R.string.azalpen_start_game)
    )

    AzalpenBase(
        contenido = contenidoOrdenatu,
        onClose = onClose,
        onNavigateToGame = onNavigateToGame
    )
}