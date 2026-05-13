package com.rafdev.nestock.ui.screens.household

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafdev.nestock.data.model.Category
import com.rafdev.nestock.ui.components.BottomNavBar
import com.rafdev.nestock.ui.components.ConfirmDeleteDialog
import com.rafdev.nestock.ui.navigation.Screen
import com.rafdev.nestock.ui.theme.*

@Composable
fun HouseholdScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToShopping: () -> Unit,
    onNavigateToInvite: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HouseholdViewModel = hiltViewModel()
) {
    val household by viewModel.household.collectAsState()
    val categories by viewModel.categories.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = Screen.Household.route, onNavigate = { route ->
                when (route) {
                    Screen.Home.route      -> onNavigateToHome()
                    Screen.Inventory.route -> onNavigateToInventory()
                    Screen.Shopping.route  -> onNavigateToShopping()
                }
            })
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().background(Background).padding(padding)
        ) {
            // Header verde
            item {
                Box(
                    Modifier.fillMaxWidth().background(GreenDark)
                        .padding(horizontal = 18.dp, vertical = 20.dp).padding(top = 28.dp)
                ) {
                    Column {
                        Text(
                            household?.name ?: "Mi hogar",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontFamily = FrauncesFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Creado por ${household?.createdBy?.take(8) ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GreenLight,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    TextButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text("Perfil", style = MaterialTheme.typography.labelMedium, color = GreenPale)
                    }
                }
            }

            // Miembros
            item {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(13.dp)).background(Surface)
                        .border(1.dp, Border, RoundedCornerShape(13.dp))
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 11.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "👥  Miembros (${household?.members?.size ?: 0})",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        TextButton(onClick = onNavigateToInvite) {
                            Text("Invitar", style = MaterialTheme.typography.labelMedium, color = GreenDark, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    HorizontalDivider(color = Border)
                    household?.members?.entries?.forEach { (uid, role) ->
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier.size(32.dp).clip(CircleShape).background(GreenMid),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(Modifier.width(9.dp))
                            Text(uid.take(12), Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Box(
                                Modifier.clip(RoundedCornerShape(7.dp))
                                    .background(if (role == "owner") GreenPale else Border)
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    if (role == "owner") "Owner" else "Miembro",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (role == "owner") GreenDark else TextMuted
                                )
                            }
                        }
                        HorizontalDivider(color = Border, modifier = Modifier.padding(start = 54.dp))
                    }
                }
            }

            // Categorías
            item {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 5.dp)
                        .clip(RoundedCornerShape(13.dp)).background(Surface)
                        .border(1.dp, Border, RoundedCornerShape(13.dp))
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 11.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "🗂  Categorías (${categories.size}/${viewModel.MAX_CATEGORIES})",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        TextButton(
                            onClick = { viewModel.openAddCategory() },
                            enabled = categories.size < viewModel.MAX_CATEGORIES
                        ) {
                            Text(
                                "+ Nueva",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (categories.size < viewModel.MAX_CATEGORIES) GreenDark else TextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    HorizontalDivider(color = Border)
                    if (categories.isEmpty()) {
                        Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            Text("Sin categorías aún. Agregá la primera.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                    } else {
                        categories.forEach { category ->
                            CategoryRow(
                                category = category,
                                onEdit = { viewModel.openEditCategory(category) },
                                onDelete = { viewModel.openDeleteCategory(category) }
                            )
                            HorizontalDivider(color = Border, modifier = Modifier.padding(start = 13.dp))
                        }
                    }
                }
            }

            // Opciones de invitación
            item {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 5.dp)
                        .clip(RoundedCornerShape(13.dp)).background(Surface)
                        .border(1.dp, Border, RoundedCornerShape(13.dp)).padding(13.dp)
                ) {
                    Text("Invitar al hogar", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.padding(bottom = 9.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("🔢 Código", "📷 QR", "🔗 Link").forEach { label ->
                            OutlinedButton(
                                onClick = onNavigateToInvite,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) { Text(label, style = MaterialTheme.typography.labelSmall) }
                        }
                    }
                }
            }

            // Estadísticas
            item {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 5.dp)
                        .clip(RoundedCornerShape(13.dp)).background(Surface)
                        .border(1.dp, Border, RoundedCornerShape(13.dp)).padding(13.dp)
                ) {
                    Text("Estadísticas del hogar", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.padding(bottom = 9.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        listOf(
                            "${household?.members?.size ?: 0}" to "Miembros",
                            "${categories.size}" to "Categorías"
                        ).forEach { (value, label) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = GreenDark, fontFamily = FrauncesFamily)
                                Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }

    // Dialogs
    if (viewModel.showAddCategoryDialog) {
        CategoryDialog(
            title = "Nueva categoría",
            name = viewModel.editCategoryName,
            onNameChange = { viewModel.editCategoryName = it },
            icon = viewModel.editCategoryIcon,
            onIconChange = { viewModel.editCategoryIcon = it },
            error = viewModel.categoryError,
            onConfirm = { viewModel.saveNewCategory() },
            onDismiss = { viewModel.showAddCategoryDialog = false }
        )
    }

    if (viewModel.showEditCategoryDialog) {
        CategoryDialog(
            title = "Editar categoría",
            name = viewModel.editCategoryName,
            onNameChange = { viewModel.editCategoryName = it },
            icon = viewModel.editCategoryIcon,
            onIconChange = { viewModel.editCategoryIcon = it },
            error = viewModel.categoryError,
            onConfirm = { viewModel.saveEditCategory() },
            onDismiss = { viewModel.showEditCategoryDialog = false }
        )
    }

    if (viewModel.showDeleteCategoryDialog) {
        ConfirmDeleteDialog(
            title = "Eliminar categoría",
            message = "¿Eliminar \"${viewModel.selectedCategory?.name}\"? Los insumos de esta categoría quedarán sin categoría.",
            onConfirm = { viewModel.confirmDeleteCategory() },
            onDismiss = { viewModel.showDeleteCategoryDialog = false }
        )
    }
}

@Composable
private fun CategoryRow(
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(34.dp).clip(RoundedCornerShape(9.dp)).background(GreenPale),
            contentAlignment = Alignment.Center
        ) {
            Text(category.icon, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.width(11.dp))
        Text(category.name, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = GreenMid, modifier = Modifier.size(17.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = OrangeAlert, modifier = Modifier.size(17.dp))
        }
    }
}

@Composable
private fun CategoryDialog(
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    icon: String,
    onIconChange: (String) -> Unit,
    error: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val commonIcons = listOf("📦", "🥦", "🍎", "🥛", "🧴", "🧹", "🧺", "💊", "🌿", "🔌", "🛒", "🧻", "☕", "🍳", "🐾", "🧼")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Selector de iconos
                Text("Ícono", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted)
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(commonIcons.size) { i ->
                        val emoji = commonIcons[i]
                        Box(
                            Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .background(if (icon == emoji) GreenPale else Background)
                                .border(1.5.dp, if (icon == emoji) GreenDark else Border, RoundedCornerShape(9.dp))
                                .then(Modifier.wrapContentSize()),
                            contentAlignment = Alignment.Center
                        ) {
                            TextButton(
                                onClick = { onIconChange(emoji) },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(emoji, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                // Nombre
                Text("Nombre", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted)
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    placeholder = { Text("Ej: Alimentos", style = MaterialTheme.typography.bodySmall, color = TextMuted) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                )
                error?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, color = OrangeAlert)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Guardar", color = GreenDark, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextMuted)
            }
        }
    )
}
