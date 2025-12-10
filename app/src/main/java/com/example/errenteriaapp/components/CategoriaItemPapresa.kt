package com.example.errenteriaapp.components



import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.classes.WasteCategory
import com.example.errenteriaapp.classes.WasteItem


@Composable
fun WasteContainersRow(
    currentWasteItem: WasteItem?,
    userAnswers: Map<Int, WasteCategory>,
    onContainerClick: (WasteCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
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
                        if (currentWasteItem?.let { userAnswers[it.id] == category } == true)
                            category.color
                        else Color.LightGray,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onContainerClick(category) }
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
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
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = category.color
                    )
                }

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(6.dp))

                // Nombre
                Text(
                    text = category.displayName.split(" ")[1],
                    fontSize = 12.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = category.color,
                    maxLines = 1
                )

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(4.dp))

                // Descripción corta
                Text(
                    text = category.description,
                    fontSize = 10.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}