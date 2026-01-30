package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.errenteriaapp.R

@Composable
fun AzalpenPapresa(
    onClose: () -> Unit,
    onNavigateToGame: () -> Unit
) {
    val contenidoPapresa = AzalpenContenido(
        audioResource = null,
        imagenesAudio = emptyList(),
        timelineAudio = emptyList(),

        tituloTexto = stringResource(R.string.azalpen_papresa_title),
        textoDidactico = stringResource(R.string.azalpen_papresa_body),
        textoBoton = stringResource(R.string.azalpen_start_puzzle)
    )

    AzalpenBase(
        contenido = contenidoPapresa,
        onClose = onClose,
        onNavigateToGame = onNavigateToGame
    )
}