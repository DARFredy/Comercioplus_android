package com.example.comercioplus

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(navController: NavController, cartViewModel: CartViewModel) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val total = cartItems.sumOf { it.product.price * it.quantity }
    
    var selectedPaymentMethod by remember { mutableStateOf("nequi") }
    var orderPlaced by remember { mutableStateOf(false) }

    // Campos del formulario
    var phoneNumber by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var cardName by remember { mutableStateOf("") }

    val primaryOrange = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background

    if (orderPlaced) {
        SuccessDialog(onConfirm = {
            cartViewModel.clearCart() // LIMPIA EL CARRITO AL FINALIZAR
            navController.navigate("user_main") {
                popUpTo(0) { inclusive = true }
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finalizar Compra", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // RESUMEN DE PRODUCTOS
                item {
                    Text(
                        "Resumen de pedido", 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                items(cartItems) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.quantity}x ${item.product.name}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "$${String.format(Locale.US, "%.0f", item.product.price * item.quantity)}", 
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant) }

                // MÉTODOS DE PAGO
                item {
                    Text(
                        "Método de pago", 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(12.dp))
                }

                item {
                    PaymentMethodItem(
                        id = "nequi",
                        name = "Nequi",
                        subtitle = "Transferencia directa",
                        icon = Icons.Default.AccountBalanceWallet,
                        isSelected = selectedPaymentMethod == "nequi",
                        onSelect = { selectedPaymentMethod = "nequi" }
                    )
                }

                // FORMULARIO DINÁMICO SEGÚN EL MÉTODO SELECCIONADO
                if (selectedPaymentMethod == "nequi") {
                    item {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Número de celular Nequi") },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                item {
                    PaymentMethodItem(
                        id = "card",
                        name = "Tarjeta de Crédito/Débito",
                        subtitle = "Visa, Mastercard, AMEX",
                        icon = Icons.Default.CreditCard,
                        isSelected = selectedPaymentMethod == "card",
                        onSelect = { selectedPaymentMethod = "card" }
                    )
                }

                if (selectedPaymentMethod == "card") {
                    item {
                        Column(modifier = Modifier.padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { if (it.length <= 16) cardNumber = it },
                                label = { Text("Número de tarjeta") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = cardName,
                                onValueChange = { cardName = it },
                                label = { Text("Nombre en la tarjeta") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                item {
                    PaymentMethodItem(
                        id = "efecty",
                        name = "Efecty",
                        subtitle = "Pago en punto físico",
                        icon = Icons.Default.Payments,
                        isSelected = selectedPaymentMethod == "efecty",
                        onSelect = { selectedPaymentMethod = "efecty" }
                    )
                }

                item {
                    PaymentMethodItem(
                        id = "qr",
                        name = "QR Bancolombia",
                        subtitle = "Escanea y paga",
                        icon = Icons.Default.QrCode2,
                        isSelected = selectedPaymentMethod == "qr",
                        onSelect = { selectedPaymentMethod = "qr" }
                    )
                }
            }

            // BOTÓN DE PAGO FIJO ABAJO
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total a pagar", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "$${String.format(Locale.US, "%.0f", total)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = primaryOrange
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    
                    val isFormValid = when(selectedPaymentMethod) {
                        "nequi" -> phoneNumber.length >= 10
                        "card" -> cardNumber.length >= 15 && cardName.isNotBlank()
                        else -> true
                    }

                    Button(
                        onClick = { orderPlaced = true },
                        enabled = isFormValid && cartItems.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryOrange)
                    ) {
                        Text("Confirmar y Pagar", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodItem(
    id: String,
    name: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
fun SuccessDialog(onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Entendido", color = Color.White)
            }
        },
        icon = {
            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF10B981), modifier = Modifier.size(64.dp))
        },
        title = { Text("¡Pedido Exitoso!", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Text(
                "Tu compra ha sido procesada correctamente. El comercio se pondrá en contacto contigo pronto.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
