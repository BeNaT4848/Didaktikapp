package com.example.errenteriaapp.navigation.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.errenteriaapp.R
import kotlin.collections.plusAssign
import kotlin.let
import kotlin.math.roundToInt

// Data classes para el juego
data class WasteItem(
    val id: Int,
    val name: String,
    val correctCategory: WasteCategory,
    val imageResId: Int
)

enum class WasteCategory(
    val color: Color,
    val displayName: String,
    val description: String,
    val imageResId: Int // Imagen del contenedor
) {
    YELLOW(
        Color(0xFFFFEB3B),
        "🟡 Ontzi horia",
        "Plastikoa, latak",
        R.drawable.ontzi_horia
    ),
    BLUE(
        Color(0xFF2196F3),
        "🔵 Ontzi urdina",
        "Papera eta kartoi",
        R.drawable.ontzi_urdina
    ),
    BROWN(
        Color(0xFF795548),
        "🟤 Hondakin organikoak",
        "Janari hondarrak",
        R.drawable.hondakin_organikoak
    ),
    BLACK(
        Color(0xFF424242),
        "⚫ Hondakin inorganikoak",
        "Birziklatu ezinak",
        R.drawable.hondakin_organikoak
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PapresaScreen(navController: NavController) {
    val wasteItems = remember {
        mutableStateListOf(
            WasteItem(1, "Botella de agua", WasteCategory.YELLOW, R.drawable.botella_agua),
            WasteItem(2, "Lata de refresco", WasteCategory.YELLOW, R.drawable.lata_refresco),
            WasteItem(3, "Bolsa de patatas", WasteCategory.YELLOW, R.drawable.bolsa_patatas),
            WasteItem(4, "Tapon de plastico", WasteCategory.YELLOW, R.drawable.tapones_plastico),
            WasteItem(5, "Yogurt", WasteCategory.YELLOW, R.drawable.yogurt),
            WasteItem(6, "Caja de cereales", WasteCategory.BLUE, R.drawable.caja_cereales),
            WasteItem(7, "Periodico", WasteCategory.BLUE, R.drawable.periodico),
            WasteItem(8, "Cuaderno de papel", WasteCategory.BLUE, R.drawable.cuaderno_papel),
            WasteItem(9, "Tubo de carton", WasteCategory.BLUE, R.drawable.tubo_carton),
            WasteItem(10, "Sobre", WasteCategory.BLUE, R.drawable.sobre),
            WasteItem(11, "Piel de fruta", WasteCategory.BROWN, R.drawable.piel_fruta),
            WasteItem(12, "Restos de verduras", WasteCategory.BROWN, R.drawable.restos_verduras),
            WasteItem(13, "Pan", WasteCategory.BROWN, R.drawable.pan),
            WasteItem(14, "Huesos", WasteCategory.BROWN, R.drawable.huesos),
            WasteItem(15, "Sobras de comida", WasteCategory.BROWN, R.drawable.sobras),
            WasteItem(16, "Chicle", WasteCategory.BLACK, R.drawable.chicle),
            WasteItem(17, "Colillas", WasteCategory.BLACK, R.drawable.colillas),
            WasteItem(18, "Compresa", WasteCategory.BLACK, R.drawable.gorro_sanitario),
            WasteItem(19, "Tiritas", WasteCategory.BLACK, R.drawable.tiritas),
            WasteItem(20, "Panal", WasteCategory.BLACK, R.drawable.panal)
        )
    }

    var currentIndex by remember { mutableStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, WasteCategory>() }
    val allAnswered = remember { derivedStateOf { wasteItems.all { userAnswers.containsKey(it.id) } } }
    var showResults by remember { mutableStateOf(false) }

    Scaffold(

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF4A460)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "PAPRESA JOLASA",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )

            // CONTADOR
            Text(
                text = "${currentIndex + 1}/${wasteItems.size}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // FOTO ACTUAL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                if (wasteItems.isNotEmpty() && currentIndex < wasteItems.size) {
                    val currentItem = wasteItems[currentIndex]

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Flecha izquierda
                        IconButton(
                            onClick = {
                                currentIndex = (currentIndex - 1 + wasteItems.size) % wasteItems.size
                            }
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Aurrekoa",
                                modifier = Modifier.size(40.dp),
                                tint = Color(0xFF2E7D32)
                            )
                        }

                        // FOTO
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .shadow(8.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .border(
                                    2.dp,
                                    Color.LightGray,
                                    RoundedCornerShape(16.dp)
                                )
                        ) {
                            Image(
                                painter = painterResource(id = currentItem.imageResId),
                                contentDescription = currentItem.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Indicador de respuesta
                            userAnswers[currentItem.id]?.let { category ->
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(24.dp)
                                        .background(category.color, CircleShape)
                                        .border(2.dp, Color.White, CircleShape)
                                ) {
                                    Text(
                                        text = "✓",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }

                        // Flecha derecha
                        IconButton(
                            onClick = {
                                currentIndex = (currentIndex + 1) % wasteItems.size
                            }
                        ) {
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = "Hurrengoa",
                                modifier = Modifier.size(40.dp),
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    }

                    // Nombre del objeto
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = currentItem.name,
                            modifier = Modifier
                                .background(Color(0xFF4CAF50))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TEXTO INSTRUCTIVO
            Text(
                text = "Sakatu ontzi egokia:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // CONTENEDORES (CLICABLES)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(140.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WasteCategory.values().forEach { category ->
                    // Contenedor clicable
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(
                                3.dp,
                                if (wasteItems.getOrNull(currentIndex)?.let { userAnswers[it.id] == category } == true)
                                    category.color
                                else Color.LightGray,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                // Cuando se hace clic en un contenedor
                                val currentItem = wasteItems.getOrNull(currentIndex)
                                currentItem?.let {
                                    userAnswers[it.id] = category
                                    // Pasar automáticamente a la siguiente foto
                                    if (currentIndex < wasteItems.size - 1) {
                                        currentIndex++
                                    }
                                }
                            }
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Icono del contenedor
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(category.color.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category.displayName.take(2),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = category.color
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Nombre
                        Text(
                            text = category.displayName.split(" ")[1],
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = category.color,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Descripción corta
                        Text(
                            text = category.description,
                            fontSize = 10.sp,
                            color = Color.DarkGray,
                            maxLines = 2,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // INFORMACIÓN DE RESPUESTA ACTUAL
            if (wasteItems.isNotEmpty() && currentIndex < wasteItems.size) {
                val currentItem = wasteItems[currentIndex]

                userAnswers[currentItem.id]?.let { currentCategory ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFF1F8E9))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(currentCategory.color, CircleShape)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Hautatuta: ${currentCategory.displayName}",
                                color = currentCategory.color,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    userAnswers.remove(currentItem.id)
                                },
                                modifier = Modifier.height(36.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Aldatu", fontSize = 14.sp)
                            }
                        }
                    }
                } ?: run {
                    Text(
                        text = "Hautatu non bota behar den",
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // BOTÓN DE VERIFICAR
            Button(
                onClick = {
                    if (allAnswered.value) {
                        showResults = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .height(56.dp),
                enabled = allAnswered.value,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Egiaztatu", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "EGIAZTATU (${
                        if (allAnswered.value) "Guztiak prest"
                        else "${userAnswers.size}/${wasteItems.size}"
                    })",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // DIALOGO DE RESULTADOS
    if (showResults) {
        val correctAnswers = wasteItems.count { item ->
            userAnswers[item.id] == item.correctCategory
        }
        val total = wasteItems.size
        val score = (correctAnswers.toFloat() / total * 100).roundToInt()
        val resultImage = when {
            score == 100 -> R.drawable.ondo_egina
            score >= 80 -> R.drawable.parte1
            score >= 60 -> R.drawable.parte2
            score >= 40 -> R.drawable.parte3
            else -> R.drawable.saiatu_berriro
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Imagen de resultado
                    Image(
                        painter = painterResource(id = resultImage),
                        contentDescription = "Emaitza",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(bottom = 16.dp),
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        text = when {
                            score == 100 -> "ONDO EGINA! 🎉"
                            score >= 80 -> "OSO ONDO! 👍"
                            score >= 60 -> "ONDO 😊"
                            score >= 40 -> "FALTAN ZENBAIT 💪"
                            else -> "SAIATU BERRIRO! 💪"
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            score == 100 -> Color(0xFFFF9800)
                            score >= 80 -> Color(0xFF4CAF50)
                            score >= 60 -> Color(0xFF2196F3)
                            score >= 40 -> Color(0xFFFF9800)
                            else -> Color(0xFFF44336)
                        },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "$score%",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Text(
                        text = "$correctAnswers/$total zuzen",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { showResults = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Berrikusi")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = { navController.navigate("nextScreen") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Hurrengoa")
                        }
                    }
                }
            }
        }
    }
}