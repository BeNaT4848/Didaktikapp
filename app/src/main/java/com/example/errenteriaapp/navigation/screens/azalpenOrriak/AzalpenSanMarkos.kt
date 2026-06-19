package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.errenteriaapp.R

/**
 * San Markos jokoaren azalpen pantaila konposatzen du
 * @param onClose Azalpena ixteko erabiltzen den callback-a
 * @param onNavigateToGame Jokora nabigatzeko erabiltzen den callback-a
 * @see AzalpenContenido
 * @see AzalpenBase
 */
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

        textoBoton = stringResource(R.string.azalpen_start_game)
    )

    AzalpenBase(
        contenido = contenidoSanMarkos,
        onClose = onClose,
        onNavigateToGame = onNavigateToGame
    )
}