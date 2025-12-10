package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.errenteriaapp.navigation.Routes
import com.example.errenteriaapp.viewModel.ConversacionViewModel

@Composable
fun MapaScreen(
    navController: NavController
) {
    println("Mapa Screen")


    Button(
        onClick = { navController.navigate(Routes.ORDENATUJOLASA_SCREEN) },
        modifier = Modifier
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF9800),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "Juego Ordenatu Jolasa",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
