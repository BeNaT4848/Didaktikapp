package com.example.errenteriaapp.components



import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.errenteriaapp.navigation.Routes
import com.example.errenteriaapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingTopBar(
    showContent: Boolean,
    navController: NavController
) {
    val onPrimaryColor = Color(0xFFFFFFFF)

    AnimatedVisibility(
        visible = showContent,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -40 }),
        modifier = Modifier.background(
            brush = Brush.linearGradient(
                colors = listOf(
                    primaryLight,
                    primaryContainerLight
                )
            )
        )
    ) {
        CenterAlignedTopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700)
                    )
                    Text(
                        "Puntuetako Rankina",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = onPrimaryColor
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigate(Routes.GPS_SCREEN) },
                    modifier = Modifier.alpha(if (showContent) 1f else 0f)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Itzuli",
                        tint = onPrimaryColor
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = onPrimaryColor,
                navigationIconContentColor = onPrimaryColor
            )
        )
    }
}