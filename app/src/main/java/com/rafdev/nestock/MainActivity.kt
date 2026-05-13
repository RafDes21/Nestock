package com.rafdev.nestock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.rafdev.nestock.ui.navigation.NestockNavGraph
import com.rafdev.nestock.ui.navigation.Screen
import com.rafdev.nestock.ui.theme.NestockTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NestockTheme {
                NestockNavGraph(
                    navController = rememberNavController(),
                    startDestination = Screen.Splash.route
                )
            }
        }
    }
}
