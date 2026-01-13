package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import androidx.compose.runtime.Composable
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
        tituloTexto = "Testu didaktikoa",
        textoDidactico = "Bertsoak ahoz egiten diren arren, bertso-paperei esker (bertsoak biltzen zituzten panfletoak) Euskal Herri osoan zehar banatu zitezkeen." +
                "Gainera, bertso-paperei esker, gaur bi bertsolari hauen bertsoak irakurri eta osatu ahal izango dituzue." +
                "Bertso hauek osatzeko, errimetan eta kontatzen ari diren istorioan fijatu zaitezte!",
        textoBoton = "Jolasten hasi"
    )

    AzalpenBase(
        contenido = contenidoBertso,
        onClose = onClose,
        onNavigateToGame = onNavigateToGame
    )
}