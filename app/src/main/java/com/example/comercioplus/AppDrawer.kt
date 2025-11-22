package com.example.comercioplus

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AppDrawer(navController: NavController, currentRoute: String?) {
    Column {
        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
            label = { Text("Dashboard") },
            selected = currentRoute == "dashboard",
            onClick = { navController.navigate("dashboard") { launchSingleTop = true } },
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.ShoppingBag, contentDescription = null) },
            label = { Text("Productos") },
            selected = currentRoute == "product_list",
            onClick = { navController.navigate("product_list") { launchSingleTop = true } },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.Category, contentDescription = null) },
            label = { Text("Categorías") },
            selected = currentRoute == "categories",
            onClick = { navController.navigate("categories") { launchSingleTop = true } },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
            label = { Text("Configuración") },
            selected = currentRoute == "settings",
            onClick = { navController.navigate("settings") { launchSingleTop = true } },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
