package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InstructionsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "¡Bienvenido al Crucigrama!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3)
            )
        },
        text = {
            Column {
                Text(
                    text = "Instrucciones:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                InstructionItem("Haz clic en el NÚMERO de una pista para activar esa palabra")
                InstructionItem("Solo puedes escribir en la palabra activa (se marca en naranja)")
                InstructionItem("Presiona EGIAZTATU para verificar tus respuestas")
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Text("¡Entendido!")
            }
        },
        containerColor = Color.White,
        shape = MaterialTheme.shapes.large
    )
}

@Composable
private fun InstructionItem(text: String) {
    Row(
        modifier = Modifier.padding(bottom = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("• ", color = Color(0xFFFF9800))
        Text(
            text = text,
            modifier = Modifier.weight(1f)
        )
    }
}