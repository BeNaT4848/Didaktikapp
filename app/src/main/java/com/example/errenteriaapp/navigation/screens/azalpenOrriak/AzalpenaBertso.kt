package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.errenteriaapp.R

@Composable
fun AzalpenBertso(
    onClose: () -> Unit,
    onNavigateToGame: () -> Unit
) {
    val contenidoBertso = AzalpenContenido(
        audioResource = R.raw.xenpelar_etxea_audioa,
        imagenesAudio = listOf(
            R.drawable.xenpelar_etxea,
            R.drawable.xenpelar_eta_txirrita_1,
            R.drawable.xenpela_eta_txirrita_2
        ),
        timelineAudio = listOf(
            5000L to 0,
            15000L to 1,
            25000L to 2
        ),
        tituloTexto = stringResource(R.string.azalpen_bertso_title),
        textoDidactico = stringResource(R.string.azalpen_bertso_body),
        textoBoton = stringResource(R.string.azalpen_start_game)
    )

    AzalpenBase(
        contenido = contenidoBertso,
        onClose = onClose,
        onNavigateToGame = onNavigateToGame
    )
}