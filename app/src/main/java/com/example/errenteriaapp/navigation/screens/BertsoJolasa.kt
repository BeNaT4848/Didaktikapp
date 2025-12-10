package com.example.errenteriaapp.navigation.screens


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.errenteriaapp.components.BertsoDesplegablea
import com.example.errenteriaapp.functions.ClickableTextFunction

@Composable
fun BertsoJolasaScreen(
    navController: NavController) {

    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            ClickableTextFunction(
                fulltext = "Hemen _____",
                clickableword = "_____",
                a = "ab",
                b = "b",
                c = "c",
                colorBox = 0xFFFFC1C1
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "Beste lerro _____.",
                clickableword = "_____",
                a = "a",
                b = "b",
                c = "c",
                colorBox = 0xFFBDFFC0
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "Hona hemen _____.",
                clickableword = "_____",
                a = "a",
                b = "b",
                c = "c",
                colorBox = 0xFFBDCEFF
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "Aukeratu hemen _____.",
                clickableword = "_____",
                a = "a",
                b = "b",
                c = "c",
                colorBox = 0xFFC5904E
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "Klikatu berriro _____.",
                clickableword = "_____",
                a = "a",
                b = "b",
                c = "c",
                colorBox = 0xFFB6B6B6
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "Beste aukera _____.",
                clickableword = "_____",
                a = "a",
                b = "b",
                c = "c",
                colorBox = 0xFFE0FF6F
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "Esaldi berria _____.",
                clickableword = "_____",
                a = "a",
                b = "b",
                c = "c",
                colorBox = 0xFFFFE4C4
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "Hurrengoa _____.",
                clickableword = "_____",
                a = "a",
                b = "b",
                c = "c",
                colorBox = 0xFFFFD1DC
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "Klik egin _____.",
                clickableword = "_____",
                a = "a",
                b = "b",
                c = "c",
                colorBox = 0xFFE0BBE4
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "Adibide hau _____.",
                clickableword = "_____",
                a = "a",
                b = "b",
                c = "c",
                colorBox = 0xFF6FFFBC
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "Beste bat _____.",
                clickableword = "_____",
                a = "a",
                b = "b",
                c = "c",
                colorBox = 0xFFCCE2CB
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "Jarraitu _____.",
                clickableword = "_____",
                a = "a",
                b = "b",
                c = "c",
                colorBox = 0xFFB5EAD7
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "Amaitzeko _____.",
                clickableword = "_____",
                a = "a",
                b = "b",
                c = "c",
                colorBox = 0xFFA7FF6F
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "Azkena _____.",
                clickableword = "_____",
                a = "a",
                b = "b",
                c = "c",
                colorBox = 0xFDF6BCFF
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BertsoJolasaPreview() {
    BertsoJolasaScreen(navController = rememberNavController()
    )
}
