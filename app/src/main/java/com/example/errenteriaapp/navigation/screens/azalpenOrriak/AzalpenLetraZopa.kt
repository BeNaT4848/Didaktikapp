package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.errenteriaapp.R

/**
 * Letra-zopa (sopa de letras) jokoaren azalpen pantaila konposatzen du
 * @param onClose Azalpena ixteko erabiltzen den callback-a
 * @param onNavigateToGame Jokora nabigatzeko erabiltzen den callback-a
 * @see AzalpenContenido
 * @see AzalpenBase
 */
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
            1000L to 0  // 1 segunduan 0. irudira aldatu
        ),

        tituloTexto = "",

        textoDidactico = "",

        textoBoton = stringResource(R.string.azalpen_start_game)
    )

    AzalpenBase(
        contenido = contenidoLetraZopa,
        onClose = onClose,
        onNavigateToGame = onNavigateToGame
    )
}