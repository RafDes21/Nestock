package com.rafdev.nestock.ui.screens.household

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafdev.nestock.ui.theme.*

@Composable
fun HouseholdSetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: HouseholdSetupViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenDark)
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🏡", style = MaterialTheme.typography.displayMedium)
            Spacer(Modifier.height(12.dp))
            Text(
                "¡Bienvenido a Nestock!",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = FrauncesFamily,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Para empezar, creá tu hogar o unite a uno existente.",
                style = MaterialTheme.typography.bodySmall,
                color = GreenLight,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(24.dp))

        // Crear hogar
        SetupCard(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                "Crear un hogar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Vas a ser el administrador. Podés invitar a tu familia después.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = viewModel.householdName,
                onValueChange = { viewModel.householdName = it; viewModel.createError = null },
                label = { Text("Nombre del hogar") },
                placeholder = { Text("Ej: Casa de los García") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                isError = viewModel.createError != null,
                colors = setupTextFieldColors()
            )
            viewModel.createError?.let {
                Text(it, style = MaterialTheme.typography.labelSmall, color = OrangeAlert,
                    modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { viewModel.createHousehold(onSetupComplete) },
                enabled = !viewModel.isLoading && viewModel.householdName.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (viewModel.isLoading)
                    CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                else
                    Text("Crear hogar", fontWeight = FontWeight.SemiBold)
            }
        }

        // Divisor
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(Modifier.weight(1f), color = Border)
            Text(
                "  o  ",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            HorizontalDivider(Modifier.weight(1f), color = Border)
        }

        // Unirse con código
        SetupCard(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                "Tengo un código de invitación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Ingresá el código que te compartió el administrador del hogar.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = viewModel.inviteCode,
                onValueChange = { viewModel.inviteCode = it.uppercase(); viewModel.joinError = null },
                label = { Text("Código de invitación") },
                placeholder = { Text("Ej: N3S7CK") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                isError = viewModel.joinError != null,
                colors = setupTextFieldColors()
            )
            viewModel.joinError?.let {
                Text(it, style = MaterialTheme.typography.labelSmall, color = OrangeAlert,
                    modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { viewModel.joinHousehold(onSetupComplete) },
                enabled = !viewModel.isLoading && viewModel.inviteCode.length == 6,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (viewModel.isLoading)
                    CircularProgressIndicator(Modifier.size(18.dp), color = GreenDark, strokeWidth = 2.dp)
                else
                    Text("Unirme al hogar", fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SetupCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(14.dp)),
        content = {
            Column(
                modifier = Modifier.padding(16.dp),
                content = content
            )
        }
    )
}

@Composable
private fun setupTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = GreenDark,
    unfocusedBorderColor = Border,
    focusedLabelColor    = GreenDark,
    unfocusedLabelColor  = TextMuted,
    focusedTextColor     = TextPrimary,
    unfocusedTextColor   = TextPrimary,
    cursorColor          = GreenDark
)
