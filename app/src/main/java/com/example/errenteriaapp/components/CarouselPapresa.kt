package com.example.errenteriaapp.components



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.classes.WasteCategory
import com.example.errenteriaapp.classes.WasteItem


@Composable
fun PhotoCarousel(
    wasteItems: List<WasteItem>,
    currentIndex: Int,
    userAnswers: Map<Int, WasteCategory>,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        if (wasteItems.isNotEmpty() && currentIndex < wasteItems.size) {
            val currentItem = wasteItems[currentIndex]

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flecha izquierda
                IconButton(onClick = onPreviousClick) {
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
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }

                // Flecha derecha
                IconButton(onClick = onNextClick) {
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
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}