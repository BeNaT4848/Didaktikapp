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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R

/**
 * Gurutze-hitzaren argibideen dialogoa erakusten du.
 * Jokoaren argibideak erakusten ditu puntu-zerrenda batean.
 *
 * @param onDismiss Dialogoa ixtean deitzen den funtzioa
 */
@Composable
fun InstructionsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.game_crucigrama_instructions_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2196F3)
            )
        },
        text = {
            Column {
                // Goiburua
                Text(
                    text = stringResource(R.string.game_crucigrama_instructions_label),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Argibideen zerrenda
                InstructionItem(stringResource(R.string.game_crucigrama_instruction_1))
                InstructionItem(stringResource(R.string.game_crucigrama_instruction_2))
                InstructionItem(stringResource(R.string.game_crucigrama_instruction_3))
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                )
            ) {
                Text(stringResource(R.string.game_crucigrama_understood))
            }
        },
        containerColor = Color.White,
        shape = MaterialTheme.shapes.large
    )
}

/**
 * Argibide-elementu bat erakusten du puntu-zerrendan.
 * @param text Argibidearen testua
 */
@Composable
private fun InstructionItem(text: String) {
    Row(
        modifier = Modifier.padding(bottom = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("• ", color = Color(0xFFFF9800)) // Puntu laranja
        Text(
            text = text,
            modifier = Modifier.weight(1f)
        )
    }
}