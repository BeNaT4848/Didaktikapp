package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R

/**
 * Pasahitzaren eremu trinko bat erakusten du.
 * Pasahitzaren karaktereak ezkutatzeko/erakusteko aukera du.
 *
 * @param value Pasahitzaren testu-balioa
 * @param onValueChange Pasahitza aldatzean deitzen den funtzioa
 * @param label Eremuaren etiketa
 * @param isError Pasahitzak errorea duen
 */
@Composable
fun CompactPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = label,
                color = Color.White,
                fontSize = 13.sp
            )
        },
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 14.sp
        ),
        isError = isError,
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        trailingIcon = {
            // Begiaren ikonoa (pasahitza erakusteko/ezkutatzeko)
            IconButton(
                onClick = { passwordVisible = !passwordVisible },
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (passwordVisible)
                            R.drawable.abierto   // 👁 begi irekia
                        else
                            R.drawable.cerrado   // 🙈 begi itxia
                    ),
                    contentDescription = if (passwordVisible) "Ocultar pasahitza" else "Erakutsi pasahitza",
                    tint = Color.White.copy(alpha = 0.8f), // Color.Unspecified erabili zure ikonoak kolorea badu
                    modifier = Modifier.size(18.dp)
                )
            }
        },
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
}