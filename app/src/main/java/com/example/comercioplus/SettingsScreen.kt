package com.example.comercioplus

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(
    navController: NavController, 
    storeViewModel: StoreViewModel,
    themeViewModel: ThemeViewModel
) {
    val themeState by themeViewModel.themeState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Configuración", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            SectionTitle("Apariencia")
            ThemeSettingItem(themeState = themeState, onThemeChange = { themeViewModel.setTheme(it) })
        }
        item {
            SectionTitle("Tienda")
            SettingItem(title = "Editar Tienda", icon = Icons.Default.ArrowForward) {
                navController.navigate("edit_store")
            }
            SettingItem(title = "Gestionar Categorías", icon = Icons.Default.Category) { // Nuevo item
                navController.navigate("categories")
            }
        }
        item {
            SectionTitle("Cuenta")
            SettingItem(title = "Cerrar sesión", icon = Icons.Default.ExitToApp) {
                navController.navigate("role_selection") {
                    popUpTo("main") { inclusive = true }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Icon(imageVector = icon, contentDescription = null)
        }
    }
}

@Composable
fun ThemeSettingItem(themeState: ThemeState, onThemeChange: (ThemeState) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Modo Oscuro", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Switch(
                checked = themeState == ThemeState.DARK,
                onCheckedChange = { isChecked ->
                    onThemeChange(if (isChecked) ThemeState.DARK else ThemeState.LIGHT)
                }
            )
        }
    }
}
