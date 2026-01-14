package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import androidx.compose.runtime.Composable
import com.example.errenteriaapp.R

@Composable
fun AzalpenArrastrar(
    onClose: () -> Unit,
    onNavigateToGame: () -> Unit
) {
    val contenidoArrastrar = AzalpenContenido(
        audioResource = R.raw.xanti_eta_maialen_audioa,

        imagenesAudio = listOf(
            R.drawable.xanti_eta_maialen_erraldoiak
        ),
        timelineAudio = listOf(
            1000L to 0
        ),

        tituloTexto = "",

        textoDidactico = "",

        textoBoton = "Jolasten hasi"
    )

    AzalpenBase(
        contenido = contenidoArrastrar,
        onClose = onClose,
        onNavigateToGame = onNavigateToGame
    )
}