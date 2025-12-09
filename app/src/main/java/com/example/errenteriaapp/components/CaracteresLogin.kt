package com.example.errenteriaapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R


@Composable
fun CharacterItemLogin(imageRes: Int, name: String) {
    Image(
        painter = painterResource(imageRes),
        contentDescription = name,
        modifier = Modifier
            .height(350.dp) // tamaño ajustado para que no sea tan grande como Home
            .width(180.dp)
            .padding(horizontal = 12.dp),
        contentScale = ContentScale.Fit
    )
}
