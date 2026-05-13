package com.rafdev.nestock.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafdev.nestock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user = viewModel.currentUser
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro que querés cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false; viewModel.logout(onLogout) }) {
                    Text("Cerrar sesión", color = OrangeAlert)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!viewModel.isDeleting) {
                    showDeleteDialog = false
                    viewModel.deletePassword = ""
                    viewModel.deleteError = null
                }
            },
            title = {
                Text(
                    "⚠️ Eliminar cuenta",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = OrangeAlert
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Esta acción es permanente e irreversible. Se eliminará:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        DeleteWarningItem("Tu cuenta y perfil (${user?.email ?: ""})")
                        DeleteWarningItem("Todos los hogares que creaste y su contenido")
                        DeleteWarningItem("Productos, categorías y listas de compras de esos hogares")
                        DeleteWarningItem("Tu historial de notificaciones")
                        DeleteWarningItem("Serás eliminado de hogares a los que te uniste como invitado")
                    }
                    if (viewModel.isEmailUser) {
                        Spacer(Modifier.height(2.dp))
                        OutlinedTextField(
                            value = viewModel.deletePassword,
                            onValueChange = { viewModel.deletePassword = it; viewModel.deleteError = null },
                            label = { Text("Confirmá tu contraseña") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            isError = viewModel.deleteError != null
                        )
                    }
                    viewModel.deleteError?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, color = OrangeAlert)
                    }
                }
            },
            confirmButton = {
                val canConfirm = !viewModel.isDeleting &&
                    (!viewModel.isEmailUser || viewModel.deletePassword.isNotBlank())
                Button(
                    onClick = { viewModel.deleteAccount(onLogout) },
                    enabled = canConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangeAlert),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (viewModel.isDeleting)
                        CircularProgressIndicator(Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    else
                        Text("Eliminar todo", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deletePassword = ""
                        viewModel.deleteError = null
                    },
                    enabled = !viewModel.isDeleting
                ) { Text("Cancelar", color = TextMuted) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, "Volver") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenDark, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().background(Background).padding(padding)
        ) {
            // Header verde
            item {
                Column(
                    Modifier.fillMaxWidth().background(GreenDark).padding(horizontal = 18.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier.size(62.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f)).border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Text("👤", style = MaterialTheme.typography.headlineLarge) }
                    Spacer(Modifier.height(9.dp))
                    Text(user?.displayName ?: "Usuario", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold)
                    Text(user?.email ?: "", style = MaterialTheme.typography.bodySmall, color = GreenLight, modifier = Modifier.padding(top = 3.dp))
                }
            }

            // Sección Cuenta
            item { SectionHeader("Cuenta") }
            item {
                ProfileCard {
                    ProfileRow(emoji = "🏡", label = "Mis hogares", onClick = {})
                    HorizontalDivider(color = Border)
                    ProfileRow(emoji = "👤", label = "Editar perfil", onClick = {})
                    HorizontalDivider(color = Border)
                    ProfileRowToggle(emoji = "🔔", label = "Notificaciones", checked = viewModel.notificationsEnabled, onCheckedChange = { viewModel.notificationsEnabled = it })
                }
            }

            // Sección Preferencias
            item { SectionHeader("Preferencias") }
            item {
                ProfileCard {
                    ProfileRowToggle(emoji = "🌙", label = "Modo oscuro", checked = viewModel.darkModeEnabled, onCheckedChange = { viewModel.darkModeEnabled = it })
                    HorizontalDivider(color = Border)
                    ProfileRow(emoji = "🌐", label = "Idioma", onClick = {})
                }
            }

            // Sección Soporte
            item { SectionHeader("Soporte") }
            item {
                ProfileCard {
                    ProfileRow(emoji = "🔒", label = "Privacidad", onClick = {})
                    HorizontalDivider(color = Border)
                    ProfileRow(emoji = "❓", label = "Ayuda y soporte", onClick = {})
                }
            }

            // Logout
            item { Spacer(Modifier.height(8.dp)) }
            item {
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp).height(46.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = OrangeAlert),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cerrar sesión", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold) }
            }

            // Eliminar cuenta
            item { Spacer(Modifier.height(6.dp)) }
            item {
                TextButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp)
                ) {
                    Text(
                        "Eliminar cuenta",
                        style = MaterialTheme.typography.labelMedium,
                        color = OrangeAlert.copy(alpha = 0.65f)
                    )
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title.uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted,
        modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp))
}

@Composable
private fun ProfileCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(13.dp)).background(Surface).border(1.dp, Border, RoundedCornerShape(13.dp)),
        content = content
    )
}

@Composable
private fun ProfileRow(emoji: String, label: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 13.dp, vertical = 11.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(30.dp).clip(RoundedCornerShape(8.dp)).background(GreenPale), contentAlignment = Alignment.Center) {
                Text(emoji, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.width(11.dp))
            Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text("›", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        }
    }
}

@Composable
private fun ProfileRowToggle(emoji: String, label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(30.dp).clip(RoundedCornerShape(8.dp)).background(Background), contentAlignment = Alignment.Center) {
            Text(emoji, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.width(11.dp))
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = TextPrimary)
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = Surface, checkedTrackColor = GreenDark))
    }
}

@Composable
private fun DeleteWarningItem(text: String) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("•", style = MaterialTheme.typography.bodySmall, color = OrangeAlert)
        Text(text, style = MaterialTheme.typography.bodySmall, color = TextMuted)
    }
}
