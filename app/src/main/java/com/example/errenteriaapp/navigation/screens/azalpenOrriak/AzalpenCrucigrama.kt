package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import androidx.compose.runtime.Composable
import com.example.errenteriaapp.R

@Composable
fun AzalpenCrucigrama(
    onClose: () -> Unit,
    onNavigateToGame: () -> Unit
) {
    val contenidoCrucigrama = AzalpenContenido(
        audioResource = R.raw.aizpitarte_kobazuloa_audioa,

        imagenesAudio = listOf(
            R.drawable.aizpitarte_kobazuloa
        ),
        timelineAudio = listOf(
            1000L to 0
        ),

        tituloTexto = "",

        textoDidactico = "",

        textoBoton = "Jarraitu"
    )

    AzalpenBase(
        contenido = contenidoCrucigrama,
        onClose = onClose,
        onNavigateToGame = onNavigateToGame
    )
}