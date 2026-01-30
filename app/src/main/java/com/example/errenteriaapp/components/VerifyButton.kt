package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R

@Composable
fun VerifyButton(onClick: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(
            text = stringResource(R.string.game_verify),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}