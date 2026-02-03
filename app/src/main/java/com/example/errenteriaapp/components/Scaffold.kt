package com.example.errenteriaapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Aplikazioaren oinarrizko eskafolda erakusten du.
 * Orokorreko markoa ematen du edukiarentzat.
 *
 * @param navController Nabigazio-kontrolatzailea (erabilgarri gerta daiteke)
 * @param content Eskafoldaren edukia (Modifier jasotzen du padding-a aplikatzeko)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavController,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        bottomBar = {
            // Beheko barra (hutsik gerta daiteke baina espazioa utziz)
            Box(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
            ) {
                // Hutsik (baina espazioa utzi)
            }
        },
    ) { padding ->
        // Edukia padding-arekin
        content(Modifier.padding(padding))
    }
}