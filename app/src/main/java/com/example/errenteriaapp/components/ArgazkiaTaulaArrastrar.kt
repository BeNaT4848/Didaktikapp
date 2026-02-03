package com.example.errenteriaapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * "Taula Arrastrar" pantailarako irudi konposatzailea.
 * Xanti eta Maialen pertsonaien irudi erraldoia erakusten du.
 * Irudia ez badago, mezu bat erakusten du.
 */
@Composable
fun ArgazkiaTaulaArrastrar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            .border(2.dp, Color.Gray, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        val context = LocalContext.current
        // Irudiaren baliabide-identifikadorea bilatu izenaren arabera
        val resourceId = context.resources.getIdentifier(
            "xanti_eta_maialen_erraldoiak",
            "drawable",
            context.packageName
        )

        if (resourceId != 0) {
            // Irudia erakutsi baldin badago
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = "Xanti eta Maialen Erraldoiak",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Irudia ez bada aurkitu, mezu bat erakutsi
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Imagen no encontrada")
                Text("xanti_eta_maialen_erraldoiak")
            }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}