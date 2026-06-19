package com.example.errenteriaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R
import com.example.errenteriaapp.classes.WasteCategory
import com.example.errenteriaapp.classes.WasteItem

/**
 * Hondakinen edukiontzien errenkada erakusten du.
 * Kategoria bakoitzaren edukiontzia erakusten du eta klik egiteko modukoa da.
 * Erabiltzaileak hondakin bat kategoria batean sartzean erakusten du.
 *
 * @param currentWasteItem Uneko hondakin-elementua (null bada, ez dago elementurik)
 * @param userAnswers Erabiltzailearen erantzunen mapa (hondakin-id → kategoria)
 * @param onContainerClick Edukiontzi batean klik egitean deitzen den funtzioa
 * @param modifier Modifier gehigarria
 */
@Composable
fun WasteContainersRow(
    currentWasteItem: WasteItem?,
    userAnswers: Map<Int, WasteCategory>,
    onContainerClick: (WasteCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(140.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WasteCategory.values().forEach { category ->
            // Edukiontzi klikagarria
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .border(
                        3.dp,
                        // Uneko hondakin-elementua kategoria honetan badago, ertz-kolore berezia
                        if (currentWasteItem?.let { userAnswers[it.id] == category } == true)
                            category.color
                        else Color.LightGray,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onContainerClick(category) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // EMOJI BAKARRIK - Atzeko kaxarik gabe
                Text(
                    text = when(category) {
                        WasteCategory.YELLOW -> "🟡"
                        WasteCategory.BLUE -> "🔵"
                        WasteCategory.BROWN -> "🟤"
                        WasteCategory.BLACK -> "⚫"
                    },
                    fontSize = 30.sp, // Ikusgarritasun hobea tamaina handiagoarekin
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Izena
                Text(
                    text = stringResource(category.displayNameResId),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Deskribapen optimizatua
                Text(
                    text = stringResource(category.descriptionResId),
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    lineHeight = 10.sp,
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}