package com.example.errenteriaapp.navigation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LetraSopaScreen(
    navController: NavController
) {
    // Estado del juego
    val palabras = remember {
        listOf(
            "SAXOFOLA", // Fila 0, columnas 4-11
            "ZEHARTXIRULA", // Fila 6, columnas 0-11
            "KLARINETEA", // Fila 10, columnas 0-9
            "DANBORRA", // Fila 11, columnas 0-7
            "TXINDATAK", // Fila 11, columnas 1-9
            "TRONPETA", // Fila 8, columnas 3-10
            "TRONPAN", // Fila 13, columnas 4-10
            "TRONBOIA" // Fila 13, columnas 6-13
        )
    }

    // Contador de pistas disponibles (empezamos con 1 pista)
    val pistasDisponibles = remember { mutableStateOf(1) }

    // Palabras encontradas
    val palabrasEncontradas = remember { mutableStateListOf<String>() }

    // Estado para mostrar mensaje de éxito
    val mostrarExito = remember { mutableStateOf(false) }

    // Animación de confeti
    val confettiScale = remember { mutableStateOf(0f) }

    // Tablero exacto como en la imagen
    val tablero = remember {
        arrayOf(
            charArrayOf('T', 'H', 'H', 'S', 'A', 'X', 'O', 'F', 'O', 'I', 'A', 'Z', 'T', 'A'),
            charArrayOf('Q', 'B', 'P', 'B', 'W', 'U', 'G', 'S', 'Y', 'P', 'R', 'J', 'X', 'A'),
            charArrayOf('P', 'M', 'K', 'J', 'G', 'X', 'E', 'G', 'K', 'O', 'E', 'R', 'I', 'C'),
            charArrayOf('I', 'Q', 'Q', 'L', 'B', 'S', 'T', 'R', 'O', 'N', 'P', 'A', 'N', 'K'),
            charArrayOf('U', 'H', 'M', 'L', 'N', 'E', 'R', 'F', 'I', 'G', 'M', 'D', 'D', 'I'),
            charArrayOf('O', 'A', 'G', 'R', 'H', 'G', 'M', 'G', 'X', 'D', 'H', 'X', 'A', 'M'),
            charArrayOf('Z', 'E', 'H', 'A', 'R', 'T', 'X', 'I', 'R', 'U', 'L', 'A', 'T', 'C'),
            charArrayOf('X', 'C', 'Y', 'O', 'U', 'E', 'A', 'K', 'V', 'V', 'T', 'B', 'A', 'J'),
            charArrayOf('C', 'N', 'C', 'T', 'R', 'O', 'N', 'P', 'E', 'T', 'A', 'A', 'K', 'C'),
            charArrayOf('I', 'L', 'V', 'Y', 'G', 'G', 'E', 'Y', 'G', 'S', 'L', 'G', 'J', 'I'),
            charArrayOf('K', 'L', 'A', 'R', 'I', 'N', 'E', 'T', 'E', 'A', 'J', 'I', 'D', 'U'),
            charArrayOf('D', 'A', 'N', 'B', 'O', 'R', 'R', 'A', 'U', 'F', 'X', 'H', 'I', 'D'),
            charArrayOf('I', 'B', 'A', 'W', 'E', 'W', 'H', 'K', 'C', 'X', 'C', 'S', 'W', 'X'),
            charArrayOf('M', 'Z', 'X', 'C', 'T', 'T', 'R', 'O', 'N', 'B', 'O', 'I', 'A', 'T')
        )
    }

    // Posiciones de las palabras (TODAS HORIZONTALES)
    val posicionesPalabras = remember {
        mapOf(
            "SAXOFOLA" to listOf(
                Pair(0, 3), Pair(0, 4), Pair(0, 5), Pair(0, 6),
                Pair(0, 7), Pair(0, 8), Pair(0, 9), Pair(0, 10)
            ), //ondo

            "ZEHARTXIRULA" to listOf(
                Pair(6, 0), Pair(6, 1), Pair(6, 2), Pair(6, 3),
                Pair(6, 4), Pair(6, 5), Pair(6, 6), Pair(6, 7),
                Pair(6, 8), Pair(6, 9), Pair(6, 10), Pair(6, 11)
            ),//ondo

            "KLARINETEA" to listOf(
                Pair(10, 0), Pair(10, 1), Pair(10, 2), Pair(10, 3),
                Pair(10, 4), Pair(10, 5), Pair(10, 6), Pair(10, 7),
                Pair(10, 8), Pair(10, 9)
            ),//ondo

            "DANBORRA" to listOf(
                Pair(11, -1), Pair(11, 0), Pair(11, 1), Pair(11, 2),
                Pair(11, 3), Pair(11, 4), Pair(11, 5), Pair(11, 6),Pair(11, 7)
            ), //ondo

            "TXINDATAK" to listOf(
                Pair(0, 12), Pair(1, 12), Pair(2, 12), Pair(3, 12),
                Pair(4, 12), Pair(5, 12), Pair(6, 12), Pair(7, 12), Pair(8, 12)
            ),

            "TRONPETA" to listOf(
                Pair(8, 3), Pair(8, 4), Pair(8, 5), Pair(8, 6),
                Pair(8, 7), Pair(8, 8), Pair(8, 9), Pair(9, 10)
            ),

            "TRONPAN" to listOf(
                Pair(3, 6), Pair(3, 7), Pair(3, 8), Pair(3, 9),
                Pair(3, 10), Pair(3, 11), Pair(3, 12)
            ),

            "TRONBOIA" to listOf(
                Pair(13, 5), Pair(13, 6), Pair(13, 7), Pair(13, 8),
                Pair(13, 9), Pair(13, 10), Pair(13, 11), Pair(13, 12)
            )//ondo
        )
    }

    // Estado para mostrar pista temporalmente
    val mostrarPista = remember { mutableStateOf(false) }
    val palabraPista = remember { mutableStateOf<String?>(null) }

    // Colores para palabras encontradas
    val coloresPalabras = mapOf(
        "SAXOFOLA" to Color(0xFFFFD700),     // Amarillo
        "ZEHARTXIRULA" to Color(0xFFFFD700), // Amarillo
        "KLARINETEA" to Color(0xFFFFD700),   // Amarillo
        "DANBORRA" to Color(0xFFFFD700),     // Amarillo
        "TXINDATAK" to Color(0xFFFFD700),    // Amarillo
        "TRONPETA" to Color(0xFFFFD700),     // Amarillo
        "TRONPAN" to Color(0xFFFFD700),      // Amarillo
        "TRONBOIA" to Color(0xFFFFD700)      // Amarillo
    )

    val scope = rememberCoroutineScope()
    val densidad = LocalDensity.current



    // Función para marcar palabra encontrada
    fun marcarPalabraEncontrada(palabra: String) {
        if (!palabrasEncontradas.contains(palabra)) {
            palabrasEncontradas.add(palabra)

            // Verificar si todas las palabras fueron encontradas
            if (palabrasEncontradas.size == palabras.size) {
                scope.launch {
                    delay(500)
                    mostrarExito.value = true

                    // Animación de confeti
                    confettiScale.value = 0f
                    repeat(3) {
                        confettiScale.value = 1f
                        delay(300)
                        confettiScale.value = 0.8f
                        delay(300)
                    }

                    // Esperar y navegar al siguiente juego
                    delay(2000)
                    navController.navigate("siguienteJuego") // Cambia esto por tu ruta real
                }
            }
        }
    }

    // Función para verificar si una celda está en una palabra específica
    fun celdaPerteneceAPalabra(fila: Int, col: Int, palabra: String): Boolean {
        return posicionesPalabras[palabra]?.contains(Pair(fila, col)) ?: false
    }

    // Efecto para limpiar pista
    LaunchedEffect(mostrarPista.value) {
        if (mostrarPista.value) {
            delay(2000)
            mostrarPista.value = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Sopa de Letras Musical",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Encuentra los 8 instrumentos",
                color = Color(0xFFBB86FC),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Barra de progreso y pistas
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1E1E)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Progreso
                    Column {
                        Text(
                            text = "Aurrerapena",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${palabrasEncontradas.size}/${palabras.size}",
                            color = Color(0xFFBB86FC),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }



                }
            }

            // Tablero de sopa de letras
            Box(
                modifier = Modifier
                    .border(2.dp, Color(0xFF3700B3), RoundedCornerShape(8.dp))
                    .background(Color(0xFF1A1A1A))
                    .padding(6.dp)
            ) {
                Column {
                    for (fila in 0 until 14) {
                        Row {
                            for (columna in 0 until 14) {
                                val celda = Pair(fila, columna)
                                val letra = tablero[fila][columna]

                                // Verificar si esta celda pertenece a alguna palabra encontrada
                                var esEncontrada = false
                                var palabraDeEstaCelda: String? = null

                                for ((palabra, posiciones) in posicionesPalabras) {
                                    if (posiciones.contains(celda)) {
                                        palabraDeEstaCelda = palabra
                                        if (palabrasEncontradas.contains(palabra)) {
                                            esEncontrada = true
                                            break
                                        }
                                    }
                                }

                                // Color de fondo
                                val colorFondo = if (esEncontrada) {
                                    Color.Yellow.copy(alpha = 0.8f)
                                } else {
                                    Color.Transparent
                                }

                                // Color del texto
                                val colorTexto = if (esEncontrada) {
                                    Color.Black
                                } else {
                                    Color.White
                                }

                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .border(0.5.dp, Color(0xFF444444))
                                        .background(colorFondo)
                                        .clickable {
                                            // Si la celda pertenece a una palabra, marcarla como encontrada
                                            palabraDeEstaCelda?.let { palabra ->
                                                marcarPalabraEncontrada(palabra)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = letra.toString(),
                                        color = colorTexto,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Lista de palabras encontradas
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1E1E)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Instrumentos encontrados:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Grid de palabras (2 columnas)
                    val columnas = 2
                    val filasNecesarias = (palabras.size + columnas - 1) / columnas

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        for (fila in 0 until filasNecesarias) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (col in 0 until columnas) {
                                    val index = fila * columnas + col
                                    if (index < palabras.size) {
                                        val palabra = palabras[index]
                                        val encontrada = palabrasEncontradas.contains(palabra)

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = 4.dp, vertical = 4.dp)
                                        ) {
                                            InstrumentoItem(
                                                nombre = palabra,
                                                encontrado = encontrada,
                                                color = Color.Yellow
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Overlay de pista
        if (mostrarPista.value && palabraPista.value != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFD700),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "PISTA:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = palabraPista.value ?: "",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFFD32F2F)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "¡Encuentra esta palabra en el tablero!",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Mensaje de éxito con confeti
        if (mostrarExito.value) {
            ConfetiAnimation(scale = confettiScale.value, densidad = densidad)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎉 ¡LO HAS CONSEGUIDO! 🎉",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFFFFD700),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "¡Has encontrado todos los instrumentos!",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Divider(
                            color = Color(0xFFFFD700),
                            thickness = 2.dp,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        Text(
                            text = "Pasando al siguiente juego...",
                            fontSize = 14.sp,
                            color = Color(0xFFBB86FC),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Animación de carga
                        CircularProgressIndicator(
                            color = Color(0xFFFFD700),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InstrumentoItem(
    nombre: String,
    encontrado: Boolean,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (encontrado) color.copy(alpha = 0.2f) else Color.Transparent
        ),
        border = BorderStroke(
            1.dp,
            if (encontrado) color else Color.Gray.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de estado
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (encontrado) color else Color.Transparent)
                    .border(
                        1.5.dp,
                        if (encontrado) color else Color.Gray,
                        CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Nombre del instrumento
            Text(
                text = nombre,
                color = if (encontrado) color else Color.White,
                fontWeight = if (encontrado) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )

            // Checkmark si está encontrado
            if (encontrado) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "✓",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ConfetiAnimation(scale: Float, densidad: Density) {
    val colors = listOf(
        Color(0xFFFFD700),  // Amarillo oro
        Color(0xFFFF9800),  // Naranja
        Color(0xFFFF5252),  // Rojo
        Color(0xFF4CAF50),  // Verde
        Color(0xFF2196F3),  // Azul
        Color(0xFF9C27B0),  // Púrpura
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Crear múltiples partículas de confeti
        repeat(40) { index ->
            val offsetX = remember { (-100..100).random().toFloat() }
            val offsetY = remember { (-100..100).random().toFloat() }
            val rotation = remember { (0..360).random().toFloat() }
            val sizeDp = remember { (6..16).random().dp }
            val delay = remember { (0..1000).random().toLong() }
            val infiniteTransition = rememberInfiniteTransition(label = "")

            val animatedOffset by infiniteTransition.animateFloat(
                initialValue = -80f,
                targetValue = 80f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1600 + delay.toInt(),
                        easing = LinearEasing,
                        delayMillis = delay.toInt()
                    ),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )

            val animatedRotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2200 + delay.toInt(),
                        easing = LinearEasing,
                        delayMillis = delay.toInt()
                    )
                ), label = ""
            )

            // Convertir offset a dp
            val offsetXDp = with(densidad) { (animatedOffset * 0.6f + offsetX).toDp() }
            val offsetYDp = with(densidad) { (animatedOffset * 0.4f + offsetY).toDp() }

            Box(
                modifier = Modifier
                    .offset(x = offsetXDp, y = offsetYDp)
                    .size(sizeDp)
                    .rotate(rotation + animatedRotation)
                    .background(
                        colors[index % colors.size],
                        shape = when (index % 4) {
                            0 -> RoundedCornerShape(0.dp)      // Cuadrado
                            1 -> CircleShape                   // Círculo
                            2 -> RoundedCornerShape(4.dp)     // Rectángulo redondeado
                            else -> RoundedCornerShape(8.dp)  // Cuadrado redondeado
                        }
                    )
            )
        }
    }
}