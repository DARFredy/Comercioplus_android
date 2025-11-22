package com.example.comercioplus

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.comercioplus.model.Order
import java.util.Locale

@Composable
fun OrderConfirmationScreen(navController: NavController, orderViewModel: OrderViewModel) {
    val orderStatus by orderViewModel.orderStatus.collectAsState()

    val order = (orderStatus as? OrderStatus.Success)?.order

    if (order == null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se encontró el pedido.")
        }
        return
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {
                Button(
                    onClick = { 
                        orderViewModel.resetOrderStatus()
                        navController.popBackStack(navController.graph.startDestinationId, false)
                     },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Volver al Inicio")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Pedido Confirmado",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("¡Gracias por tu compra!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Tu pedido ha sido confirmado.", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(24.dp))
                Divider()
            }
            item {
                Text(
                    text = "Resumen del Pedido",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            items(order.items) { item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    // CORRECCIÓN: Usamos product_id y price del modelo OrderItemRequest
                    Text("Producto #${item.product_id} (x${item.quantity})")
                    Text("$${String.format(Locale.US, "%.2f", item.price * item.quantity)}")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "$${String.format(Locale.US, "%.2f", order.total)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
