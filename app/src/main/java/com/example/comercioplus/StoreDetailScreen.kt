package com.example.comercioplus

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(
    navController: NavController,
    storeViewModel: StoreViewModel,
    productViewModel: ProductViewModel,
    storeId: Long
) {
    val storeUiState by storeViewModel.uiState.collectAsState()
    val products by productViewModel.products.collectAsState()
    val isLoadingProducts by productViewModel.isLoading.collectAsState()
    val selectedStore = storeUiState.selectedStore

    val storeProducts = remember(products, storeId) {
        products.filter { it.store_id == storeId }
    }

    var showRatingDialog by remember { mutableStateOf(false) }
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }

    LaunchedEffect(storeId) {
        storeViewModel.fetchStoreById(storeId)
        productViewModel.fetchProducts()
    }

    val primaryOrange = Color(0xFFE65A00)
    val backgroundColor = Color(0xFF0F172A)
    val surfaceColor = Color(0xFF1E293B)
    val textColor = Color(0xFFE2E8F0)
    val secondaryTextColor = Color(0xFF94A3B8)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedStore?.name ?: "Tienda", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showRatingDialog = true }) {
                        Icon(Icons.Default.Star, contentDescription = "Calificar", tint = Color(0xFFFFB300))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // HEADER SECCION (Banner y Logo)
            item(span = { GridItemSpan(2) }) {
                Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedStore?.coverUrl ?: R.drawable.splashscreen),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Gradiente sobre el banner
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(
                        Brush.verticalGradient(listOf(Color.Transparent, backgroundColor.copy(alpha = 0.7f)))
                    ))

                    // Logo Circular Flotante
                    Surface(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp).size(100.dp),
                        shape = CircleShape,
                        color = surfaceColor,
                        shadowElevation = 8.dp,
                        border = androidx.compose.foundation.BorderStroke(4.dp, backgroundColor)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedStore?.logoUrl ?: R.drawable.casco2),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(4.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // INFORMACIÓN DE LA TIENDA
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            selectedStore?.name ?: "Cargando...",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        if (selectedStore?.verified == true) {
                            Spacer(Modifier.width(6.dp))
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF3B82F6), modifier = Modifier.size(20.dp))
                        }
                    }
                    
                    Text(
                        selectedStore?.category ?: "Repuestos y Accesorios",
                        style = MaterialTheme.typography.labelLarge,
                        color = primaryOrange,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        selectedStore?.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = secondaryTextColor,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // STATS ROW
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StoreStatItem("Productos", (selectedStore?.productsCount ?: storeProducts.size).toString(), textColor)
                        Divider(modifier = Modifier.height(30.dp).width(1.dp), color = surfaceColor)
                        StoreStatItem("Calificación", "★ ${selectedStore?.rating ?: "4.5"}", textColor)
                        Divider(modifier = Modifier.height(30.dp).width(1.dp), color = surfaceColor)
                        StoreStatItem("Seguidores", "1.2k", textColor)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        "Catálogo de Productos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // LISTADO DE PRODUCTOS
            if (isLoadingProducts && storeProducts.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = primaryOrange)
                    }
                }
            } else if (storeProducts.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Text(
                        "No hay productos disponibles.",
                        color = secondaryTextColor,
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(storeProducts) { product ->
                    UserMarketCard(
                        product = product,
                        onClick = { navController.navigate("product_detail/${product.id}") }
                    )
                }
            }
        }

        if (showRatingDialog) {
            AlertDialog(
                onDismissRequest = { showRatingDialog = false },
                containerColor = surfaceColor,
                titleContentColor = Color.White,
                textContentColor = textColor,
                title = { Text("Calificar Tienda", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            repeat(5) { index ->
                                IconButton(onClick = { rating = index + 1 }) {
                                    Icon(
                                        imageVector = if (index < rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                                        contentDescription = null,
                                        tint = if (index < rating) Color(0xFFFFB300) else secondaryTextColor,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            label = { Text("Tu comentario") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = textColor,
                                focusedBorderColor = primaryOrange,
                                unfocusedBorderColor = secondaryTextColor
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            storeViewModel.rateStore(storeId, rating, comment)
                            showRatingDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryOrange)
                    ) { Text("Enviar") }
                },
                dismissButton = {
                    TextButton(onClick = { showRatingDialog = false }) { Text("Cancelar", color = secondaryTextColor) }
                }
            )
        }
    }
}

@Composable
fun StoreStatItem(label: String, value: String, textColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.White)
        Text(label, fontSize = 11.sp, color = Color.Gray)
    }
}
