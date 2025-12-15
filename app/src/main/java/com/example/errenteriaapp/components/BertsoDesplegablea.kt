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
import androidx.compose.ui.text.style.TextAlign
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
            .padding(horizontal = 4.dp, vertical = 12.dp)
            .background(Color(ColorBox), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .border(
                            width = 0.8.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(backgroundFor(a), RoundedCornerShape(20.dp))
                        .clickable(enabled = selectedOption == null) { onOptionSelected(a) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = a,
                        color = textColorFor(a),
                        fontSize = 15.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 6.dp)
                            .fillMaxWidth(),
                        maxLines = 2,
                        softWrap = true,
                        textAlign = TextAlign.Center
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .border(
                            width = 0.8.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(backgroundFor(b), RoundedCornerShape(20.dp))
                        .clickable(enabled = selectedOption == null) { onOptionSelected(b) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = b,
                        color = textColorFor(b),
                        fontSize = 15.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 6.dp)
                            .fillMaxWidth(),
                        maxLines = 2,
                        softWrap = true,
                        textAlign = TextAlign.Center
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .border(
                            width = 0.8.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(backgroundFor(c), RoundedCornerShape(20.dp))
                        .clickable(enabled = selectedOption == null) { onOptionSelected(c) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = c,
                        color = textColorFor(c),
                        fontSize = 15.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 6.dp)
                            .fillMaxWidth(),
                        maxLines = 2,
                        softWrap = true,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}