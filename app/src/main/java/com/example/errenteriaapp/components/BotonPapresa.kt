package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.errenteriaapp.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Egiaztatzeko botoia erakusten du Papresa jokoan.
 * Erabiltzaileak hondakin guztiak sailkatu dituenean botoia aktibatzen da.
 *
 * @param allAnswered Erabiltzaileak galdera guztiak erantzun dituen
 * @param answeredCount Erantzundako galdera kopurua
 * @param totalCount Galdera kopuru osoa
 * @param onVerifyClick Botoian klik egitean deitzen den funtzioa
 * @param modifier Modifier gehigarria
 */
@Composable
fun VerifyButton(
    allAnswered: Boolean,
    answeredCount: Int,
    totalCount: Int,
    onVerifyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Botoiaren egoeraren araberako testua
    val statusText = if (allAnswered) {
        stringResource(R.string.papresa_verify_ready)
    } else {
        stringResource(R.string.papresa_verify_progress, answeredCount, totalCount)
    }

    Button(
        onClick = onVerifyClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .height(56.dp),
        enabled = allAnswered,
        shape = RoundedCornerShape(12.dp)
    ) {
        // Egiaztatzeko ikonoa
        Icon(
            Icons.Default.Check,
            contentDescription = stringResource(R.string.papresa_verify_icon),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        // Botoiaren testua
        Text(
            text = stringResource(R.string.papresa_verify_button, statusText),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}