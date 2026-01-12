package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.errenteriaapp.navigation.Routes
import com.example.errenteriaapp.navigation.screens.azalpenOrriak.AzalpenBertso
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Imports para BottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    navController: NavController
) {
    // Estados
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBertsoSheet by remember { mutableStateOf(false) }

    // Forzar altura al abrir
    LaunchedEffect(showBertsoSheet) {
        if (showBertsoSheet) {
            delay(100) // Pequeño delay para asegurar renderizado
            sheetState.expand() // Forzar expansión máxima
        }
    }

    // Contenido principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E9))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Botones (todos igual excepto Bertso)
        Button(
            onClick = { navController.navigate(Routes.ORDENATUJOLASA_SCREEN) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Juego Ordenatu Jolasa", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        // BOTÓN BERTSO - Abre Bottom Sheet
        Button(
            onClick = { showBertsoSheet = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Juego Bertso Jolasa", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = { navController.navigate(Routes.BASURA_SCREEN) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Juego Basura", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = { navController.navigate(Routes.SOPALETRA_SCREEN) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Juego Letra Sopa", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = { navController.navigate(Routes.SANMARKOS_SCREEN) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("San Markoseko Galderak", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = { navController.navigate(Routes.TAULAARRASTRAR_SCRENN) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Taula Arrastatu Jolasa", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = { navController.navigate(Routes.CRUCIGRAMA_SCREEN) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Gurutzegrama Jokoa", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = { navController.navigate(Routes.GPS_SCREEN) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("GPS", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }

    // BOTTOM SHEET
    if (showBertsoSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBertsoSheet = false
            },
            sheetState = sheetState,
            dragHandle = null,
            containerColor = Color(0xFF0E1B14),
            scrimColor = Color.Black.copy(alpha = 0.4f)
        ) {
            // CONTENIDO CON ALTURA FORZADA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalConfiguration.current.screenHeightDp.dp * 0.48f)
            ) {
                AzalpenBertso(
                    onClose = {
                        scope.launch {
                            sheetState.hide()
                            delay(300)
                            showBertsoSheet = false
                        }
                    },
                    onNavigateToGame = {
                        scope.launch {
                            sheetState.hide()
                            delay(300)
                            showBertsoSheet = false
                            navController.navigate(Routes.BERTSOJOLASA_SCREEN)
                        }
                    }
                )
            }
        }
    }
}