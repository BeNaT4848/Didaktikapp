package com.example.errenteriaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Bertso jolaserako desplegable konposatzailea.
 * Hiru aukera erakusten ditu eta erabiltzailea aukera bat hautatzea ahalbidetzen du.
 * Aukerak eremu horizontaletan erakusten dira eta hautatutakoak kolorez markatzen dira.
 *
 * @param a Lehenengo aukera (ezkerrekoa)
 * @param b Bigarren aukera (erdikoa)
 * @param c Hirugarren aukera (eskuinekoa)
 * @param selectedOption Erabiltzaileak hautatutako aukera (null bada, oraindik ez du hautatu)
 * @param isCorrectSelection Hautatutako aukera zuzena den (null bada, oraindik ez da egiaztatu)
 * @param onOptionSelected Erabiltzaileak aukera bat hautatzen duenean deitzen den funtzioa
 */
@Composable
fun BertsoDesplegablea(
    a: String,
    b: String,
    c: String,
    selectedOption: String?,
    isCorrectSelection: Boolean?,
    onOptionSelected: (String) -> Unit
) {
    // Kolore definizioak
    val successColor = Color(0xFF4CAF50) // Zuzena
    val errorColor = Color(0xFFE53935) // Okerra

    /**
     * Aukera baten atzeko kolorea kalkulatzen du.
     * @param option Kalkulatu nahi den aukera
     * @return Atzeko kolore egokia
     */
    fun backgroundFor(option: String) = when {
        option == selectedOption && isCorrectSelection == true -> successColor
        option == selectedOption && isCorrectSelection == false -> errorColor
        else -> Color.White
    }

    /**
     * Aukera baten testu-kolorea kalkulatzen du.
     * @param option Kalkulatu nahi den aukera
     * @return Testu-kolore egokia
     */
    @Composable
    fun textColorFor(option: String) = if (option == selectedOption && isCorrectSelection != null) {
        Color.Black
    } else {
        Color.Black
    }

    Box (
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 4.dp, vertical = 12.dp)
            .background(color =  MaterialTheme.colorScheme.onPrimaryContainer, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hiru aukerak errenkada batean
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AUKERA A (ezkerra)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .border(
                            width = 0.8.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(backgroundFor(a), RoundedCornerShape(20.dp))
                        .clickable(enabled = selectedOption == null) { onOptionSelected(a) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = a,
                        color = textColorFor(a),
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 6.dp)
                            .fillMaxWidth(),
                        maxLines = 2,
                        softWrap = true,
                        textAlign = TextAlign.Center
                    )
                }

                // AUKERA B (erdia)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .border(
                            width = 0.8.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(backgroundFor(b), RoundedCornerShape(20.dp))
                        .clickable(enabled = selectedOption == null) { onOptionSelected(b) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = b,
                        color = textColorFor(b),
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 6.dp)
                            .fillMaxWidth(),
                        maxLines = 2,
                        softWrap = true,
                        textAlign = TextAlign.Center
                    )
                }

                // AUKERA C (eskuina)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                        .border(
                            width = 0.8.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(backgroundFor(c), RoundedCornerShape(20.dp))
                        .clickable(enabled = selectedOption == null) { onOptionSelected(c) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = c,
                        color = textColorFor(c),
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 6.dp)
                            .fillMaxWidth(),
                        maxLines = 2,
                        softWrap = true,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}