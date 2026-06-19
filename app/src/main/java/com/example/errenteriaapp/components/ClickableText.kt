package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
/**
 * Testu klikagarri konposatzailea erakusten du.
 * Testuan hitz jakin bat klik egiteko modukoa da eta aukera desplegable bat erakusten du.
 * Bertso jolaserako erabiltzen da.
 *
 * @param fulltext Testu osoa
 * @param clickableword Klik egin daitekeen hitza (fulltext-en barruan egon behar du)
 * @param act Aukeraren testua (A)
 * @param bct Aukeraren testua (B)
 * @param cct Aukeraren testua (C)
 * @param correctAnswer Erantzun zuzena (act, bct edo cct-en bat)
 * @param onCorrect Erabiltzaileak erantzun zuzena hautatzean deitzen den funtzioa
 * @param onWrong Erabiltzaileak erantzun okerra hautatzean deitzen den funtzioa
 * @param onAnswered Erabiltzaileak erantzun duenean deitzen den funtzioa
 * @param isLocked Jokoa blokeatuta dagoen (ezin da hautatu)
 * @param attemptKey Saiakeraren gakoa (berrezartzeko erabiltzen da)
 * @param resetOnAttempt Saiakera bakoitzean egoera berrezartzeko
 */
@Composable
fun ClickableTextFunction(
    fulltext: String,
    clickableword:String,
    act: String,
    bct: String,
    cct: String,
    correctAnswer: String,
    onCorrect: (() -> Unit)? = null,
    onWrong: (() -> Unit)? = null,
    onAnswered: (() -> Unit)? = null,
    isLocked: Boolean = false,
    attemptKey: Int = 0,
    resetOnAttempt: Boolean = false
) {

    val fullText = fulltext
    val clickableWord = clickableword
    val context = LocalContext.current

    // Egoera gordetzeko (berrezarri ahal bada, attemptKey erabili)
    val saverKey = if (resetOnAttempt) attemptKey else 0
    var showBertso by rememberSaveable(saverKey) { mutableStateOf(false) }
    var selectedOption by rememberSaveable(saverKey) { mutableStateOf<String?>(null) }
    var isCorrectSelection by rememberSaveable(saverKey) { mutableStateOf<Boolean?>(null) }
    // Erakusteko hitza: hautatutako aukera edo jatorrizko hitza
    val displayedWord = selectedOption ?: clickableWord

    /**
     * Aukera hautatzean kudeatzen du.
     * @param option Hautatutako aukera
     */
    fun handleSelection(option: String) {
        if (selectedOption != null || isLocked) return
        selectedOption = option
        val isCorrect = option == correctAnswer
        isCorrectSelection = isCorrect


        if (isCorrect) {
            onCorrect?.invoke()
        } else {
            onWrong?.invoke()
        }
        onAnswered?.invoke()

        showBertso = false
    }
    // Testu annotatua eraiki (hitz klikagarria markatuta)
    val annotatedText = buildAnnotatedString {
        val start = fullText.indexOf(clickableWord)
        if (start == -1) {
            append(fullText)
            return@buildAnnotatedString
        }
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
                    else -> MaterialTheme.colorScheme.primary
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
            style = TextStyle(fontSize = 20.sp, color = MaterialTheme.colorScheme.primary),
            onClick = { offset ->
                val annotation = annotatedText.getStringAnnotations(
                    tag = "CLICK",
                    start = offset,
                    end = offset
                ).firstOrNull()

                if (annotation != null && !isLocked && selectedOption == null) {
                    showBertso = !showBertso
                }
            }
        )

        if (showBertso && !isLocked) {
            BertsoDesplegablea(
                a = act,
                b = bct,
                c = cct,
                selectedOption = selectedOption,
                isCorrectSelection = isCorrectSelection,
                onOptionSelected = { option ->
                    handleSelection(option)
                }
            )
        }
    }
}
