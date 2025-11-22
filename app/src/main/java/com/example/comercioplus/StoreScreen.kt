package com.example.comercioplus

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.comercioplus.model.Product
import com.example.comercioplus.model.Role
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreScreen(
    navController: NavController,
    storeViewModel: StoreViewModel,
    productViewModel: ProductViewModel,
    categoryViewModel: CategoryViewModel,
    userViewModel: UserViewModel,
    dashboardViewModel: DashboardViewModel
) {
    val storeState by storeViewModel.uiState.collectAsState()
    val myProducts by productViewModel.myProducts.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val isLoadingProducts by productViewModel.isLoading.collectAsState()
    val dashboardState by dashboardViewModel.uiState.collectAsState()

    val successMessage by productViewModel.successMessage.collectAsState()
    val storeSuccessMessage by storeViewModel.successMessage.collectAsState()
    val userProfile by userViewModel.userProfile.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Intentamos obtener el ID de la tienda de varias fuentes para evitar el bloqueo
    val effectiveStoreId = storeState.id ?: userProfile.store_id

    LaunchedEffect(Unit) {
        storeViewModel.fetchMyStore()
    }

    LaunchedEffect(effectiveStoreId) {
        if (effectiveStoreId != null) {
            productViewModel.fetchMyProducts(effectiveStoreId)
        }
    }

    val productsByCategory = remember(myProducts) {
        myProducts.groupBy { it.category_id }
    }

    LaunchedEffect(successMessage, storeSuccessMessage) {
        val message = successMessage ?: storeSuccessMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (userProfile.role == Role.COMERCIANTE && effectiveStoreId != null) {
                FloatingActionButton(onClick = { navController.navigate("add_product") }, containerColor = Color(0xFFE65A00)) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
                }
            }
        }
    ) { padding ->
        // Si está cargando y no tenemos nada, mostramos el indicador
        if (storeState.isLoading && storeState.id == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFE65A00))
            }
        } 
        // Si NO está cargando y REALMENTE no hay ID en ningún lado (ni perfil ni respuesta API)
        else if (effectiveStoreId == null && !storeState.isLoading) {
            NoStoreView(onRegisterClick = { navController.navigate("edit_store") })
        } 
        // En cualquier otro caso, mostramos la tienda (incluso con datos parciales del perfil)
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    StoreHeader(
                        storeName = storeState.storeName,
                        storeDescription = storeState.storeDescription,
                        coverImage = storeState.coverImage,
                        logoImage = storeState.logoImage,
                        onRefresh = { 
                            storeViewModel.fetchMyStore()
                        },
                        onEdit = { navController.navigate("edit_store") }
                    )
                }

                if (storeState.error != null && effectiveStoreId == null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text(
                                text = storeState.error!!,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item {
                    QuickStatsBar(dashboardState)
                }

                if (myProducts.isEmpty() && !isLoadingProducts) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay productos aún en tu tienda.", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                } else {
                    categories.forEach { category ->
                        val productsInCategory = productsByCategory[category.id] ?: emptyList()
                        if (productsInCategory.isNotEmpty()) {
                            item {
                                CategoryCarousel(
                                    categoryName = category.name,
                                    products = productsInCategory,
                                    onProductClick = { navController.navigate("product_detail/${it.id}") },
                                    onEditClick = { navController.navigate("edit_product/${it.id}") },
                                    onDeleteClick = { it.id?.let { id -> productViewModel.deleteProduct(id.toString(), effectiveStoreId) } }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoStoreView(onRegisterClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Storefront,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Aún no tienes una tienda configurada",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Para comenzar a vender tus productos, necesitas registrar la información de tu negocio.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRegisterClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65A00)),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Registrar mi Tienda", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuickStatsBar(stats: DashboardUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Productos", value = stats.totalProducts.toString())
            StatItem(label = "Bajo Stock", value = stats.lowStockItems.toString(), color = Color.Red)
            StatItem(label = "Ingresos", value = "$${stats.totalRevenue.toInt()}")
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color = Color.Unspecified) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun CategoryCarousel(
    categoryName: String,
    products: List<Product>,
    onProductClick: (Product) -> Unit,
    onEditClick: (Product) -> Unit,
    onDeleteClick: (Product) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = categoryName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                MarketProductCard(
                    product = product,
                    onClick = { onProductClick(product) },
                    onEdit = { onEditClick(product) },
                    onDelete = { onDeleteClick(product) }
                )
            }
        }
    }
}

@Composable
fun MarketProductCard(
    product: Product,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val BASE_DOMAIN = RetrofitInstance.BASE_DOMAIN

    val imageUrl = remember(product.image_url, product.image) {
        val raw = product.image_url ?: product.image ?: ""
        if (raw.isBlank()) null
        else if (raw.startsWith("http")) raw
        else if (raw.startsWith("/")) "$BASE_DOMAIN$raw"
        else "$BASE_DOMAIN/storage/$raw"
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl ?: R.drawable.casco2),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = String.format(Locale.US, "$%.2f", product.price),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFE65A00),
                    fontWeight = FontWeight.ExtraBold
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(0.6f), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StoreHeader(
    storeName: String,
    storeDescription: String,
    coverImage: Uri?,
    logoImage: Uri?,
    onRefresh: () -> Unit,
    onEdit: () -> Unit
) {
    val timestamp = remember { System.currentTimeMillis() }
    val coverModel = remember(coverImage) { coverImage?.let { "$it?t=$timestamp" } ?: R.drawable.casco2 }
    val logoModel = remember(logoImage) { logoImage?.let { "$it?t=$timestamp" } ?: R.drawable.casco }

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)) {
        Image(
            painter = rememberAsyncImagePainter(model = coverModel),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f))))
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
            }
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Refrescar", tint = Color.White)
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = logoModel),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(storeName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(storeDescription, color = Color.White.copy(0.8f), fontSize = 14.sp, maxLines = 1)
            }
        }
    }
}