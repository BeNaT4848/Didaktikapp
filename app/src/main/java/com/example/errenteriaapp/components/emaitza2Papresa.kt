package com.example.errenteriaapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.errenteriaapp.R

@Composable
fun Emaitza2Papresa(
    showSuccess: Boolean,
    showWrong: Boolean = false,
    onContinue: () -> Unit = {},
    onRetry: () -> Unit = {},
    allowDismiss: Boolean = false  // Añade este parámetr
) {
    Dialog(
        onDismissRequest = {
            if (allowDismiss) {
                if (showSuccess) onContinue() else onRetry()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .aspectRatio(0.8f),
            shape = RoundedCornerShape(32.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(if (showSuccess) 0.8f else 0.6f)
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(
                            id = if (showSuccess) R.drawable.ondo_egina else R.drawable.saiatu_berriro
                        ),
                        contentDescription = if (showSuccess) "Ondo eginda" else "Saiatu berriro",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Button(
                    onClick = {
                        if (showSuccess) onContinue() else onRetry()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(bottom = 24.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showSuccess) Color(0xFF4CAF50) else Color(0xFFC62828)
                    )
                ) {
                    Text(
                        text = if (showSuccess) "Jarraitu Mapara" else "Saiatu berriro",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}