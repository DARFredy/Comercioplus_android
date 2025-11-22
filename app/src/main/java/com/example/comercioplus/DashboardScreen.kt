package com.example.comercioplus

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.comercioplus.model.Role
import com.example.comercioplus.model.Store
import com.example.comercioplus.model.Category
import com.example.comercioplus.model.Product
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    navController: NavController, 
    dashboardViewModel: DashboardViewModel,
    userViewModel: UserViewModel,
    storeViewModel: StoreViewModel,
    productViewModel: ProductViewModel,
    categoryViewModel: CategoryViewModel
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    val userProfile by userViewModel.userProfile.collectAsState()
    val storeUiState by storeViewModel.uiState.collectAsState()
    val products by productViewModel.products.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val isLoadingProducts by productViewModel.isLoading.collectAsState()
    
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        storeViewModel.fetchAllStores()
        productViewModel.fetchProducts()
        categoryViewModel.fetchCategories()
    }

    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryOrange = MaterialTheme.colorScheme.primary

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().background(backgroundColor),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            ModernHeader(userProfile.name, primaryOrange)
        }

        item(span = { GridItemSpan(2) }) {
            QuickActions(navController, primaryOrange)
        }

        if (userProfile.role == Role.COMERCIANTE) {
            item(span = { GridItemSpan(2) }) { MerchantKpiSection(uiState, primaryOrange) }
        } else {
            val filteredProducts = if (selectedCategoryId == null) {
                products
            } else {
                products.filter { it.category_id == selectedCategoryId }
            }

            item(span = { GridItemSpan(2) }) {
                CategoryBar(categories, selectedCategoryId) { selectedCategoryId = it }
            }

            item(span = { GridItemSpan(2) }) {
                SectionHeader("${filteredProducts.size} productos encontrados", "Más populares")
            }

            items(filteredProducts) { product ->
                ProductMarketCard(
                    product = product,
                    onClick = { navController.navigate("product_detail/${product.id}") },
                    primaryColor = primaryOrange
                )
            }
            
            if (isLoadingProducts && filteredProducts.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = primaryOrange)
                    }
                }
            }

            item(span = { GridItemSpan(2) }) {
                PaginationFooter(primaryOrange)
            }
        }
    }
}

@Composable
fun ProductMarketCard(
    product: Product,
    onClick: () -> Unit,
    primaryColor: Color
) {
    Card(
        modifier = Modifier.padding(8.dp).fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(product.image_url ?: R.drawable.casco2),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = primaryColor,
                    fontWeight = FontWeight.ExtraBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Ver Detalle", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun QuickActions(navController: NavController, primaryColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionButton(
            label = "Escanear",
            icon = Icons.Default.QrCodeScanner,
            color = primaryColor,
            modifier = Modifier.weight(1f),
            onClick = { navController.navigate("scanner") }
        )
        
        Box(
            modifier = Modifier
                .weight(1.2f)
                .height(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFFA855F7), Color(0xFFEC4899))))
                .clickable { navController.navigate("ia_reports") }
                .padding(1.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(19.dp))
                    .background(Color(0xFF1E1B4B).copy(alpha = 0.8f))
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("IA", color = Color(0xFFEC4899), fontWeight = FontWeight.Black, fontSize = 10.sp)
                        Text("Comercio Plus", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(label: String, icon: ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(80.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun ModernHeader(userName: String, primaryColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.verticalGradient(listOf(primaryColor, Color(0xFFB34600))))
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text("Hola, ${userName.ifEmpty { "Bienvenido" }}", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
            Text("ComercioPlus", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

@Composable
fun CategoryBar(categories: List<Category>, selectedId: Long?, onSelect: (Long?) -> Unit) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 8.dp)) {
        item {
            FilterChip(
                selected = selectedId == null,
                onClick = { onSelect(null) },
                label = { Text("Todo") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
        items(categories) { category ->
            FilterChip(
                selected = category.id == selectedId,
                onClick = { onSelect(category.id) },
                label = { Text(category.name) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
fun StoreWebStyleCard(
    store: Store,
    productCount: Int,
    onViewStore: () -> Unit,
    primaryColor: Color
) {
    Card(
        modifier = Modifier.padding(8.dp).fillMaxWidth().clickable { onViewStore() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(store.coverUrl ?: R.drawable.splashscreen),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Logo circular superpuesto
                Surface(
                    modifier = Modifier.padding(start = 12.dp, top = 60.dp).size(60.dp).border(3.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    shape = CircleShape, color = MaterialTheme.colorScheme.surface, shadowElevation = 4.dp
                ) {
                    Image(painter = rememberAsyncImagePainter(store.logoUrl ?: R.drawable.casco2), contentDescription = null, modifier = Modifier.padding(2.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                    if (store.verified) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp).background(MaterialTheme.colorScheme.surface, CircleShape))
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Spacer(modifier = Modifier.height(25.dp))
                Text(store.name ?: "Tienda", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(store.description ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(14.dp))
                        Text(" ${store.rating ?: "N/A"}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Inventory2, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                        Text(" $productCount productos", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(store.category ?: "General", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                Button(
                    onClick = onViewStore,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("Ver tienda", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Surface(
            shape = RoundedCornerShape(8.dp), 
            color = MaterialTheme.colorScheme.surface, 
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(subtitle, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                Icon(Icons.Default.ArrowDropDown, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun MerchantKpiSection(uiState: DashboardUiState, primaryColor: Color) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            KpiBox("Ingresos", formatCurrency(uiState.totalRevenue), Icons.Default.AttachMoney, Modifier.weight(1f), primaryColor)
            KpiBox("Ventas", uiState.ordersThisMonth.toString(), Icons.Default.ShoppingCart, Modifier.weight(1f), primaryColor)
        }
        KpiBox("Stock Bajo", uiState.lowStockItems.toString(), Icons.Default.Warning, Modifier.fillMaxWidth(), primaryColor)
    }
}

@Composable
fun KpiBox(label: String, value: String, icon: ImageVector, modifier: Modifier, color: Color) {
    Surface(
        modifier = modifier, 
        shape = RoundedCornerShape(12.dp), 
        color = MaterialTheme.colorScheme.surface, 
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun PaginationFooter(color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {}, 
            modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                .size(36.dp)
        ) {
            Icon(Icons.Default.ChevronLeft, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Surface(modifier = Modifier.size(36.dp), shape = RoundedCornerShape(8.dp), color = color) {
            Box(contentAlignment = Alignment.Center) { Text("1", color = Color.White, fontWeight = FontWeight.Bold) }
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = {}, 
            modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                .size(36.dp)
        ) {
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
    }
}

private fun formatCurrency(value: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
        maximumFractionDigits = 0
    }.format(value)
}
