package com.rafdev.nestock.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rafdev.nestock.ui.screens.auth.LoginScreen
import com.rafdev.nestock.ui.screens.auth.RegisterScreen
import com.rafdev.nestock.ui.screens.splash.SplashScreen
import com.rafdev.nestock.ui.screens.home.HomeScreen
import com.rafdev.nestock.ui.screens.household.HouseholdScreen
import com.rafdev.nestock.ui.screens.household.HouseholdSetupScreen
import com.rafdev.nestock.ui.screens.household.InviteScreen
import com.rafdev.nestock.ui.screens.inventory.AddItemScreen
import com.rafdev.nestock.ui.screens.inventory.InventoryScreen
import com.rafdev.nestock.ui.screens.inventory.ItemDetailScreen
import com.rafdev.nestock.ui.screens.notifications.NotificationsScreen
import com.rafdev.nestock.ui.screens.profile.ProfileScreen
import com.rafdev.nestock.ui.screens.shopping.ShoppingListScreen

@Composable
fun NestockNavGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHouseholdSetup = {
                    navController.navigate(Screen.HouseholdSetup.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { hasHousehold ->
                    val destination = if (hasHousehold) Screen.Home.route else Screen.HouseholdSetup.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.HouseholdSetup.route) {
            HouseholdSetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.HouseholdSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToInventory     = { navController.navigate(Screen.Inventory.route) },
                onNavigateToShopping      = { navController.navigate(Screen.Shopping.route) },
                onNavigateToHousehold     = { navController.navigate(Screen.Household.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToLogin         = {
                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Screen.Inventory.route) {
            InventoryScreen(
                onNavigateToHome       = { navController.navigate(Screen.Home.route) },
                onNavigateToShopping   = { navController.navigate(Screen.Shopping.route) },
                onNavigateToHousehold  = { navController.navigate(Screen.Household.route) },
                onNavigateToItemDetail = { itemId -> navController.navigate(Screen.ItemDetail.createRoute(itemId)) },
                onNavigateToAddItem    = { navController.navigate(Screen.AddItem.route) }
            )
        }

        composable(
            route = Screen.ItemDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { back ->
            val itemId = back.arguments?.getString("itemId") ?: return@composable
            ItemDetailScreen(
                itemId = itemId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Screen.EditItem.createRoute(itemId)) }
            )
        }

        composable(Screen.AddItem.route) {
            AddItemScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.EditItem.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { back ->
            val itemId = back.arguments?.getString("itemId") ?: return@composable
            AddItemScreen(itemId = itemId, onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Shopping.route) {
            ShoppingListScreen(
                onNavigateToHome      = { navController.navigate(Screen.Home.route) },
                onNavigateToInventory = { navController.navigate(Screen.Inventory.route) },
                onNavigateToHousehold = { navController.navigate(Screen.Household.route) }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Household.route) {
            HouseholdScreen(
                onNavigateToHome      = { navController.navigate(Screen.Home.route) },
                onNavigateToInventory = { navController.navigate(Screen.Inventory.route) },
                onNavigateToShopping  = { navController.navigate(Screen.Shopping.route) },
                onNavigateToInvite    = { navController.navigate(Screen.Invite.route) },
                onNavigateToProfile   = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.Invite.route) {
            InviteScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
