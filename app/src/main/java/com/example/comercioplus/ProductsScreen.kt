package com.example.comercioplus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProductsScreen(navController: NavController, productViewModel: ProductViewModel, categoryViewModel: CategoryViewModel) {
    val products by productViewModel.products.collectAsState()

    // Usamos una cuadrícula (Grid) para que se vea como un mercado real
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 2 productos por fila para que sea vistoso
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            // Usamos MarketProductCard que es el componente moderno que creamos
            MarketProductCard(
                product = product,
                onClick = { navController.navigate("product_detail/${product.id}") },
                onEdit = { navController.navigate("edit_product/${product.id}") },
                onDelete = { product.id?.let { id -> productViewModel.deleteProduct(id.toString(), product.store_id) } }
            )
        }
    }
}
