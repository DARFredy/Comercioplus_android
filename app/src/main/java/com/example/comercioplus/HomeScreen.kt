package com.example.comercioplus

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class Statistic(val label: String, val value: String, val icon: ImageVector, val route: String? = null)

@Composable
fun HomeScreen(
    navController: NavController,
    categoryViewModel: CategoryViewModel,
    storeViewModel: StoreViewModel,
    productViewModel: ProductViewModel
) {
    val products by productViewModel.products.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()

    val statistics = listOf(
        Statistic("Total de Productos", products.size.toString(), Icons.Default.Archive, "products"),
        Statistic("Total de Categorías", categories.size.toString(), Icons.Default.Category, "categories"),
        Statistic("Ventas del Mes", "$1,250", Icons.Default.AttachMoney), // Mock data
        Statistic("Pedidos Nuevos", "12", Icons.Default.ShoppingCart)      // Mock data
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.headlineLarge)
        Text("Resumen de tu tienda", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(statistics) { statistic ->
                StatisticCard(statistic = statistic, onClick = {
                    statistic.route?.let { route ->
                        navController.navigate(route)
                    }
                })
            }
        }
    }
}

@Composable
fun StatisticCard(statistic: Statistic, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = statistic.route != null, onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(statistic.icon, contentDescription = statistic.label, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = statistic.value, 
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(text = statistic.label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
