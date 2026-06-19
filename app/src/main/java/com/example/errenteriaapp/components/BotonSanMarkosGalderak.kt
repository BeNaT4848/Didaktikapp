package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
 * Galdetegiaren "Hurrengoa" botoia erakusten du.
 * Erabiltzaileak galdera bat erantzun duenean aktibatzen da.
 * Azken galdera botoiaren kolorea eta testua aldatzen dira.
 *
 * @param currentQuestionIndex Uneko galderaren indizea (0tik hasita)
 * @param totalQuestions Galdera kopuru osoa
 * @param correctAnswers Zuzen erantzundako galdera kopurua
 * @param isAnswered Erabiltzaileak galdera erantzun duen
 * @param onNextClick Botoian klik egitean deitzen den funtzioa
 * @param modifier Modifier gehigarria
 */
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
        // Kolorea egoeraren arabera aldatzen da
        colors = ButtonDefaults.buttonColors(
            containerColor = if (currentQuestionIndex == totalQuestions - 1) {
                // Azken galdera: puntuazioaren arabera kolorea
                if (correctAnswers >= 2) Color(0xFF4CAF50) else Color(0xFF2196F3)
            } else {
                Color(0xFF2196F3) // Galdera arruntak
            }
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botoiaren testua egoeraren arabera
            Text(
                text = if (currentQuestionIndex < totalQuestions - 1) {
                    stringResource(R.string.sanmarkos_next_question)
                } else {
                    stringResource(R.string.sanmarkos_finish_quiz)
                },
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Gezi ikonoa ez da azken galderan erakusten
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