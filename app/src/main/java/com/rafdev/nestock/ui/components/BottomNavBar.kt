package com.rafdev.nestock.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.rafdev.nestock.ui.navigation.Screen

private data class NavItem(val label: String, val icon: ImageVector, val route: String)

private val items = listOf(
    NavItem("Inicio",     Icons.Filled.Home,         Screen.Home.route),
    NavItem("Inventario", Icons.Filled.Inventory2,   Screen.Inventory.route),
    NavItem("Compras",    Icons.Filled.ShoppingCart, Screen.Shopping.route),
    NavItem("Hogar",      Icons.Filled.Group,        Screen.Household.route)
)

@Composable
fun BottomNavBar(currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick  = { if (currentRoute != item.route) onNavigate(item.route) },
                icon     = { Icon(item.icon, contentDescription = item.label) },
                label    = { Text(item.label) }
            )
        }
    }
}
