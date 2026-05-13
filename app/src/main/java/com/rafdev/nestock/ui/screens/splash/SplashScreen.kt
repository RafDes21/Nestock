package com.rafdev.nestock.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafdev.nestock.ui.theme.FrauncesFamily
import com.rafdev.nestock.ui.theme.GreenDark
import com.rafdev.nestock.ui.theme.GreenLight
import com.rafdev.nestock.ui.theme.GreenPale

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToHouseholdSetup: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsState()

    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.HOME            -> onNavigateToHome()
            SplashDestination.HOUSEHOLD_SETUP -> onNavigateToHouseholdSetup()
            SplashDestination.LOGIN           -> onNavigateToLogin()
            SplashDestination.NONE            -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenDark)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "🏡",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "Nestock",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = FrauncesFamily,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "Tu inventario del hogar",
                style = MaterialTheme.typography.bodyMedium,
                color = GreenLight
            )
        }

        Text(
            text = "rafdev",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            style = MaterialTheme.typography.labelSmall,
            color = GreenPale.copy(alpha = 0.5f)
        )
    }
}
