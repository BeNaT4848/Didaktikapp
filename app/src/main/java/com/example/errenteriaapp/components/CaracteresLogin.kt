package com.example.errenteriaapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CharacterItemLogin(
    imageRes: Int,
    name: String,
    imageHeight: androidx.compose.ui.unit.Dp
) {
    Image(
        painter = painterResource(imageRes),
        contentDescription = name,
        modifier = Modifier
            .fillMaxWidth()
            .height(imageHeight),
        contentScale = ContentScale.Fit
    )
}