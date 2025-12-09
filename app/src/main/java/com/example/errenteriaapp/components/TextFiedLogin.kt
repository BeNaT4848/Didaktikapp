package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginTextField(
    value: String, onChange: (String) -> Unit, isError: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4A460)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(Modifier.padding(20.dp)) {

            Text(
                text = "Izena eta Abizena", color = Color.White, fontSize = 18.sp , modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = value,
                onValueChange = { input ->
                    val filtered = input.filter { it.isLetter() || it.isWhitespace() }
                    onChange(filtered)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Zure Izena eta Abizena", color = Color.White.copy(alpha = 0.7f))
                },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                isError = isError,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    errorBorderColor = Color.Red,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                )
            )
        }
    }
}
