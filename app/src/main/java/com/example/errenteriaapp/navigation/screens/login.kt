package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.errenteriaapp.R
import com.example.errenteriaapp.components.*
import com.example.errenteriaapp.database.viewModel.LoginViewModel
import com.example.errenteriaapp.navigation.Routes
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,
) {
    var nombreCompleto by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isTeacherMode by remember { mutableStateOf(false) }
    val isSaving = loginViewModel.isSaving.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val allIrakasleak by loginViewModel.getAllIrakasleak().collectAsStateWithLifecycle(initialValue = emptyList())

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val imageHeight = maxHeight * 0.4f

        BackgroundChurch()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(Modifier.height(20.dp))

                LoginTitle()

                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isTeacherMode) "Irakasle modua" else "Ikasle modua",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                FilterChip(
                                    selected = !isTeacherMode,
                                    onClick = {
                                        isTeacherMode = false
                                        errorMessage = ""
                                        password = ""
                                        nombreCompleto = ""
                                    },
                                    label = {
                                        Text(
                                            "Ikaslea",
                                            fontSize = 14.sp,
                                            color = if (!isTeacherMode) MaterialTheme.colorScheme.primary else Color.White
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color.White,
                                        containerColor = Color.Transparent
                                    ),
                                    shape = MaterialTheme.shapes.large,
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                FilterChip(
                                    selected = isTeacherMode,
                                    onClick = {
                                        isTeacherMode = true
                                        errorMessage = ""
                                        password = ""
                                        nombreCompleto = ""
                                    },
                                    label = {
                                        Text(
                                            "Irakaslea",
                                            fontSize = 14.sp,
                                            color = if (isTeacherMode) MaterialTheme.colorScheme.primary else Color.White
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color.White,
                                        containerColor = Color.Transparent
                                    ),
                                    shape = MaterialTheme.shapes.large
                                )
                            }
                        }

                        Divider(
                            color = Color.White.copy(alpha = 0.2f),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (isTeacherMode) {
                                CompactTextField(
                                    value = nombreCompleto,
                                    onValueChange = {
                                        nombreCompleto = it
                                        errorMessage = ""
                                    },
                                    label = "Irakaslearen izena",
                                    isError = errorMessage.isNotEmpty(),
                                    singleLine = true
                                )

                                CompactPasswordField(
                                    value = password,
                                    onValueChange = {
                                        password = it
                                        errorMessage = ""
                                    },
                                    label = "Pasahitza",
                                    isError = errorMessage.isNotEmpty()
                                )
                            } else {
                                CompactTextField(
                                    value = nombreCompleto,
                                    onValueChange = {
                                        nombreCompleto = it
                                        errorMessage = ""
                                    },
                                    label = "Zure izena eta abizena",
                                    isError = errorMessage.isNotEmpty(),
                                    singleLine = true
                                )
                            }
                        }

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        // Cambiar la validación del formulario
                        val isFormValid = when {
                            isTeacherMode -> nombreCompleto.trim().isNotEmpty() && password.isNotEmpty()
                            else -> {
                                val trimmed = nombreCompleto.trim()
                                trimmed.isNotEmpty() && hasNameAndSurname(trimmed)
                            }
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    val nombreLimpio = nombreCompleto.trim()

                                    if (isTeacherMode) {
                                        // Validar irakaslea
                                        val irakasle = allIrakasleak.find {
                                            it.izenaAbizena.equals(nombreLimpio, ignoreCase = true)
                                        }

                                        if (irakasle != null && irakasle.contraseña == password) {
                                            loginViewModel.guardarNombre(nombreLimpio)
                                            errorMessage = ""
                                            navController.navigate(Routes.GPS_SCREEN)
                                        } else {
                                            errorMessage = "Irakaslearen izena edo pasahitza okerrak dira"
                                        }
                                    } else {
                                        // Modo ikaslea - VALIDACIÓN MEJORADA
                                        if (nombreLimpio.isBlank()) {
                                            errorMessage = "Mesedez, idatzi zure izena eta abizena"
                                        } else if (!isValidName(nombreLimpio)) {
                                            errorMessage = "Izena eta abizena bakarrik letrak eta espazioak izan ditzake"
                                        } else if (!hasNameAndSurname(nombreLimpio)) {
                                            errorMessage = "Mesedez, idatzi zure izena ETA abizena (bi hitz edo gehiago)"
                                        } else {
                                            loginViewModel.guardarNombre(nombreLimpio)
                                            errorMessage = ""
                                            navController.navigate(Routes.GPS_SCREEN)
                                        }
                                    }
                                }
                            },
                            enabled = isFormValid && !isSaving.value,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            )
                        ) {
                            if (isSaving.value) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = if (isTeacherMode) "SAIOA HASI" else "HASI JOLASA",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                LoginDivider()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 30.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    CharacterItemLogin(
                        imageRes = R.drawable.xanti_silla_hablando,
                        name = "Xanti",
                        imageHeight = imageHeight * 0.9f
                    )
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    CharacterItemLogin(
                        imageRes = R.drawable.maialen_silla_hablando,
                        name = "Maialen",
                        imageHeight = imageHeight * 0.9f
                    )
                }
            }
        }
    }
}

// Función de validación mejorada
fun isValidName(name: String): Boolean {
    if (name.isBlank()) return false

    // Verificar cada carácter
    for (char in name) {
        val isValid = when {
            char.isLetter() -> true
            char.isWhitespace() -> true
            char == '\'' || char == '-' -> true
            // Letras acentuadas y ñ
            char in "áéíóúüñÁÉÍÓÚÜÑ" -> true
            else -> false
        }

        if (!isValid) {
            return false
        }
    }

    return true
}

// FUNCIÓN NUEVA: Validar que tiene al menos nombre y apellido
fun hasNameAndSurname(fullName: String): Boolean {
    // Limpiar espacios extra
    val cleanedName = fullName.trim()

    // Dividir por espacios
    val parts = cleanedName.split("\\s+".toRegex())

    // Debe tener al menos 2 partes y cada parte debe tener al menos 2 caracteres
    // (esto evita que pongan solo iniciales como "A B" o "X Y")
    if (parts.size < 2) {
        return false
    }

    // Verificar que cada parte tenga al menos 2 caracteres (excepto apellidos compuestos)
    // Contar las partes que tienen al menos 2 caracteres
    val validParts = parts.count { part ->
        // Permitir apóstrofes y guiones en los nombres
        val lettersOnly = part.filter { it.isLetter() }
        lettersOnly.length >= 2
    }

    // Debe haber al menos 2 partes válidas (nombre y primer apellido)
    return validParts >= 2
}





