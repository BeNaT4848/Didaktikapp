package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R

/**
 * Egiaztatzeko botoia erakusten du "Taula Arrastrar" jokoan.
 * Botoia aktibatu egiten da erabiltzaileak leku guztiak betetzen dituenean.
 *
 * @param allSlotsFilled Pertsonaia bakoitzeko leku guztiak beteta dauden
 * @param onVerifyClick Botoian klik egitean deitzen den funtzioa
 */
@Composable
fun VerifyButtonnn(
    allSlotsFilled: Boolean,
    onVerifyClick: () -> Unit
) {
    Button(
        onClick = onVerifyClick,
        enabled = allSlotsFilled,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (allSlotsFilled) Color(0xFF2196F3) else Color(0xFF90CAF9),
            contentColor = Color.White
        )
    ) {
        Text(
            text = stringResource(R.string.game_verify),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}