package com.example.errenteriaapp.components



import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VerifyButton(
    allAnswered: Boolean,
    answeredCount: Int,
    totalCount: Int,
    onVerifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {


    Button(
        onClick = onVerifyClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .height(56.dp),
        enabled = allAnswered,
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.Check, contentDescription = "Egiaztatu", modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "EGIAZTATU (${
                if (allAnswered) "Guztiak prest"
                else "$answeredCount/$totalCount"
            })",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}