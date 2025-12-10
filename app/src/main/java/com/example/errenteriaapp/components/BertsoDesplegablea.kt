package com.example.errenteriaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BertsoDesplegablea(
    a: String,
    b: String,
    c: String,
    ColorBox: Long,
    selectedOption: String?,
    isCorrectSelection: Boolean?,
    onOptionSelected: (String) -> Unit
) {
    val successColor = Color(0xFF4CAF50)
    val errorColor = Color(0xFFE53935)

    fun backgroundFor(option: String) = when {
        option == selectedOption && isCorrectSelection == true -> successColor
        option == selectedOption && isCorrectSelection == false -> errorColor
        else -> Color.White
    }

    fun textColorFor(option: String) = if (option == selectedOption && isCorrectSelection != null) {
        Color.White
    } else {
        Color.Black
    }

    Box (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color(ColorBox), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                        .border(
                            width = 0.01.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(backgroundFor(a), RoundedCornerShape(20.dp))
                        .clickable(enabled = selectedOption == null) { onOptionSelected(a) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(a, color = textColorFor(a), fontSize = 14.sp)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .border(
                            width = 0.01.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(backgroundFor(b), RoundedCornerShape(20.dp))
                        .clickable(enabled = selectedOption == null) { onOptionSelected(b) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(b, color = textColorFor(b), fontSize = 14.sp)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .border(
                            width = 0.01.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(backgroundFor(c), RoundedCornerShape(20.dp))
                        .clickable(enabled = selectedOption == null) { onOptionSelected(c) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(c, color = textColorFor(c), fontSize = 14.sp)
                }
            }
        }
    }
}