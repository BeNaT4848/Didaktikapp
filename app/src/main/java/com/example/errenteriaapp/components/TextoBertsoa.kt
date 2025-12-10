package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun textoBertsoa(textobertsoa: String) {
    Text(
        text = textobertsoa,
        fontSize = 20.sp,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
    )
}