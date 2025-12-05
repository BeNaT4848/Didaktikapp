package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.errenteriaapp.R
import com.example.errenteriaapp.viewModel.ConversacionViewModel

@Composable
fun GameScreen(
    conversacionViewModel: ConversacionViewModel,
    navController: NavController
    ) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.mapa),
            contentDescription = null,
            contentScale = ContentScale.FillBounds, // Ajusta la imagen a toda la pantalla
            modifier = Modifier.matchParentSize()
        )
    }
}