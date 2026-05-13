package com.rafdev.nestock.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.rafdev.nestock.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (hasHousehold: Boolean) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel.error) {
        viewModel.error?.let {
            Toast.makeText(context, "Firebase: $it", Toast.LENGTH_LONG).show()
        }
    }

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
                Spacer(Modifier.height(72.dp))

                // Logo
                Box(
                    Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(GreenLight.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) { Text("🏡", style = MaterialTheme.typography.headlineLarge) }

                Spacer(Modifier.height(16.dp))

                Text(
                    "nestock",
                    style = MaterialTheme.typography.displayMedium,
                    color = GreenPale,
                    fontFamily = FrauncesFamily
                )
                Text(
                    "Control de insumos del hogar",
                    style = MaterialTheme.typography.bodySmall,
                    color = GreenLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 5.dp)
                )

                Spacer(Modifier.height(40.dp))

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
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(13.dp),
                    colors = authTextFieldColors()
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
                    colors = authTextFieldColors()
                )
                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.login(onLoginSuccess) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = !viewModel.isLoading && viewModel.email.isNotBlank() && viewModel.password.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                    shape = RoundedCornerShape(13.dp)
                ) {
                    if (viewModel.isLoading)
                        CircularProgressIndicator(Modifier.size(20.dp), color = Surface, strokeWidth = 2.dp)
                    else
                        Text("Iniciar sesión", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
                    Text("  o continuar con  ", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.28f))
                    HorizontalDivider(Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
                }
                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            try {
                                val credMan = CredentialManager.create(context)
                                val option = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(context.getString(com.rafdev.nestock.R.string.default_web_client_id))
                                    .setAutoSelectEnabled(false)
                                    .build()
                                val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
                                val result = credMan.getCredential(context, request)
                                val googleCred = GoogleIdTokenCredential.createFrom(result.credential.data)
                                viewModel.loginWithGoogle(googleCred.idToken, onLoginSuccess)
                            } catch (e: Exception) {
                                Log.e("GoogleSignIn", "${e::class.simpleName}: ${e.message}", e)
                                Toast.makeText(context, "${e::class.simpleName}: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.06f),
                        contentColor = Color.White.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(13.dp)
                ) {
                    Text("G   Continuar con Google", style = MaterialTheme.typography.labelMedium)
                }

                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text("¿No tienes cuenta? ", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.3f))
                    Text(
                        "Regístrate",
                        style = MaterialTheme.typography.labelSmall,
                        color = GreenLight,
                        modifier = Modifier.clickable(onClick = onNavigateToRegister)
                    )
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = GreenLight,
    unfocusedBorderColor = GreenLight.copy(alpha = 0.3f),
    focusedLabelColor    = GreenLight,
    unfocusedLabelColor  = GreenLight.copy(alpha = 0.5f),
    focusedTextColor     = GreenPale,
    unfocusedTextColor   = GreenPale.copy(alpha = 0.7f),
    cursorColor          = GreenLight
)
