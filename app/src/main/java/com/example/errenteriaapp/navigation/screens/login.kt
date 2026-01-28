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
    val context = LocalContext.current
    val sessionPrefs = remember(context) {
        context.getSharedPreferences("session", Context.MODE_PRIVATE)
    }

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
                    .padding(horizontal = 16.dp, vertical = 8.dp), // Menos padding vertical
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(Modifier.height(20.dp)) // Menos espacio arriba

                LoginTitle()

                Spacer(Modifier.height(16.dp)) // Menos espacio

                // Card más compacto
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Elevación más suave
                    shape = MaterialTheme.shapes.extraLarge // Bordes más redondeados
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp), // Padding interno reducido
                        verticalArrangement = Arrangement.spacedBy(12.dp) // Menos espacio entre elementos
                    ) {
                        // Selector de modo - más compacto
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp) // Muy poco espacio
                        ) {
                            Text(
                                text = if (isTeacherMode) "Irakasle modua" else "Ikasle modua",
                                color = Color.White,
                                fontSize = 16.sp, // Texto más pequeño
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
                                        password = "" // Limpiar contraseña al cambiar modo
                                    },
                                    label = {
                                        Text(
                                            "Ikaslea",
                                            fontSize = 14.sp, // Texto más pequeño
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
                                        password = "" // Limpiar contraseña al cambiar modo
                                    },
                                    label = {
                                        Text(
                                            "Irakaslea",
                                            fontSize = 14.sp, // Texto más pequeño
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

                        // Divider más fino
                        Divider(
                            color = Color.White.copy(alpha = 0.2f),
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // Campos según el modo - más compactos
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (isTeacherMode) {
                                // Modo Irakaslea
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
                                // Modo Ikaslea
                                CompactTextField(
                                    value = nombreCompleto,
                                    onValueChange = {
                                        nombreCompleto = it.filter { char -> char.isLetter() || char.isWhitespace() }
                                        errorMessage = ""
                                    },
                                    label = "Zure izena eta abizena",
                                    isError = errorMessage.isNotEmpty(),
                                    singleLine = true
                                )
                            }
                        }

                        // Mensaje de error - más compacto
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp, // Texto más pequeño
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        // Botón de acción - más compacto
                        val isFormValid = when {
                            isTeacherMode -> nombreCompleto.trim().isNotEmpty() && password.isNotEmpty()
                            else -> nombreCompleto.trim().isNotEmpty()
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    val ctx = navController.context
                                    val sessionPrefs = ctx.getSharedPreferences("session", android.content.Context.MODE_PRIVATE)

                                    if (isTeacherMode) {
                                        // Validar irakaslea
                                        val irakasle = allIrakasleak.find {
                                            it.izenaAbizena.equals(nombreCompleto.trim(), ignoreCase = true)
                                        }

                                        if (irakasle != null && irakasle.contraseña == password) {
                                            // Login exitoso para irakaslea
                                            loginViewModel.guardarNombre(nombreCompleto, asTeacher = true)

                                            // Guardar usuario activo y rol en preferencias
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
                                        // Modo ikaslea
                                        if (nombreCompleto.isBlank()) {
                                            errorMessage = "Mesedez, idatzi zure izena eta abizena"
                                        } else {
                                            loginViewModel.guardarNombre(nombreCompleto)

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
                                .height(48.dp), // Botón más pequeño
                            shape = MaterialTheme.shapes.large, // Bordes redondeados
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onTertiary
                            )
                        ) {
                            if (isSaving.value) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onTertiary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp) // Indicador más pequeño
                                )
                            } else {
                                Text(
                                    text = if (isTeacherMode) "SAIOA HASI" else "HASI JOLASA",
                                    fontSize = 16.sp, // Texto más pequeño
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp)) // Menos espacio

                // Divider más pequeño
                LoginDivider()
            }

            // Personajes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 30.dp), // Menos espacio abajo
                horizontalArrangement = Arrangement.spacedBy(12.dp), // Menos espacio entre personajes
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    CharacterItemLogin(
                        imageRes = R.drawable.xanti_silla_hablando,
                        name = "Xanti",
                        imageHeight = imageHeight * 0.9f // Personajes un poco más pequeños
                    )
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    CharacterItemLogin(
                        imageRes = R.drawable.maialen_silla_hablando,
                        name = "Maialen",
                        imageHeight = imageHeight * 0.9f // Personajes un poco más pequeños
                    )
                }
            }
        }
    }
}