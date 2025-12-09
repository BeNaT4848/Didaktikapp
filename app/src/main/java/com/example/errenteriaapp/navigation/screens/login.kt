package com.example.errenteriaapp.navigation.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.errenteriaapp.R
import com.example.errenteriaapp.components.*
import com.example.errenteriaapp.viewModel.LoginViewModel


@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,

) {
    var nombreCompleto by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val isSaving = loginViewModel.isSaving.collectAsState()

    BackgroundChurch()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(Modifier.height(30.dp)) // Subimos todo

            LoginTitle()

            Spacer(Modifier.height(20.dp))

            LoginTextField(
                value = nombreCompleto,
                onChange = {
                    nombreCompleto = it
                    errorMessage = ""
                },
                isError = errorMessage.isNotEmpty()
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(text = errorMessage, color = Color(0xFFFF6B6B))
            }

            Spacer(Modifier.height(30.dp))

            LoginDivider()

            Spacer(Modifier.height(30.dp))

            LoginPlaySection(
                enabled = nombreCompleto.trim().isNotEmpty() && !isSaving.value,
                isSaving = isSaving.value,
                onClick = {
                    if (nombreCompleto.isBlank()) {
                        errorMessage = "Mesedez, idatzi zure izena eta abizena"
                    } else {
                        loginViewModel.guardarNombre(nombreCompleto)
                        // Navegamos a la pantalla del mapa
                        navController.navigate("MAPA_SCREEN")
                    }
                }
            )
        }

        // 🔥 Personajes pequeños en la parte inferior como en Home
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            CharacterItemLogin(R.drawable.xanti_silla_hablando, "Xanti")

            Spacer(modifier = Modifier.width(50.dp)) // 🔥 Ajusta aquí separación

            CharacterItemLogin(R.drawable.maialen_silla_hablando, "Maialen")
        }

    }
}
