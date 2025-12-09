package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginPlaySection(
    enabled: Boolean,
    isSaving: Boolean,
    onClick: () -> Unit
) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = "ZOAZ JOLASTERA",
            fontSize = 28.sp,
            color = Color.White
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = onClick,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF4A460),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .height(55.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isSaving)
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            else
                Text("HASI JOLASA", fontSize = 18.sp)
        }
    }
}
