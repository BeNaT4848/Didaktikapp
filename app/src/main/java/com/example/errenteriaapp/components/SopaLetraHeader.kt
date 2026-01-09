package com.example.errenteriaapp.components



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SopaHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = "ZENTENARIOA",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 9.dp, bottom = 2.dp)
        )

        Text(
            text = "Ze musika tresna entzun dira?",
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 13.dp)
        )
    }
}