package com.example.errenteriaapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ModalBottomSheet birsagarri bat erakusten du.
 * Beste konposatzaile batzuek erabili dezaketen orokortutako modala.
 *
 * @param sheetContainerColor Modalaren atzeko kolorea
 * @param scrimAlpha Atzeko lausotzearen opakutasuna (0.0 - 1.0)
 * @param sheetHeightFraction Modalaren altueraren frakzioa pantailarekiko (0.0 - 1.0)
 * @param onDismiss Modal itxitzean deitzen den funtzioa
 * @param sheetContent Modalaren edukia (itxi funtzioa jasotzen du)
 * @param content Oinarrizko edukia (modal irekitzeko funtzioa jasotzen du)
 */
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

    // Irekitzean zabaltzea behartu
    LaunchedEffect(showSheet) {
        if (showSheet) {
            delay(100)
            sheetState.expand()
        }
    }

    // Modal irekitzeko funtzioa jasotzen duen UI
    content.invoke {
        scope.launch { showSheet = true }
    }

    // Modal true denean erakutsi
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