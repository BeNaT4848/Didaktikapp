package com.example.errenteriaapp.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.classes.WasteCategory
import com.example.errenteriaapp.classes.WasteItem

@Composable
fun AnswerInfoCard(
    currentWasteItem: WasteItem?,
    userAnswers: Map<Int, WasteCategory>,
    onChangeAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    currentWasteItem?.let { item ->
        userAnswers[item.id]?.let { currentCategory ->
            Card(
                modifier = modifier
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
                        onClick = onChangeAnswer,
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
                fontSize = 15.sp,
                modifier = modifier.padding(16.dp)
            )
        }
    }
}