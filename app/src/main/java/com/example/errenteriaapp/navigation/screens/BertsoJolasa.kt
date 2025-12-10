package com.example.errenteriaapp.navigation.screens


import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.errenteriaapp.components.BertsoDesplegablea
import com.example.errenteriaapp.components.textoBertsoa
import com.example.errenteriaapp.functions.ClickableTextFunction

@Composable
fun BertsoJolasaScreen(
    navController: NavController) {

    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            Spacer(modifier = Modifier.padding(6.dp))
            textoBertsoa(
                textobertsoa = "Milla zortziehun eta hirurogeita"
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "hamalau urte _____.",
                clickableword = "_____",
                act = "abenduan",
                bct = "urrian",
                cct = "martxoan",
                colorBox = 0xFFFFC1C1,
                correctAnswer = "urrian"
            )
        }
        item {
            textoBertsoa(
                textobertsoa = "lehenengo plazan kantatu nuen\n" +
                        "nik Ernaniko lurrean,\n" +
                        "San Antonio deitzen diogun"
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "ermita baten _____.",
                clickableword = "_____",
                act = "aurrian",
                bct = "barruan",
                cct = "atzean",
                colorBox = 0xFFBDFFC0,
                correctAnswer = "aurrian"
            )
        }
        item {
            textoBertsoa(
                textobertsoa = "lengo ohitura zaharrean.\n" +
                        "\n" + "\n" +
                        "Joxe Migelek atera zuen\n" +
                        "oso izketa leguna:\n" +
                        "«Hau da, gazteak, prezisamente"
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "guk egin behar _____.",
                clickableword = "_____",
                act = "deguna",
                bct = "daukaguna",
                cct = "zerbait",
                colorBox = 0xFFBDCEFF,
                correctAnswer = "deguna"
            )
        }
        item {
            textoBertsoa(
                textobertsoa = "altxa dezagun San Antonio."
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "gaur da beraren _____!",
                clickableword = "_____!",
                act = "ospakizuna",
                bct = "ohorea",
                cct = "eguna",
                colorBox = 0xFFC5904E,
                correctAnswer = "eguna"
            )
        }
        item {
            textoBertsoa(
                textobertsoa = "Gaur Goiatz Txikin dago itxututa\n" +
                    "orduko nire laguna."
            )
            Spacer(modifier = Modifier.padding(10.dp))
            textoBertsoa(
                textobertsoa = "(…)"
            )
            Spacer(modifier = Modifier.padding(10.dp))
            textoBertsoa(
                textobertsoa = "Hirurogeita hamar bat urte"
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "badut _____.",
                clickableword = "_____",
                act = "sorbaldan",
                bct = "bizkarrian",
                cct = "bularrean",
                colorBox = 0xFFB6B6B6,
                correctAnswer = "bizkarrian"
            )
        }
        item {
            textoBertsoa(
                textobertsoa = "kargamenturik txarrena hau da,"
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "ezin utzi _____.",
                clickableword = "_____",
                act = "bidean",
                bct = "kanpoan",
                cct = "bazterrian",
                colorBox = 0xFFE0FF6F,
                correctAnswer = "bazterrian"
            )
        }
        item {
            textoBertsoa(
                textobertsoa = "anka batetik kojoka nabil,\n" +
                        "reuma daukat iztarrean,\n" +
                        "baina baditut laguntzaileak,"
            )
        }
        item {
            ClickableTextFunction(
                fulltext = "ez nago modu _____.",
                clickableword = "_____",
                act = "onean",
                bct = "txarrian",
                cct = "erdian",
                colorBox = 0xFFFFE4C4,
                correctAnswer = "txarrian"
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
