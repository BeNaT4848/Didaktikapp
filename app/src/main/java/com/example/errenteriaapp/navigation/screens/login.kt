package com.example.errenteriaapp.navigation.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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

/**
 * Saioa hasteko pantaila konposatzen du (erabiltzailearen erregistroa)
 * @param loginViewModel Saioa hasteko ViewModela
 * @param navController Nabigazio kontrolatzailea
 */
@OptIn(ExperimentalMaterial3Api::class)
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
    var selectedClass by remember { mutableStateOf("") }
    var isClassExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sessionPrefs = remember(context) {
        context.getSharedPreferences("session", Context.MODE_PRIVATE)
    }
    val validationError = remember(nombreCompleto, isTeacherMode) {
        if (!isTeacherMode && nombreCompleto.isNotEmpty()) {
            getValidationMessage(nombreCompleto)
        } else {
            null
        }
    }
    val classOptions = listOf(
        "1A",
        "1B",
        "2A",
        "2B",
        "3A",
        "3B",
        "4A",
        "4B",
        "5A",
        "5B",
        "6A",
        "6B"
    )

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
                    .padding(horizontal = 16.dp, vertical = 8.dp), // Padding bertikala gutxiago
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(Modifier.height(20.dp)) // Goiko espazio gutxiago

                LoginTitle()

                Spacer(Modifier.height(16.dp)) // Espazio gutxiago

                // Txartel trinkoagoa
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Altxamendu leunagoa
                    shape = MaterialTheme.shapes.extraLarge // Ertzak gehiago biribildu
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp), // Barneko padding murriztua
                        verticalArrangement = Arrangement.spacedBy(12.dp) // Elementuen artean espazio gutxiago
                    ) {
                        // Modu hautatzailea - trinkoagoa
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp) // Espazio oso gutxi
                        ) {
                            Text(
                                text = if (isTeacherMode) "Irakasle modua" else "Ikasle modua",
                                color = Color.White,
                                fontSize = 16.sp, // Testu txikiagoa
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
                                        password = "" // Pasahitza garbitu modua aldatzean
                                    },
                                    label = {
                                        Text(
                                            "Ikaslea",
                                            fontSize = 14.sp, // Testu txikiagoa
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
                                        password = "" // Pasahitza garbitu modua aldatzean
                                    },
                                    label = {
                                        Text(
                                            "Irakaslea",
                                            fontSize = 14.sp, // Testu txikiagoa
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

                        // Bereizlea finagoa
                        Divider(
                            color = Color.White.copy(alpha = 0.2f),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // Moduaren araberako eremuak - trinkoagoak
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (isTeacherMode) {
                                // Irakasle modua
                                CompactTextField(
                                    value = nombreCompleto,
                                    onValueChange = {
                                        nombreCompleto = it.filter { char -> char.isLetter() || char.isWhitespace() }
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
                                ExposedDropdownMenuBox(
                                    expanded = isClassExpanded,
                                    onExpandedChange = { isClassExpanded = !isClassExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = selectedClass,
                                        onValueChange = {},
                                        readOnly = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        label = {
                                            Text(
                                                text = "Aukeratu zure klasea",
                                                color = Color.White,
                                                fontSize = 13.sp // Etiketa txikiagoa
                                            )
                                        },
                                        textStyle = TextStyle(
                                            color = Color.White,
                                            fontSize = 14.sp // Testu txikiagoa
                                        ),
                                        isError = errorMessage.isNotEmpty() && selectedClass.isEmpty(),
                                        singleLine = true,
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = isClassExpanded
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.White.copy(alpha = 0.8f),
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                                            errorBorderColor = Color.Red,
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            cursorColor = Color.White,
                                            focusedLabelColor = Color.White.copy(alpha = 0.9f),
                                            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                                            focusedTrailingIconColor = Color.White,
                                            unfocusedTrailingIconColor = Color.White.copy(alpha = 0.7f)
                                        )
                                    )

                                    ExposedDropdownMenu(
                                        expanded = isClassExpanded,
                                        onDismissRequest = { isClassExpanded = false }
                                    ) {
                                        classOptions.forEach { classOption ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = classOption,
                                                        color = Color.Black,
                                                        fontSize = 14.sp
                                                    )
                                                },
                                                onClick = {
                                                    selectedClass = classOption
                                                    isClassExpanded = false
                                                    errorMessage = ""
                                                }
                                            )
                                        }
                                    }
                                }
                                // Ikasle modua
                                CompactTextField(
                                    value = nombreCompleto,
                                    onValueChange = {
                                        // Bakarrik letrak eta zuriuneak baimendu
                                        nombreCompleto = it.filter { char ->
                                            char.isLetter() || char.isWhitespace() || char == 'ñ' || char == 'Ñ'
                                        }
                                        errorMessage = ""
                                    },
                                    label = "Zure izena eta abizena",
                                    isError = errorMessage.isNotEmpty() || validationError != null,
                                    singleLine = true,
                                    validationError = validationError
                                )
                            }
                        }

                        // Errore mezua - trinkoagoa
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp, // Testu txikiagoa
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        // Ekintza botoia - trinkoagoa
                        val isFormValid = when {
                            isTeacherMode -> nombreCompleto.trim().isNotEmpty() && password.isNotEmpty()
                            else -> nombreCompleto.trim().isNotEmpty() &&
                                    selectedClass.isNotEmpty() &&
                                    validationError == null
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    if (isTeacherMode) {
                                        // Irakaslea egiaztatu
                                        val irakasle = allIrakasleak.find {
                                            it.izenaAbizena.equals(nombreCompleto.trim(), ignoreCase = true)
                                        }

                                        if (irakasle != null && irakasle.contraseña == password) {
                                            // Arrakastatsua irakaslearentzat
                                            loginViewModel.guardarNombre(nombreCompleto, asTeacher = true)

                                            val cleanName = nombreCompleto.trim()
                                            sessionPrefs.edit()
                                                .putString("active_user_name", cleanName)
                                                .putBoolean("is_teacher_mode", true)
                                                .apply()

                                            errorMessage = ""
                                            navController.navigate(Routes.GPS_SCREEN)
                                        } else {
                                            errorMessage = "Irakaslearen izena edo pasahitza okerrak dira"
                                        }
                                    } else {
                                        // Ikasle modua - EGIAZTAPEN EKIN
                                        if (nombreCompleto.isBlank()) {
                                            errorMessage = "Mesedez, idatzi zure izena eta abizena"
                                        } else if (validationError != null) {
                                            errorMessage = validationError
                                        } else if (selectedClass.isBlank()) {
                                            errorMessage = "Mesedez, aukeratu zure klasea"
                                        } else {
                                            // Egiaztapen arrakastatsua
                                            loginViewModel.guardarNombre(nombreCompleto, false, selectedClass)

                                            val cleanName = nombreCompleto.trim()
                                            sessionPrefs.edit()
                                                .putString("active_user_name", cleanName)
                                                .putBoolean("is_teacher_mode", false)
                                                .apply()

                                            errorMessage = ""
                                            navController.navigate(Routes.GPS_SCREEN)
                                        }
                                    }
                                }
                            },
                            enabled = isFormValid && !isSaving.value,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp), // Botoi txikiagoa
                            shape = MaterialTheme.shapes.large, // Ertzak biribildu
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            )
                        ) {
                            if (isSaving.value) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp) // Adierazle txikiagoa
                                )
                            } else {
                                Text(
                                    text = if (isTeacherMode) "SAIOA HASI" else "HASI JOLASA",
                                    fontSize = 16.sp, // Testu txikiagoa
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp)) // Espazio gutxiago

                // Bereizle txikiagoa
                LoginDivider()
            }

            // Pertsonaiak
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 30.dp), // Beheko espazio gutxiago
                horizontalArrangement = Arrangement.spacedBy(12.dp), // Pertsonaien artean espazio gutxiago
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    CharacterItemLogin(
                        imageRes = R.drawable.xanti_silla_hablando,
                        name = "Xanti",
                        imageHeight = imageHeight * 0.9f // Pertsonaiak pixka bat txikiagoak
                    )
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    CharacterItemLogin(
                        imageRes = R.drawable.maialen_silla_hablando,
                        name = "Maialen",
                        imageHeight = imageHeight * 0.9f // Pertsonaiak pixka bat txikiagoak
                    )
                }
            }
        }
    }
}

/**
 * Izenaren balidazio mezua lortzen du
 * @param name Egiaztatu beharreko izen osoa
 * @return Balidazio errore mezua edo null balidazio zuzena bada
 */
fun getValidationMessage(name: String): String? {
    val trimmedName = name.trim()

    if (trimmedName.isEmpty()) {
        return null // Ez erakutsi mezurik hutsik badago
    }

    val parts = trimmedName.split("\\s+".toRegex())

    return when {
        parts.size < 2 -> "Sartu izena eta abizena (adibidez: Aitor Fernandez)"
        parts[0].length < 2 -> "Izenak gutxienez 2 letra izan behar ditu"
        parts.subList(1, parts.size).joinToString(" ").length < 3 ->
            "Abizenak gutxienez 3 letra izan behar ditu"
        else -> null
    }
}