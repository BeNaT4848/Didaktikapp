package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R

/**
 * Galdetegiaren goiburua erakusten du.
 * San Markos galdetegiaren pantailarako erabiltzen da.
 */
@Composable
fun QuizHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 1.dp, top = 18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Izenburua
            Text(
                text = stringResource(R.string.sanmarkos_title),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Azpizenburua
            Text(
                text = stringResource(R.string.sanmarkos_subtitle),
                color = Color(0xFF4FC3F7),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}