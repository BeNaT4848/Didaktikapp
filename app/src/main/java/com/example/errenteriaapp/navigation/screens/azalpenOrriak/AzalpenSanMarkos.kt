package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import androidx.compose.runtime.Composable
import com.example.errenteriaapp.R

@Composable
fun AzalpenSanMarkos(
    onClose: () -> Unit,
    onNavigateToGame: () -> Unit
) {
    val contenidoSanMarkos = AzalpenContenido(
        audioResource = R.raw.san_markoseko_gotorlekua_audioa,

        imagenesAudio = emptyList(),

        timelineAudio = emptyList(),

        tituloTexto = "",

        textoDidactico = "",

        textoBoton = "Jarraitu"
    )

    AzalpenBase(
        contenido = contenidoSanMarkos,
        onClose = onClose,
        onNavigateToGame = onNavigateToGame
    )
}