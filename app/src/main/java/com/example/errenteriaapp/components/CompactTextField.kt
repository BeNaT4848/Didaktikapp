package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Testu-eremu trinko bat erakusten du.
 * Login eta erregistro pantailarako erabiltzen da.
 *
 * @param value Testuaren balioa
 * @param onValueChange Testua aldatzean deitzen den funtzioa
 * @param label Eremuaren etiketa
 * @param isError Errorea duen
 * @param singleLine Lerro bakarra erakutsi behar den
 * @param validationError Balidazio-errorearen mezua (null bada, ez da erakusten)
 */
@Composable
fun CompactTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    singleLine: Boolean = true,
    validationError: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = label,
                color = Color.White,
                fontSize = 13.sp // Etiketa txikiagoa
            )
        },
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 14.sp // Testu txikiagoa
        ),
        isError = isError,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White.copy(alpha = 0.8f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
            errorBorderColor = Color.Red,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedLabelColor = Color.White.copy(alpha = 0.9f),
            unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
        )
    )

    // Balidazio-errorea erakutsi behar bada
    validationError?.let { error ->
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp, start = 16.dp)
        )
    }
}