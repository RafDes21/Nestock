package com.rafdev.nestock.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafdev.nestock.ui.theme.*

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(LoginBg)
            .imePadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.height(60.dp))

                // Logo
                Box(
                    Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(GreenLight.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) { Text("✨", style = MaterialTheme.typography.headlineLarge) }

                Spacer(Modifier.height(16.dp))

                Text(
                    "Crear cuenta",
                    style = MaterialTheme.typography.displayMedium,
                    color = GreenPale,
                    fontFamily = FrauncesFamily
                )
                Text(
                    "Únete a Nestock gratis",
                    style = MaterialTheme.typography.bodySmall,
                    color = GreenLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 5.dp)
                )

                Spacer(Modifier.height(36.dp))

                // Error
                viewModel.error?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = OrangeAlert,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                OutlinedTextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.name = it },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    shape = RoundedCornerShape(13.dp),
                    colors = registerTextFieldColors()
                )
                Spacer(Modifier.height(9.dp))
                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(13.dp),
                    colors = registerTextFieldColors()
                )
                Spacer(Modifier.height(9.dp))
                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(13.dp),
                    colors = registerTextFieldColors()
                )
                Spacer(Modifier.height(9.dp))
                OutlinedTextField(
                    value = viewModel.confirmPassword,
                    onValueChange = { viewModel.confirmPassword = it },
                    label = { Text("Confirmar contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(13.dp),
                    colors = registerTextFieldColors()
                )
                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.register(onRegisterSuccess) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = !viewModel.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                    shape = RoundedCornerShape(13.dp)
                ) {
                    if (viewModel.isLoading)
                        CircularProgressIndicator(Modifier.size(20.dp), color = Surface, strokeWidth = 2.dp)
                    else
                        Text("Crear cuenta", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text("¿Ya tienes cuenta? ", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.3f))
                    Text(
                        "Inicia sesión",
                        style = MaterialTheme.typography.labelSmall,
                        color = GreenLight,
                        modifier = Modifier.clickable(onClick = onNavigateBack)
                    )
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun registerTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = GreenLight,
    unfocusedBorderColor = GreenLight.copy(alpha = 0.3f),
    focusedLabelColor    = GreenLight,
    unfocusedLabelColor  = GreenLight.copy(alpha = 0.5f),
    focusedTextColor     = GreenPale,
    unfocusedTextColor   = GreenPale.copy(alpha = 0.7f),
    cursorColor          = GreenLight
)
