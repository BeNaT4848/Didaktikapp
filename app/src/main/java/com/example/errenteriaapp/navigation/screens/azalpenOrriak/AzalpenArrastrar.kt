package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.errenteriaapp.R

/**
 * Arrastrar (eramate) jokoaren azalpen pantaila konposatzen du
 * @param onClose Azalpena ixteko erabiltzen den callback-a
 * @param onNavigateToGame Jokora nabigatzeko erabiltzen den callback-a
 * @see AzalpenContenido
 * @see AzalpenBase
 */
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
            1000L to 0  // 1 segunduan 0. irudira aldatu
        ),

        tituloTexto = "",

        textoDidactico = "",

        textoBoton = stringResource(R.string.azalpen_start_game)
    )

    AzalpenBase(
        contenido = contenidoArrastrar,
        onClose = onClose,
        onNavigateToGame = onNavigateToGame
    )
}