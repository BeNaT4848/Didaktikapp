package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import androidx.compose.runtime.Composable

@Composable
fun AzalpenPapresa(
    onClose: () -> Unit,
    onNavigateToGame: () -> Unit
) {
    val contenidoPapresa = AzalpenContenido(
        audioResource = null,
        imagenesAudio = emptyList(),
        timelineAudio = emptyList(),

        tituloTexto = "Papresa: Papergintza Errenterian",
        textoDidactico = "Jolasa bi zatitan dago:" +
                "\n\n" +
                "❶ **PUZLEA**: Papresa fabrikaren historia ikasiko duzu. " +
                "Puzzlea osatu eta irakurri fabrikaren historia." +
                "\n\n" +
                "❷ **BIRZIKLATZEA**: Puzzlea amaitutakoan, objektuak paperera egokian sartu beharko dituzu.",
        textoBoton = "Puzzlea hasi"
    )

    AzalpenBase(
        contenido = contenidoPapresa,
        onClose = onClose,
        onNavigateToGame = onNavigateToGame
    )
}