package com.example.errenteriaapp.navigation.screens


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.errenteriaapp.components.BertsoDesplegablea

@Composable
fun BertsoJolasaScreen(
    navController: NavController) {

    LazyColumn {
        item {
            BertsoDesplegablea(
                a = "a",
                b = "b",
                c = "c",
                ColorBox = 0xFFFFC1C1
            )
        }
        item {
            BertsoDesplegablea(
                a = "a",
                b = "b",
                c = "c",
                ColorBox = 0xFFBDFFC0
            )
        }
        item {
            BertsoDesplegablea(
                a = "a",
                b = "b",
                c = "c",
                ColorBox = 0xFFBDCEFF
            )
        }
        item {
            BertsoDesplegablea(
                a = "a",
                b = "b",
                c = "c",
                ColorBox = 0xFFC5904E
            )
        }
        item {
            BertsoDesplegablea(
                a = "a",
                b = "b",
                c = "c",
                ColorBox = 0xFFB6B6B6
            )
        }
        item {
            BertsoDesplegablea(
                a = "a",
                b = "b",
                c = "c",
                ColorBox = 0xFFE0FF6F
            )
        }
        item {
            BertsoDesplegablea(
                a = "a",
                b = "b",
                c = "c",
                ColorBox = 0xFFFFE4C4
            )
        }
        item {
            BertsoDesplegablea(
                a = "a",
                b = "b",
                c = "c",
                ColorBox = 0xFFFFD1DC
            )
        }
        item {
            BertsoDesplegablea(
                a = "a",
                b = "b",
                c = "c",
                ColorBox = 0xFFE0BBE4
            )
        }
        item {
            BertsoDesplegablea(
                a = "a",
                b = "b",
                c = "c",
                ColorBox = 0xFF6FFFBC
            )
        }
        item {
            BertsoDesplegablea(
                a = "a",
                b = "b",
                c = "c",
                ColorBox = 0xFFCCE2CB
            )
        }
        item {
            BertsoDesplegablea(
                a = "a",
                b = "b",
                c = "c",
                ColorBox = 0xFFB5EAD7
            )
        }
        item {
            BertsoDesplegablea(
                a = "a",
                b = "b",
                c = "c",
                ColorBox = 0xFFA7FF6F
            )
        }
        item {
            BertsoDesplegablea(
                a = "a",
                b = "b",
                c = "c",
                ColorBox = 0xFDF6BCFF
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
