package com.example.errenteriaapp.navigation.screens.azalpenOrriak

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.errenteriaapp.R

/**
 * Bertso-jokoaren azalpen pantaila konposatzen du
 * @param onClose Azalpena ixteko erabiltzen den callback-a
 * @param onNavigateToGame Jokora nabigatzeko erabiltzen den callback-a
 * @see AzalpenContenido
 * @see AzalpenBase
 */
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
            5000L to 0,   // 5 segunduan 0. irudira aldatu
            15000L to 1,  // 15 segunduan 1. irudira aldatu
            25000L to 2   // 25 segunduan 2. irudira aldatu
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