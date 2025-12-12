package com.example.errenteriaapp.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
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
import com.example.errenteriaapp.R

@Composable
fun GameResultDialogs(
    showSuccess: Boolean,
    showWrong: Boolean,
    onDismissSuccess: () -> Unit,
    onDismissWrong: () -> Unit,
    onSuccessButton: () -> Unit,
    onWrongButton: () -> Unit
) {
    if (showSuccess) {
        ResultDialog(
            isSuccess = true,
            imageRes = R.drawable.ondo_egina,
            buttonText = "Jolasekin jarraitu!",
            buttonColor = Color(0xFF4CAF50),
            onDismiss = onDismissSuccess,
            onButtonClick = onSuccessButton
        )
    }
    if (showWrong) {
        ResultDialog(
            isSuccess = false,
            imageRes = R.drawable.saiatu_berriro,
            buttonText = "Saiatu berriro!",
            buttonColor = Color(0xFFC62828),
            onDismiss = onDismissWrong,
            onButtonClick = onWrongButton
        )
    }
}

@Composable
fun ResultDialog(
    isSuccess: Boolean,
    imageRes: Int,
    buttonText: String,
    buttonColor: Color,
    onDismiss: () -> Unit,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
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
                        .weight(if (isSuccess) 0.8f else 0.6f)
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = if (isSuccess) "Ondo eginda" else "Saiatu berriro",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(bottom = 24.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
