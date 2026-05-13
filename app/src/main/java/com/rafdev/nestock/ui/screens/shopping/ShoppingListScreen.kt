package com.rafdev.nestock.ui.screens.shopping

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafdev.nestock.ui.components.BottomNavBar
import com.rafdev.nestock.ui.navigation.Screen
import com.rafdev.nestock.ui.theme.*

@Composable
fun ShoppingListScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToHousehold: () -> Unit,
    viewModel: ShoppingListViewModel = hiltViewModel()
) {
    val urgent by viewModel.urgent.collectAsState()
    val lowStock by viewModel.lowStock.collectAsState()
    val context = LocalContext.current

    val totalPending = urgent.size + lowStock.size

    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = Screen.Shopping.route, onNavigate = { route ->
                when (route) {
                    Screen.Home.route      -> onNavigateToHome()
                    Screen.Inventory.route -> onNavigateToInventory()
                    Screen.Household.route -> onNavigateToHousehold()
                }
            })
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().background(Background).padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Header
            item {
                Column(
                    Modifier.fillMaxWidth().background(Surface)
                        .padding(horizontal = 16.dp).padding(top = 46.dp, bottom = 14.dp)
                ) {
                    Text("Lista de compras", style = MaterialTheme.typography.headlineSmall, fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(
                        if (totalPending == 0) "¡Todo en orden!" else "$totalPending para comprar",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
            }

            // Estado vacío
            if (totalPending == 0) {
                item {
                    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("🎉", style = MaterialTheme.typography.displayMedium)
                            Text("Todo el stock está completo", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Cuando un insumo baje del mínimo\naparecerá aquí automáticamente.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                    }
                }
            }

            // Sección URGENTE
            if (urgent.isNotEmpty()) {
                item {
                    Row(
                        Modifier.padding(horizontal = 15.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            Modifier.clip(RoundedCornerShape(6.dp)).background(OrangeAlert).padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text("SIN STOCK", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Surface)
                        }
                        Text("Comprá esto primero", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                }
                items(urgent, key = { it.entry.id }) { state ->
                    ShoppingCard(state = state, onCompre = { viewModel.openPurchaseDialog(state) })
                }
            }

            // Sección BAJO STOCK
            if (lowStock.isNotEmpty()) {
                item {
                    Row(
                        Modifier.padding(horizontal = 15.dp).padding(top = if (urgent.isEmpty()) 11.dp else 16.dp, bottom = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            Modifier.clip(RoundedCornerShape(6.dp)).background(GreenMid).padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text("BAJO STOCK", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Surface)
                        }
                        Text("Quedan pocas unidades", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                }
                items(lowStock, key = { it.entry.id }) { state ->
                    ShoppingCard(state = state, onCompre = { viewModel.openPurchaseDialog(state) })
                }
            }

            // Botón compartir
            if (totalPending > 0) {
                item {
                    Box(
                        Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 13.dp)
                            .clip(RoundedCornerShape(13.dp)).background(GreenPale).padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, viewModel.shareText())
                            }
                            context.startActivity(Intent.createChooser(intent, "Compartir lista"))
                        }) {
                            Text("📤  Compartir lista", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = GreenDark)
                        }
                    }
                }
            }

        }
    }

    // Dialog confirmar compra
    viewModel.purchasingEntry?.let { state ->
        AlertDialog(
            onDismissRequest = { viewModel.purchasingEntry = null },
            title = {
                Text("¿Cuánto compraste?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(state.entry.itemName, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    OutlinedTextField(
                        value = viewModel.purchaseQtyInput,
                        onValueChange = { viewModel.purchaseQtyInput = it },
                        label = { Text("Cantidad (${state.entry.unit})") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "El stock se actualizará automáticamente",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmPurchase() }) {
                    Text("Confirmar", color = GreenDark, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.purchasingEntry = null }) {
                    Text("Cancelar", color = TextMuted)
                }
            }
        )
    }
}

@Composable
private fun ShoppingCard(
    state: ShoppingItemUiState,
    onCompre: () -> Unit
) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(13.dp)).background(Surface)
            .border(1.dp, if (state.isUrgent) OrangeAlert.copy(alpha = 0.3f) else Border, RoundedCornerShape(13.dp))
            .padding(13.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(state.entry.itemName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(Modifier.height(3.dp))
                Text(
                    buildStockText(state),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (state.isUrgent) OrangeAlert else TextMuted
                )
            }
            Box(
                Modifier.clip(RoundedCornerShape(9.dp))
                    .background(if (state.isUrgent) OrangePale else GreenPale)
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            ) {
                Text(
                    "Comprá ${state.toBuy.fmt()} ${state.entry.unit}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (state.isUrgent) OrangeAlert else GreenDark
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onCompre,
            modifier = Modifier.fillMaxWidth().height(38.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
            shape = RoundedCornerShape(9.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            Text("✓  Compré", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun buildStockText(state: ShoppingItemUiState): String {
    return if (state.isUrgent) {
        "Sin stock · mínimo: ${state.minQty.fmt()} ${state.entry.unit}"
    } else {
        "Tenés: ${state.currentQty.fmt()} · mínimo: ${state.minQty.fmt()} ${state.entry.unit}"
    }
}

private fun Double.fmt() = if (this == toLong().toDouble()) toLong().toString() else toString()
