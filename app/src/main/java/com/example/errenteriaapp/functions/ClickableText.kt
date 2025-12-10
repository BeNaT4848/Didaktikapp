package com.example.errenteriaapp.functions

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.components.BertsoDesplegablea
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.example.errenteriaapp.R

@Composable
fun ClickableTextFunction(
    fulltext: String,
    clickableword:String,
    act: String,
    bct: String,
    cct: String,
    colorBox: Long,
    correctAnswer: String
) {

    val fullText = fulltext
    val clickableWord = clickableword

    val context = LocalContext.current
    var showBertso by rememberSaveable { mutableStateOf(false) }
    var selectedOption by rememberSaveable { mutableStateOf<String?>(null) }
    var isCorrectSelection by rememberSaveable { mutableStateOf<Boolean?>(null) }

    val displayedWord = selectedOption ?: clickableWord

    fun handleSelection(option: String) {
        if (selectedOption != null) return
        selectedOption = option
        val isCorrect = option == correctAnswer
        isCorrectSelection = isCorrect
        val message = if (isCorrect) "Ongi" else "Erantzun Okerrra"

        val inflater = LayoutInflater.from(context)
        val toastLayout = inflater.inflate(R.layout.toast_feedback, null)
        val icon = toastLayout.findViewById<ImageView>(R.id.toast_icon)
        val text = toastLayout.findViewById<TextView>(R.id.toast_message)
        text.text = message
        icon.setImageResource(if (isCorrect) R.drawable.ic_toast_success else R.drawable.ic_toast_error)

        Toast(context).apply {
            duration = Toast.LENGTH_SHORT
            view = toastLayout
            show()
        }

        showBertso = false
    }

    val annotatedText = buildAnnotatedString {
        val start = fullText.indexOf(clickableWord)
        val end = start + clickableWord.length

        append(fullText.substring(0, start))

        pushStringAnnotation(
            tag = "CLICK",
            annotation = "pressed"
        )
        withStyle(
            SpanStyle(
                color = when (isCorrectSelection) {
                    true -> Color(0xFF4CAF50)
                    false -> Color(0xFFE53935)
                    else -> Color(0xFF1D3357)
                },
                textDecoration = if (isCorrectSelection == null) TextDecoration.Underline else TextDecoration.None
            )
        ) {
            append(displayedWord)
        }
        pop()

        append(fullText.substring(end))
    }

    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {

        ClickableText(
            text = annotatedText,
            style = TextStyle(fontSize = 20.sp),
            onClick = { offset ->
                val annotation = annotatedText.getStringAnnotations(
                    tag = "CLICK",
                    start = offset,
                    end = offset
                ).firstOrNull()

                if (annotation != null) {
                    showBertso = !showBertso
                }
            }
        )

        if (showBertso) {
            BertsoDesplegablea(
                a = act,
                b = bct,
                c = cct,
                ColorBox = colorBox,
                selectedOption = selectedOption,
                isCorrectSelection = isCorrectSelection,
                onOptionSelected = { option ->
                    handleSelection(option)
                }
            )
        }
    }
}
