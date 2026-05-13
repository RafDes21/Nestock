package com.rafdev.nestock.ui.screens.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafdev.nestock.ui.components.ConfirmDeleteDialog
import com.rafdev.nestock.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: ItemDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(itemId) { viewModel.loadItem(itemId) }
    val item by viewModel.item.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    item?.let { currentItem ->
                        IconButton(onClick = { onNavigateToEdit(currentItem.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color.White)
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.White.copy(alpha = 0.85f))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenDark)
            )
        }
    ) { padding ->
        item?.let { it ->
            LazyColumn(Modifier.fillMaxSize().background(Background).padding(padding)) {
                // Header verde
                item {
                    Column(Modifier.fillMaxWidth().background(GreenDark).padding(horizontal = 18.dp, vertical = 20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) { Text("📦", style = MaterialTheme.typography.headlineMedium) }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(it.name, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontFamily = FrauncesFamily, fontWeight = FontWeight.SemiBold)
                                Text(it.categoryId.ifEmpty { "Sin categoría" } + " · " + it.unit, style = MaterialTheme.typography.labelSmall, color = GreenLight)
                            }
                        }
                    }
                }

                // Alerta bajo stock
                if (it.isLowStock) {
                    item {
                        Row(
                            Modifier.fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(OrangePale)
                                .padding(horizontal = 13.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚠️", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.width(7.dp))
                            Text("Stock bajo — aparece en la lista de compras", style = MaterialTheme.typography.bodySmall, color = OrangeAlert, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Stats
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        listOf(
                            Triple("${it.quantity.fmt()}", "Stock actual", it.isLowStock),
                            Triple("${it.minQuantity.fmt()}", "Mínimo", false),
                            Triple("${it.optimalQuantity.fmt()}", "Óptimo", false)
                        ).forEach { (value, label, warn) ->
                            Column(
                                Modifier.weight(1f).clip(RoundedCornerShape(13.dp))
                                    .background(Surface).border(1.dp, Border, RoundedCornerShape(13.dp)).padding(11.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = if (warn) OrangeAlert else GreenDark, fontFamily = FrauncesFamily)
                                Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                        }
                    }
                }

                // Barcode
                if (!it.barcode.isNullOrEmpty()) {
                    item {
                        Column(
                            Modifier.fillMaxWidth().padding(horizontal = 14.dp)
                                .clip(RoundedCornerShape(13.dp)).background(Surface)
                                .border(1.dp, Border, RoundedCornerShape(13.dp)).padding(13.dp)
                        ) {
                            Text("Código de barras", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                it.barcode!!,
                                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Background).padding(8.dp, 6.dp)
                            )
                        }
                    }
                }

                // Última actualización
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp)
                            .clip(RoundedCornerShape(13.dp)).background(Surface)
                            .border(1.dp, Border, RoundedCornerShape(13.dp)).padding(13.dp)
                    ) {
                        Text("Última actualización", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted)
                        Spacer(Modifier.height(9.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(26.dp).clip(RoundedCornerShape(13.dp)).background(GreenPale), contentAlignment = Alignment.Center) {
                                Text(it.updatedBy.firstOrNull()?.uppercase() ?: "?", style = MaterialTheme.typography.labelMedium, color = GreenDark, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(9.dp))
                            Text("Actualizado por ${it.updatedBy.take(8)}", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                    }
                }

                // Acciones cantidad
                item { Spacer(Modifier.height(8.dp)) }
                item {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.reduce(it.id) },
                            modifier = Modifier.weight(1f).height(46.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.5.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenDark),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("− Reducir", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold) }
                        Button(
                            onClick = { viewModel.add(it.id) },
                            modifier = Modifier.weight(1f).height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("+ Agregar", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
        } ?: Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = GreenDark)
        }
    }

    if (showDeleteDialog) {
        ConfirmDeleteDialog(
            title = "Eliminar insumo",
            message = "¿Eliminar \"${item?.name}\"? Esta acción no se puede deshacer.",
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteItem(itemId) { onNavigateBack() }
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

private fun Double.fmt() = if (this == toLong().toDouble()) toLong().toString() else toString()
