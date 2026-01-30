package com.example.errenteriaapp.navigation.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.errenteriaapp.R
import com.example.errenteriaapp.i18n.AppLanguageState
import com.example.errenteriaapp.i18n.LanguageManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        SettingsCard(title = stringResource(R.string.settings_language_title)) {
            Text(
                text = stringResource(R.string.settings_language_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            LanguageSelector()
        }

        SettingsCard(title = stringResource(R.string.settings_theme_title)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_theme_appearance),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.settings_theme_light_dark),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ThemeOptionButton(
                        label = stringResource(R.string.settings_theme_light),
                        selected = !isDarkMode,
                        onClick = { onThemeToggle(false) }
                    )
                    ThemeOptionButton(
                        label = stringResource(R.string.settings_theme_dark),
                        selected = isDarkMode,
                        onClick = { onThemeToggle(true) }
                    )
                }
            }
        }

        if (isTeacherMode) {
            SettingsCard(title = stringResource(R.string.settings_teacher_options_title)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.settings_teacher_delete_ranking_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.settings_teacher_delete_ranking_desc),
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
                                    text = stringResource(R.string.settings_teacher_delete_ranking_button),
                                    style = MaterialTheme.typography.labelLarge,
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

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.settings_teacher_reset_scores_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.settings_teacher_reset_scores_desc),
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
                                    text = stringResource(R.string.settings_teacher_reset_scores_button),
                                    style = MaterialTheme.typography.labelLarge,
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
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()

    var selected by remember { mutableStateOf(LanguageManager.getSavedLanguageTag(context)) }
    var showApplying by remember { mutableStateOf(false) }

    fun apply(tag: String) {
        showApplying = true
        if (selected == tag) return

        LanguageManager.saveAndApply(context, tag)
        selected = LanguageManager.getSavedLanguageTag(context)
        AppLanguageState.bump()

        // Fallback: si el locale no se actualiza en runtime, recrea la Activity.
        scope.launch {
            delay(120)
            val currentLang = context.resources.configuration.locales[0].language
            if (currentLang != tag) {
                activity?.recreate()
            }
        }
    }

    // Cierra la animación tras un ratito (sin recrear Activity)
    LaunchedEffect(showApplying) {
        if (!showApplying) return@LaunchedEffect
        delay(260)
        showApplying = false
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LanguageButton(
                label = stringResource(R.string.language_spanish),
                selected = selected == "es",
                onClick = { apply("es") }
            )
            LanguageButton(
                label = stringResource(R.string.language_basque),
                selected = selected == "eu",
                onClick = { apply("eu") }
            )
            LanguageButton(
                label = stringResource(R.string.language_english),
                selected = selected == "en",
                onClick = { apply("en") }
            )
        }

        AnimatedVisibility(
            visible = showApplying,
            enter = fadeIn(animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)) +
                scaleIn(initialScale = 0.98f, animationSpec = tween(180, easing = FastOutSlowInEasing)),
            exit = fadeOut(animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)) +
                scaleOut(targetScale = 0.98f, animationSpec = tween(140, easing = FastOutSlowInEasing))
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(text = stringResource(R.string.language_applying))
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageButton(
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = colors
    ) {
        Text(text = label, fontSize = 14.sp)
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