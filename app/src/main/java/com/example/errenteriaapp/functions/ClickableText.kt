package com.example.errenteriaapp.functions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.errenteriaapp.components.BertsoDesplegablea
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun ClickableTextFunction(fulltext: String, clickableword:String,
                          a: String, b: String, c: String, colorBox: Long) {

    val fullText = fulltext
    val clickableWord = clickableword

    var showBertso by rememberSaveable { mutableStateOf(false) }

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
                color = Color(0xFF1D3357),
                textDecoration = TextDecoration.Underline
            )
        ) {
            append(clickableWord)
        }
        pop()

        append(fullText.substring(end))
    }

    Column(modifier = Modifier.padding(20.dp)) {

        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                val annotation = annotatedText.getStringAnnotations(
                    tag = "CLICK",
                    start = offset,
                    end = offset
                ).firstOrNull()

                if (annotation != null) {
                    showBertso = !showBertso   // si estaba visible, lo ocultas; si no, lo muestras
                }
            }
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (showBertso) {
            BertsoDesplegablea(
                a = a,
                b = b,
                c = c,
                ColorBox = colorBox
            )
        }
    }
}
