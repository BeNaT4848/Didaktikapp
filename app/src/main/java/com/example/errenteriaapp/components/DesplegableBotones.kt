package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReusableModalBottomSheet(
    sheetContainerColor: Color = Color(0xFF0E1B14),
    scrimAlpha: Float = 0.4f,
    sheetHeightFraction: Float = 0.48f,
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
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = sheetContainerColor,
            scrimColor = Color.Black.copy(alpha = scrimAlpha)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalConfiguration.current.screenHeightDp.dp * sheetHeightFraction)
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
