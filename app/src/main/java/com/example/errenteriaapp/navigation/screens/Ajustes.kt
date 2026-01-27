package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AjustesScreen(
    modifier: Modifier = Modifier,
    isTeacherMode: Boolean = false,
    isDarkMode: Boolean = false,
    onThemeToggle: (Boolean) -> Unit = {},
    onDeleteRanking: () -> Unit = {},
    onResetScores: () -> Unit = {},
    isDeletingRanking: Boolean = false,
    isResettingScores: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AjustesContent(
            isTeacherMode = isTeacherMode,
            isDarkMode = isDarkMode,
            onThemeToggle = onThemeToggle,
            onDeleteRanking = onDeleteRanking,
            onResetScores = onResetScores,
            isDeletingRanking = isDeletingRanking,
            isResettingScores = isResettingScores
        )
    }
}

@Composable
private fun AjustesContent(
    modifier: Modifier = Modifier,
    isTeacherMode: Boolean,
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onDeleteRanking: () -> Unit,
    onResetScores: () -> Unit,
    isDeletingRanking: Boolean,
    isResettingScores: Boolean
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Konfigurazioa",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        SettingsCard(title = "Hizkuntza") {
            Text(
                text = "Aukeratu aplikazioaren hizkuntza.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            LanguageSelector()
        }
        SettingsCard(title = "Gaia") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Itxura",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Gai argia / Gai iluna",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ThemeOptionButton(
                        label = "Modu argia",
                        selected = !isDarkMode,
                        onClick = { onThemeToggle(false) }
                    )
                    ThemeOptionButton(
                        label = "Modu iluna",
                        selected = isDarkMode,
                        onClick = { onThemeToggle(true) }
                    )
                }
            }
        }
        if (isTeacherMode) {
            // Tercera card para irakasles
            SettingsCard(title = "Irakasleentzako aukerak") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Opción 1: Eliminar todo el ranking (usuarios incluidos)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Ranking guztia ezabatu",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Ekintza honek rankingaren datu GUZTIAK ezabatuko ditu (erabiltzaileak barne). Ekintza hau ezin da desegin.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onDeleteRanking,
                            enabled = !isDeletingRanking,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isDeletingRanking) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onError,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "RANKING GUZTIA EZABATU",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )

                    // Opción 2: Solo eliminar puntuazioak (usuarios se mantienen)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Puntuazioak ezabatu",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Ekintza honek erabiltzaile GUZTIEN puntuazioak ezabatuko ditu, baina erabiltzaileak mantenduko dira.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onResetScores,
                            enabled = !isResettingScores,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isResettingScores) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onError,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "PUNTUAZIOAK EZABATU",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageSelector() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf("Gaztelania", "Euskara", "Ingelesa").forEach { label ->
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun ThemeOptionButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = if (selected) {
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        ButtonDefaults.buttonColors()
    }
    Button(
        onClick = onClick,
        colors = colors,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(label)
    }
}

@Preview
@Composable
private fun AjustesPreview() {
    MaterialTheme {
        AjustesScreen(isTeacherMode = true)
    }
}