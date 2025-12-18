package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Datos para visualizar el crucigrama
data class CeldaVisual(
    val fila: Int,
    val columna: Int,
    val esNegra: Boolean = false,
    val numeroPista: Int? = null,
    val letra: Char? = null
)

@Composable
fun CrucigramaScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título principal
        Text(
            text = "GURUTZEGRAMA",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2196F3),
            modifier = Modifier.padding(bottom = 4.dp, top = 10.dp)
        )

        Text(
            text = "Aizpitarte-Hitz gurutzatuak",
            fontSize = 18.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Tablero del crucigrama visual (8x11)
        TableroCrucigramaVisual()

        Spacer(modifier = Modifier.height(30.dp))

        // Sección de pistas HORIZONTALES
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE8F5E9)
            ),
            border = BorderStroke(1.dp, Color(0xFF4CAF50))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "HORIZONTALAK",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Pista 1
                PistaVisualItem(
                    numero = 1,
                    texto = "Duela milaka urte kobazuloetan bizi ziren gizakiak."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Pista 3
                PistaVisualItem(
                    numero = 3,
                    texto = "Kobazuloetako hormetan margotzen zituzten animaliak eta sinboloak."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Pista 5
                PistaVisualItem(
                    numero = 5,
                    texto = "Haria lantzeko erabilizen zuten tresna."
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sección de pistas VERTICALES
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD)
            ),
            border = BorderStroke(1.dp, Color(0xFF2196F3))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "BERTIKALAK",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Pista 2
                PistaVisualItem(
                    numero = 2,
                    texto = "Historiaurrea ikertzen duen zientzialaria."
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Pista 4
                PistaVisualItem(
                    numero = 4,
                    texto = "Aizpitarteko kobazuloak dauden herria."
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Eglaxiatu (Corregir)
        Button(
            onClick = { /* Sin funcionalidad */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF9800),
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "EGIAZTATU",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }



        Spacer(modifier = Modifier.height(8.dp))

        // Instrucción
        Text(
            text = "Idatzi hizkiak gelaxketan eta sakatu 'EGLAXIATU' zure erantzunak egiaztatzeko",
            fontSize = 12.sp,
            color = Color.Gray,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun TableroCrucigramaVisual() {
    // Crear celdas visuales para el crucigrama 8x11 usando ArrayList
    val celdas = crearCeldasVisualesConArrayList()

    Column(
        modifier = Modifier
            .border(3.dp, Color.Black)
            .background(Color.White)
            .padding(1.dp)
    ) {
        // 11 filas
        for (fila in 0 until 11) {
            Row {
                // 8 columnas
                for (columna in 0 until 8) {
                    val celda = celdas[fila][columna]
                    CeldaVisualUI(celda = celda)
                }
            }
        }
    }
}

@Composable
fun CeldaVisualUI(celda: CeldaVisual) {
    Box(
        modifier = Modifier
            .size(35.dp)
            .border(1.dp, Color.Gray)
            .background(if (celda.esNegra) Color.Black else Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Número de pista (pequeño, esquina superior izquierda)
        if (celda.numeroPista != null) {
            Text(
                text = "${celda.numeroPista}",
                fontSize = 10.sp,
                color = Color.Blue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(2.dp)
            )
        }

        // Letra (si hay)
        celda.letra?.let { letra ->
            Text(
                text = letra.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PistaVisualItem(numero: Int, texto: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Número de pista con fondo circular
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(
                    color = if (numero % 2 == 1) Color(0xFFC8E6C9) else Color(0xFFBBDEFB),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .border(
                    1.dp,
                    if (numero % 2 == 1) Color(0xFF4CAF50) else Color(0xFF2196F3),
                    androidx.compose.foundation.shape.CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$numero",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (numero % 2 == 1) Color(0xFF2E7D32) else Color(0xFF1565C0)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Texto de la pista
        Text(
            text = texto,
            fontSize = 15.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}

// Función para crear las celdas visuales del crucigrama 8x11 usando ArrayList
fun crearCeldasVisualesConArrayList(): ArrayList<ArrayList<CeldaVisual>> {
    // Crear ArrayList de ArrayLists
    val grid = ArrayList<ArrayList<CeldaVisual>>()

    // Fila 0
    val fila0 = ArrayList<CeldaVisual>()
    fila0.add(CeldaVisual(0, 0, esNegra = true))
    fila0.add(CeldaVisual(0, 1, esNegra = true))
    fila0.add(CeldaVisual(0, 2, esNegra = false, numeroPista = 1, letra = 'L'))
    fila0.add(CeldaVisual(0, 3, esNegra = false, letra = 'E'))
    fila0.add(CeldaVisual(0, 4, esNegra = false, letra = 'I'))
    fila0.add(CeldaVisual(0, 5, esNegra = false, letra = 'Z'))
    fila0.add(CeldaVisual(0, 6, esNegra = false, numeroPista = 3, letra = 'E'))
    fila0.add(CeldaVisual(0, 7, esNegra = false, letra = 'A'))
    grid.add(fila0)

    // Fila 1
    val fila1 = ArrayList<CeldaVisual>()
    fila1.add(CeldaVisual(1, 0, esNegra = true))
    fila1.add(CeldaVisual(1, 1, esNegra = true))
    fila1.add(CeldaVisual(1, 2, esNegra = true))
    fila1.add(CeldaVisual(1, 3, esNegra = false, letra = 'S'))
    fila1.add(CeldaVisual(1, 4, esNegra = true))
    fila1.add(CeldaVisual(1, 5, esNegra = true))
    fila1.add(CeldaVisual(1, 6, esNegra = false, letra = 'S'))
    fila1.add(CeldaVisual(1, 7, esNegra = true))

    grid.add(fila1)

    // Fila 2
    val fila2 = ArrayList<CeldaVisual>()
    fila2.add(CeldaVisual(2, 0, esNegra = true))
    fila2.add(CeldaVisual(2, 1, esNegra = false,numeroPista = 4, letra = 'R'))
    fila2.add(CeldaVisual(2, 2, esNegra = true))
    fila2.add(CeldaVisual(2, 3, esNegra = false,  letra = 'T'))
    fila2.add(CeldaVisual(2, 4, esNegra = true))
    fila2.add(CeldaVisual(2, 5, esNegra = true))
    fila2.add(CeldaVisual(2, 6, esNegra = false, letra = 'T'))
    fila2.add(CeldaVisual(2, 7, esNegra = true))
    grid.add(fila2)

    // Fila 3
    val fila3 = ArrayList<CeldaVisual>()
    fila3.add(CeldaVisual(3, 0, esNegra = false, numeroPista = 5, letra = 'Z'))
    fila3.add(CeldaVisual(3, 1, esNegra = false, letra = 'U'))
    fila3.add(CeldaVisual(3, 2, esNegra = false, letra = 'T'))
    fila3.add(CeldaVisual(3, 3, esNegra = false, letra = 'A'))
    fila3.add(CeldaVisual(3, 4, esNegra = false, letra = 'B'))
    fila3.add(CeldaVisual(3, 5, esNegra = false, letra = 'E'))
    fila3.add(CeldaVisual(3, 6, esNegra = false, letra = 'A'))
    fila3.add(CeldaVisual(3, 7, esNegra = true))

    grid.add(fila3)

    // Fila 4
    val fila4 = ArrayList<CeldaVisual>()
    fila4.add(CeldaVisual(4, 0, esNegra = true))
    fila4.add(CeldaVisual(4, 1, esNegra = false,  letra = 'P'))
    fila4.add(CeldaVisual(4, 2, esNegra = true))
    fila4.add(CeldaVisual(4, 3, esNegra = false, letra = 'L'))
    fila4.add(CeldaVisual(4, 4, esNegra = true))
    fila4.add(CeldaVisual(4, 5, esNegra = true))
    fila4.add(CeldaVisual(4, 6, esNegra = false, letra = 'L'))
    fila4.add(CeldaVisual(4, 7, esNegra = true))

    grid.add(fila4)

    // Fila 5
    val fila5 = ArrayList<CeldaVisual>()
    fila5.add(CeldaVisual(5, 0, esNegra = true))
    fila5.add(CeldaVisual(5, 1, esNegra = false, letra = 'E'))
    fila5.add(CeldaVisual(5, 1, esNegra = true))
    fila5.add(CeldaVisual(5, 2, esNegra = false, letra = 'A'))
    fila5.add(CeldaVisual(5, 3, esNegra = true))
    fila5.add(CeldaVisual(5, 4, esNegra = true))
    fila5.add(CeldaVisual(5, 5, esNegra = false, letra = 'A'))
    fila5.add(CeldaVisual(5, 6, esNegra = true))
    grid.add(fila5)

    // Fila 6
    val fila6 = ArrayList<CeldaVisual>()
    fila6.add(CeldaVisual(6, 0, esNegra = true))
    fila6.add(CeldaVisual(6, 1, esNegra = false, letra = 'S'))
    fila6.add(CeldaVisual(6, 2, esNegra = true))
    fila6.add(CeldaVisual(6, 3, esNegra = false, letra = 'G'))
    fila6.add(CeldaVisual(6, 4, esNegra = true))
    fila6.add(CeldaVisual(6, 5, esNegra = true))
    fila6.add(CeldaVisual(6, 6, esNegra = false, letra = 'K'))
    fila6.add(CeldaVisual(6, 7, esNegra = true))
    grid.add(fila6)

    // Fila 7
    val fila7 = ArrayList<CeldaVisual>()
    fila7.add(CeldaVisual(7, 0, esNegra = true))
    fila7.add(CeldaVisual(7, 1, esNegra = false, letra = 'T'))
    fila7.add(CeldaVisual(7, 2, esNegra = true))
    fila7.add(CeldaVisual(7, 3, esNegra = false, letra = 'M'))
    fila7.add(CeldaVisual(7, 4, esNegra = true))
    fila7.add(CeldaVisual(7, 5, esNegra = true))
    fila7.add(CeldaVisual(7, 6, esNegra = false, letra = 'T'))
    fila7.add(CeldaVisual(7, 7, esNegra = true))
    grid.add(fila7)

    // Fila 8
    val fila8 = ArrayList<CeldaVisual>()
    fila8.add(CeldaVisual(8, 0, esNegra = true))
    fila8.add(CeldaVisual(8, 1, esNegra = false, letra = 'R'))
    fila8.add(CeldaVisual(8, 2, esNegra = true))
    fila8.add(CeldaVisual(8, 3, esNegra = false, letra = 'I'))
    fila8.add(CeldaVisual(8, 4, esNegra = true))
    fila8.add(CeldaVisual(8, 5, esNegra = true))
    fila8.add(CeldaVisual(8, 6, esNegra = false, letra = 'I'))
    fila8.add(CeldaVisual(8, 7, esNegra = true))
    grid.add(fila8)

    // Fila 9
    val fila9 = ArrayList<CeldaVisual>()
    fila9.add(CeldaVisual(9, 0, esNegra = true))
    fila9.add(CeldaVisual(9, 1, esNegra = false, letra = 'E'))
    fila9.add(CeldaVisual(9, 2, esNegra = true))
    fila9.add(CeldaVisual(9, 3, esNegra = false, letra = 'T'))
    fila9.add(CeldaVisual(9, 4, esNegra = true))
    fila9.add(CeldaVisual(9, 5, esNegra = true))
    fila9.add(CeldaVisual(9, 6, esNegra = false, letra = 'T'))
    fila9.add(CeldaVisual(9, 7, esNegra = true))
    grid.add(fila9)

    // Fila 10
    val fila10 = ArrayList<CeldaVisual>()
    fila10.add(CeldaVisual(10, 0, esNegra = true))
    fila10.add(CeldaVisual(10, 1, esNegra = true))
    fila10.add(CeldaVisual(10, 2, esNegra = true))
    fila10.add(CeldaVisual(10, 3, esNegra = false, letra = 'A'))
    fila10.add(CeldaVisual(10, 4, esNegra = true))
    fila10.add(CeldaVisual(10, 5, esNegra = true))
    fila10.add(CeldaVisual(10, 6, esNegra = false, letra = 'A'))
    fila10.add(CeldaVisual(10, 7, esNegra = true))
    grid.add(fila10)

    return grid
}