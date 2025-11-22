package com.example.comercioplus

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.comercioplus.model.Role
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    userViewModel: UserViewModel,
    productId: String?
) {
    val userProfile by userViewModel.userProfile.collectAsState()
    
    LaunchedEffect(productId) {
        if (productId != null) {
            productViewModel.getProductById(productId)
        } else {
            navController.popBackStack()
        }
    }

    val product by productViewModel.selectedProduct.collectAsState()
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var showRatingDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (userProfile.role == Role.COMERCIANTE) {
                        IconButton(onClick = {
                            if (productId != null) {
                                navController.navigate("edit_product/$productId")
                            }
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Producto")
                        }
                    }
                }
            )
        },
        bottomBar = {
            product?.let {
                BottomAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Button(
                        onClick = { cartViewModel.addToCart(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65A00))
                    ) {
                        Text("Agregar al Carrito", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            product?.let { currentProduct ->
                item {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = currentProduct.image_url?.takeIf { it.isNotEmpty() } ?: R.drawable.casco2
                        ),
                        contentDescription = currentProduct.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    )
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentProduct.name,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            if (userProfile.role == Role.CLIENTE) {
                                IconButton(onClick = { showRatingDialog = true }) {
                                    Icon(Icons.Default.Star, contentDescription = "Calificar", tint = Color(0xFFFFB300))
                                }
                            }
                        }
                        
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", currentProduct.price)}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65A00)
                        )
                        Divider()
                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentProduct.description ?: "Sin descripción disponible.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
        
        if (showRatingDialog) {
            AlertDialog(
                onDismissRequest = { showRatingDialog = false },
                title = { Text("Calificar Producto") },
                text = {
                    Column {
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            repeat(5) { index ->
                                IconButton(onClick = { rating = index + 1 }) {
                                    Icon(
                                        imageVector = if (index < rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB300)
                                    )
                                }
                            }
                        }
                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            label = { Text("Tu comentario (opcional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        // Aquí iría la llamada al ViewModel para calificar
                        showRatingDialog = false
                    }) { Text("Enviar") }
                },
                dismissButton = {
                    TextButton(onClick = { showRatingDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}
