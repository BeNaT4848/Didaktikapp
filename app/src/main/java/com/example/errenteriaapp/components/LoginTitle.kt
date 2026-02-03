package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R

/**
 * Login pantailarako izenburua erakusten du.
 * Galdera bat eta azpizenburua erakusten ditu.
 */
@Composable
fun LoginTitle() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Izenburu nagusia (galdera)
        Text(
            text = stringResource(R.string.login_title_question),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        // Azpizenburua
        Text(
            text = stringResource(R.string.login_title_subtitle),
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )
    }
}