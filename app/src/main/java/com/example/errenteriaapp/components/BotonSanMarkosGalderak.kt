package com.example.errenteriaapp.components



import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuizNextButton(
    currentQuestionIndex: Int,
    totalQuestions: Int,
    correctAnswers: Int,
    isAnswered: Boolean,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onNextClick,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        enabled = isAnswered,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (currentQuestionIndex == totalQuestions - 1) {
                if (correctAnswers >= 2) Color(0xFF4CAF50) else Color(0xFF2196F3)
            } else {
                Color(0xFF2196F3)
            }
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (currentQuestionIndex < totalQuestions - 1) "Hurrengo galdera" else "Amaitu proba",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            if (currentQuestionIndex < totalQuestions - 1) {
                Text(
                    text = "→",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}