package com.example.comercioplus

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.comercioplus.model.Role

sealed class UserScreen(val route: String, val label: String, val icon: ImageVector) {
    object Home : UserScreen("user_home", "Inicio", Icons.Default.Home)
    object Cart : UserScreen("user_cart", "Carrito", Icons.Default.ShoppingCart)
    object Profile : UserScreen("user_profile", "Perfil", Icons.Default.Person)
    object Exit : UserScreen("role_selection", "Salir", Icons.Default.ExitToApp)
}

@Composable
fun UserMainScreen(
    mainNavController: NavController,
    productViewModel: ProductViewModel,
    categoryViewModel: CategoryViewModel,
    cartViewModel: CartViewModel,
    userViewModel: UserViewModel,
    themeViewModel: ThemeViewModel,
    storeViewModel: StoreViewModel,
    dashboardViewModel: DashboardViewModel
) {
    val navController = rememberNavController()
    val userProfile by userViewModel.userProfile.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    
    // Calculamos el total de items para el badge
    val cartCount = cartItems.sumOf { it.quantity }
    
    val userItems = remember(userProfile.role) {
        if (userProfile.role == Role.INVITADO) {
            listOf(UserScreen.Home, UserScreen.Cart, UserScreen.Exit)
        } else {
            listOf(UserScreen.Home, UserScreen.Cart, UserScreen.Profile)
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                userItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (screen == UserScreen.Cart && cartCount > 0) {
                                        Badge(
                                            containerColor = Color(0xFFE65A00),
                                            contentColor = Color.White
                                        ) {
                                            Text(cartCount.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(screen.icon, contentDescription = null)
                            }
                        },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = { 
                            if (screen == UserScreen.Exit) {
                                mainNavController.navigate(screen.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            } else {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFE65A00),
                            unselectedIconColor = Color.Gray,
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = UserScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(UserScreen.Home.route) {
                DashboardScreen(
                    navController = mainNavController,
                    dashboardViewModel = dashboardViewModel,
                    userViewModel = userViewModel,
                    storeViewModel = storeViewModel,
                    productViewModel = productViewModel,
                    categoryViewModel = categoryViewModel
                )
            }
            composable(UserScreen.Cart.route) {
                CartScreen(navController = mainNavController, cartViewModel = cartViewModel)
            }
            composable(UserScreen.Profile.route) {
                ProfileScreen(navController = mainNavController, userViewModel = userViewModel, themeViewModel = themeViewModel)
            }
        }
    }
}
