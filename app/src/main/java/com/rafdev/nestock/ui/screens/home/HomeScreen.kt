package com.rafdev.nestock.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafdev.nestock.ui.components.AlertBanner
import com.rafdev.nestock.ui.components.BottomNavBar
import com.rafdev.nestock.ui.components.CategoryCard
import com.rafdev.nestock.ui.navigation.Screen
import com.rafdev.nestock.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToInventory: () -> Unit,
    onNavigateToShopping: () -> Unit,
    onNavigateToHousehold: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val household by viewModel.household.collectAsState()
    val lowStockItems by viewModel.lowStockItems.collectAsState()
    val categorySummary by viewModel.categorySummary.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val householdId by viewModel.householdId.collectAsState()
    val isLoading by viewModel.isLoadingHousehold.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var newHouseholdName by remember { mutableStateOf("") }

    if (isLoading) {
        Box(Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = GreenDark)
        }
        return
    }

    // Sin hogar asignado: mostrar pantalla de setup
    if (householdId == null) {
        Box(
            Modifier.fillMaxSize().background(Background).imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Text("🏡", style = MaterialTheme.typography.displayLarge)
                Spacer(Modifier.height(16.dp))
                Text(
                    "Bienvenido a Nestock",
                    style = MaterialTheme.typography.headlineMedium,
                    color = GreenDark,
                    fontFamily = FrauncesFamily,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Creá un hogar o ingresá un código para unirte a uno existente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(28.dp))

                // Crear hogar
                Button(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                    shape = RoundedCornerShape(13.dp)
                ) { Text("Crear hogar") }

                Spacer(Modifier.height(20.dp))

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(Modifier.weight(1f), color = Border)
                    Text("  o unirse con código  ", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    HorizontalDivider(Modifier.weight(1f), color = Border)
                }

                Spacer(Modifier.height(16.dp))

                // Campo de código
                OutlinedTextField(
                    value = viewModel.joinCode,
                    onValueChange = { viewModel.joinCode = it.uppercase() },
                    label = { Text("Código de invitación") },
                    placeholder = { Text("Ej: AB12CD") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(13.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    isError = viewModel.joinError != null,
                    supportingText = viewModel.joinError?.let { { Text(it, color = OrangeAlert) } }
                )

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { viewModel.joinWithCode {} },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = viewModel.joinCode.length >= 6 && !viewModel.isJoining,
                    shape = RoundedCornerShape(13.dp)
                ) {
                    if (viewModel.isJoining)
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = GreenDark)
                    else
                        Text("Unirse al hogar")
                }

                Spacer(Modifier.height(24.dp))

                TextButton(onClick = { viewModel.signOut { onNavigateToLogin() } }) {
                    Text("Cerrar sesión", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Nuevo hogar") },
                text = {
                    OutlinedTextField(
                        value = newHouseholdName,
                        onValueChange = { newHouseholdName = it },
                        label = { Text("Nombre del hogar") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newHouseholdName.isNotBlank())
                            viewModel.createHousehold(newHouseholdName) { showCreateDialog = false }
                    }) { Text("Crear") }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) { Text("Cancelar") }
                }
            )
        }
        return
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = Screen.Home.route, onNavigate = { route ->
                when (route) {
                    Screen.Inventory.route -> onNavigateToInventory()
                    Screen.Shopping.route  -> onNavigateToShopping()
                    Screen.Household.route -> onNavigateToHousehold()
                }
            })
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().background(Background).padding(padding)) {
            // Header verde
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(GreenDark)
                        .padding(horizontal = 18.dp, vertical = 20.dp)
                        .padding(top = 28.dp)
                ) {
                    Column {
                        Text(
                            "Buenos días, ${viewModel.currentUser?.displayName?.split(" ")?.firstOrNull() ?: "👋"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GreenLight
                        )
                        Text(
                            household?.name ?: "Tu hogar",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontFamily = FrauncesFamily
                        )
                        Spacer(Modifier.height(8.dp))
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .padding(horizontal = 11.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "🏡  ${household?.name ?: ""} · ${household?.members?.size ?: 0} miembros",
                                style = MaterialTheme.typography.labelSmall,
                                color = GreenPale
                            )
                        }
                    }
                    IconButton(
                        onClick = onNavigateToNotifications,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones", tint = GreenPale)
                    }
                }
            }

            item {
                Column(Modifier.padding(horizontal = 15.dp, vertical = 14.dp)) {
                    if (lowStockItems.isNotEmpty()) {
                        AlertBanner(lowStockItems = lowStockItems)
                        Spacer(Modifier.height(12.dp))
                    }

                    Text(
                        "Categorías",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        modifier = Modifier.padding(bottom = 9.dp)
                    )

                    if (categories.isEmpty()) {
                        Box(
                            Modifier.fillMaxWidth()
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                                .background(Surface)
                                .border(1.dp, Border, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Aún no hay categorías. Creá una en Mi Hogar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    } else {
                        for (i in categories.indices step 2) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(Modifier.weight(1f)) {
                                    CategoryCard(
                                        category = categories[i],
                                        itemCount = categorySummary[categories[i].id] ?: 0,
                                        onClick = onNavigateToInventory
                                    )
                                }
                                if (i + 1 < categories.size) {
                                    Box(Modifier.weight(1f)) {
                                        CategoryCard(
                                            category = categories[i + 1],
                                            itemCount = categorySummary[categories[i + 1].id] ?: 0,
                                            onClick = onNavigateToInventory
                                        )
                                    }
                                } else {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}
