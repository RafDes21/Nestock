package com.rafdev.nestock.ui.screens.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rafdev.nestock.data.model.Category
import com.rafdev.nestock.ui.components.BottomNavBar
import com.rafdev.nestock.ui.components.ItemRow
import com.rafdev.nestock.ui.navigation.Screen
import com.rafdev.nestock.ui.theme.*

@Composable
fun InventoryScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToShopping: () -> Unit,
    onNavigateToHousehold: () -> Unit,
    onNavigateToItemDetail: (String) -> Unit,
    onNavigateToAddItem: () -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val items by viewModel.filteredItems.collectAsState()
    val categories by viewModel.categories.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = Screen.Inventory.route, onNavigate = { route ->
                when (route) {
                    Screen.Home.route      -> onNavigateToHome()
                    Screen.Shopping.route  -> onNavigateToShopping()
                    Screen.Household.route -> onNavigateToHousehold()
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddItem,
                containerColor = GreenDark,
                shape = RoundedCornerShape(13.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) { Icon(Icons.Filled.Add, contentDescription = "Agregar", tint = Surface) }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().background(Background).padding(padding)
        ) {
            // Header
            Column(
                Modifier.fillMaxWidth().background(Surface)
                    .padding(horizontal = 16.dp).padding(top = 46.dp, bottom = 13.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Inventario", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontFamily = FrauncesFamily)
                    Box(
                        Modifier.size(32.dp).clip(RoundedCornerShape(9.dp)).background(GreenPale).clickable(onClick = onNavigateToAddItem),
                        contentAlignment = Alignment.Center
                    ) { Text("＋", style = MaterialTheme.typography.bodyLarge, color = GreenDark) }
                }
                Spacer(Modifier.height(11.dp))
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Background)
                        .border(1.dp, Border, RoundedCornerShape(10.dp)).padding(horizontal = 11.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = viewModel.searchQuery,
                        onValueChange = { viewModel.searchQuery = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall.copy(color = TextPrimary),
                        decorationBox = { inner ->
                            if (viewModel.searchQuery.isEmpty()) Text("Buscar insumo...", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            inner()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Tabs categorías
            if (categories.isNotEmpty()) {
                Row(
                    Modifier.fillMaxWidth().background(Surface)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 15.dp, vertical = 11.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    CategoryTab(icon = null, label = "Todos", selected = viewModel.selectedCategory == null) {
                        viewModel.selectedCategory = null
                    }
                    categories.forEach { cat ->
                        CategoryTab(
                            icon = cat.icon,
                            label = cat.name,
                            selected = viewModel.selectedCategory?.id == cat.id
                        ) {
                            viewModel.selectedCategory = cat
                        }
                    }
                }
            }

            // Lista
            LazyColumn(
                Modifier.fillMaxSize().padding(horizontal = 15.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 11.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    ItemRow(item = item, onClick = { onNavigateToItemDetail(item.id) })
                }
                if (items.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Sin insumos", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryTab(icon: String?, label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.clip(RoundedCornerShape(18.dp))
            .background(if (selected) GreenDark else Surface)
            .border(1.dp, if (selected) GreenDark else Border, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 11.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            icon?.let { Text(it, style = MaterialTheme.typography.labelSmall) }
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = if (selected) Surface else TextMuted)
        }
    }
}
