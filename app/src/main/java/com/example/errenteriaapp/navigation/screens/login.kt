package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
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
    var errorMessageResId by remember { mutableStateOf<Int?>(null) }
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
                                text = stringResource(
                                    if (isTeacherMode) R.string.login_mode_teacher else R.string.login_mode_student
                                ),
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
                                        errorMessageResId = null
                                        password = "" // Limpiar contraseña al cambiar modo
                                    },
                                    label = {
                                        Text(
                                            stringResource(R.string.login_student_label),
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
                                        errorMessageResId = null
                                        password = "" // Limpiar contraseña al cambiar modo
                                    },
                                    label = {
                                        Text(
                                            stringResource(R.string.login_teacher_label),
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
                                        errorMessageResId = null
                                    },
                                    label = stringResource(R.string.login_teacher_name),
                                    isError = errorMessageResId != null,
                                    singleLine = true
                                )

                                CompactPasswordField(
                                    value = password,
                                    onValueChange = {
                                        password = it
                                        errorMessageResId = null
                                    },
                                    label = stringResource(R.string.login_password),
                                    isError = errorMessageResId != null
                                )
                            } else {
                                // Modo Ikaslea
                                CompactTextField(
                                    value = nombreCompleto,
                                    onValueChange = {
                                        nombreCompleto = it.filter { char -> char.isLetter() || char.isWhitespace() }
                                        errorMessageResId = null
                                    },
                                    label = stringResource(R.string.login_student_name),
                                    isError = errorMessageResId != null,
                                    singleLine = true
                                )
                            }
                        }

                        // Mensaje de error - más compacto
                        val errorMessage = errorMessageResId
                        if (errorMessage != null) {
                            Text(
                                text = stringResource(errorMessage),
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
                                            loginViewModel.guardarNombre(nombreCompleto)

                                            // Guardar usuario activo para progreso por usuario
                                            val cleanName = nombreCompleto.trim()
                                            sessionPrefs.edit().putString("active_user_name", cleanName).apply()

                                            errorMessageResId = null
                                            navController.navigate(Routes.GPS_SCREEN)
                                        } else {
                                            errorMessageResId = R.string.login_error_teacher_credentials
                                        }
                                    } else {
                                        // Modo ikaslea
                                        if (nombreCompleto.isBlank()) {
                                            errorMessageResId = R.string.login_error_student_name
                                        } else {
                                            loginViewModel.guardarNombre(nombreCompleto)

                                            // Guardar usuario activo para progreso por usuario
                                            val cleanName = nombreCompleto.trim()
                                            sessionPrefs.edit().putString("active_user_name", cleanName).apply()

                                            errorMessageResId = null
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
                                    text = stringResource(
                                        if (isTeacherMode) R.string.login_action_teacher else R.string.login_action_student
                                    ),
                                    fontSize = 16.sp // Texto más pequeño
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
