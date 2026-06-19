package com.example.errenteriaapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.errenteriaapp.R

/**
 * Eliza baten atzeko planoa erakusten du.
 * San Markos galdetegiaren pantailarako erabiltzen da.
 */
@Composable
fun BackgroundChurch() {
    Image(
        painter = painterResource(id = R.drawable.iglesia),
        contentDescription = "Fondo de iglesia",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}