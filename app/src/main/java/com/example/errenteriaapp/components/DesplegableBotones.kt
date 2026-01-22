package com.example.errenteriaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReusableModalBottomSheet(
    sheetContainerColor: Color = MaterialTheme.colorScheme.background,
    scrimAlpha: Float = 0.4f,
    sheetHeightFraction: Float = 0.48f,
    onDismiss: () -> Unit = {},
    sheetContent: @Composable (onClose: () -> Unit) -> Unit,
    content: @Composable (openSheet: () -> Unit) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }

    // Forzar expansión cuando se abra
    LaunchedEffect(showSheet) {
        if (showSheet) {
            delay(100)
            sheetState.expand()
        }
    }

    // UI que recibe la función para abrir el sheet
    content.invoke {
        scope.launch { showSheet = true }
    }

    // Modal cuando es true
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
                onDismiss()
            },
            sheetState = sheetState,
            containerColor = sheetContainerColor,
            scrimColor = MaterialTheme.colorScheme.background.copy(alpha = scrimAlpha)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalConfiguration.current.screenHeightDp.dp * sheetHeightFraction)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                sheetContent {
                    scope.launch {
                        sheetState.hide()
                        delay(300)
                        showSheet = false
                    }
                }
            }
        }
    }
}
