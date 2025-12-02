package com.example.errenteriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.errenteriaapp.navigation.Screens.HomeSreen
import com.example.errenteriaapp.ui.theme.ErrenteriaappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ErrenteriaappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeSreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


