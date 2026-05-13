package com.rafdev.nestock.ui.navigation

sealed class Screen(val route: String) {
    object Splash      : Screen("splash")
    object Login       : Screen("login")
    object Register    : Screen("register")
    object Home        : Screen("home")
    object Inventory   : Screen("inventory")
    object ItemDetail  : Screen("itemDetail/{itemId}") {
        fun createRoute(itemId: String) = "itemDetail/$itemId"
    }
    object AddItem     : Screen("addItem")
    object EditItem    : Screen("editItem/{itemId}") {
        fun createRoute(itemId: String) = "editItem/$itemId"
    }
    object Shopping    : Screen("shopping")
    object Notifications: Screen("notifications")
    object Household   : Screen("household")
    object Invite          : Screen("invite")
    object Profile         : Screen("profile")
    object HouseholdSetup  : Screen("householdSetup")
}
