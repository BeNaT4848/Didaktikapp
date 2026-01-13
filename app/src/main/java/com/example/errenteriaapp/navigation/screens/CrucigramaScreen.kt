package com.example.errenteriaapp.navigation.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.errenteriaapp.components.ActiveWordIndicator
import com.example.errenteriaapp.components.CluesSection
import com.example.errenteriaapp.components.CrucigramaHeader
import com.example.errenteriaapp.components.GameResultDialogs
import com.example.errenteriaapp.components.InstructionsDialog
import com.example.errenteriaapp.components.PistaInfo
import com.example.errenteriaapp.components.TableroCrucigramaInteractivo
import com.example.errenteriaapp.components.VerificationIndicator
import com.example.errenteriaapp.components.VerifyButton
import com.example.errenteriaapp.components.generateFocusRequesters
import com.example.errenteriaapp.components.onBorrarYRetroceder
import com.example.errenteriaapp.components.onClickCelda
import com.example.errenteriaapp.components.onLetraCambiada
import com.example.errenteriaapp.database.viewModel.CrucigramaViewModel
import com.example.errenteriaapp.navigation.Routes
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CrucigramaScreen(navController: NavController) {
    val viewModel: CrucigramaViewModel = viewModel()
    val celdas by viewModel.celdas
    val crucigramaEstado by viewModel.crucigramaEstado
    val verificacionRealizada by viewModel.verificacionRealizada
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val mostrarDialogoExito by viewModel.mostrarDialogoExito
    val palabraActiva by viewModel.palabraActiva
    val mostrarInstruccionesIniciales by viewModel.mostrarInstruccionesIniciales

    val focusRequesters = remember { generateFocusRequesters() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CrucigramaHeader()

        if (palabraActiva != null) {
            ActiveWordIndicator(
                palabraActiva = palabraActiva!!,
                onDeactivate = {
                    viewModel.desactivarPalabra()
                    focusManager.clearFocus()
                }
            )
        }

        TableroCrucigramaInteractivo(
            celdas = celdas,
            focusRequesters = focusRequesters,
            onLetraCambiada = { fila, columna, caracter ->
                onLetraCambiada(fila, columna, caracter, viewModel, coroutineScope, focusRequesters)
            },
            onBorrar = { fila, columna ->
                onBorrarYRetroceder(fila, columna, viewModel, coroutineScope, focusRequesters)
            },
            onEnterPressed = { _, _ -> },
            focusManager = focusManager,
            palabraActiva = palabraActiva,
            crucigramaEstado = crucigramaEstado,
            onClickCelda = { fila, columna ->
                onClickCelda(fila, columna, viewModel)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        CluesSection(
            pistasHorizontales = listOf(
                PistaInfo(1, "Duela milaka urte kobazuloetan bizi ziren gizakiak."),
                PistaInfo(5, "Haria lantzeko erabiltzen zuten tresna.")
            ),
            pistasVerticales = listOf(
                PistaInfo(2, "Historiaurrea ikertzen duen zientzialaria."),
                PistaInfo(4, "Aizpitarteko kobazuloak dauden herria."),
                PistaInfo(3, "Kobazuloetako hormetan margotzen zituzten animaliak eta sinboloak.")
            ),
            palabraActiva = palabraActiva,
            onActivateWord = { numero -> viewModel.activarPalabraPorNumero(numero) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        VerifyButton(
            onClick = {
                viewModel.verificarRespuestas()
                keyboardController?.hide()
            }
        )

        if (mostrarDialogoExito) {
            GameResultDialogs(
                showSuccess = true,
                showWrong = false,
                onDismissSuccess = {
                    viewModel.cerrarDialogoExito()
                },
                onDismissWrong = {},
                onSuccessButton = {
                    viewModel.cerrarDialogoExito()
                    navController.navigate(Routes.MAPA_SCREEN)
                },
                onWrongButton = {}
            )
        }

        if (verificacionRealizada && !mostrarDialogoExito) {
            VerificationIndicator(celdas = celdas)
        }

        Spacer(modifier = Modifier.height(30.dp))

        if (mostrarInstruccionesIniciales) {
            InstructionsDialog(
                onDismiss = { viewModel.cerrarInstrucciones() }
            )
        }
    }
}