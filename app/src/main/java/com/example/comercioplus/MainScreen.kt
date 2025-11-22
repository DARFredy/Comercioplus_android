package com.example.comercioplus

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object Products : Screen("products", "Productos", Icons.Default.List)
    object Store : Screen("store", "Tienda", Icons.Default.Store)
    object Settings : Screen("settings", "Configuración", Icons.Default.Settings)
}

val items = listOf(
    Screen.Home,
    Screen.Products,
    Screen.Store,
    Screen.Settings,
)

@Composable
fun MainScreen(
    mainNavController: androidx.navigation.NavController,
    categoryViewModel: CategoryViewModel,
    storeViewModel: StoreViewModel,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    userViewModel: UserViewModel,
    themeViewModel: ThemeViewModel,
    dashboardViewModel: DashboardViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        floatingActionButton = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            if (navBackStackEntry?.destination?.route != Screen.Home.route) {
                FloatingActionButton(
                    onClick = { mainNavController.navigate("add_product") },
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product")
                }
            }
        },
        bottomBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                            }
                        },
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                DashboardScreen(
                    navController = mainNavController, 
                    dashboardViewModel = dashboardViewModel,
                    userViewModel = userViewModel,
                    storeViewModel = storeViewModel,
                    productViewModel = productViewModel,
                    categoryViewModel = categoryViewModel
                )
            }
            composable(Screen.Products.route) {
                 ProductsScreen(
                    navController = mainNavController,
                    productViewModel = productViewModel,
                    categoryViewModel = categoryViewModel
                )
            }
            composable(Screen.Store.route) {
                StoreScreen(
                    navController = mainNavController,
                    storeViewModel = storeViewModel,
                    productViewModel = productViewModel,
                    categoryViewModel = categoryViewModel,
                    userViewModel = userViewModel,
                    dashboardViewModel = dashboardViewModel
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    navController = mainNavController,
                    storeViewModel = storeViewModel,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}
