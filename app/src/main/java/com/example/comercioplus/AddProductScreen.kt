package com.example.comercioplus

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.comercioplus.model.Category
import com.example.comercioplus.model.Product
import com.example.comercioplus.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    categoryViewModel: CategoryViewModel,
    userViewModel: UserViewModel,
    storeViewModel: StoreViewModel? = null, // Añadido opcional para backup de ID
    initialSku: String = ""
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("10") }
    var sku by remember { mutableStateOf(initialSku) }
    
    val categories by categoryViewModel.categories.collectAsState()
    val userProfile by userViewModel.userProfile.collectAsState()

    // Corregido: Se usa MutableStateFlow como fallback para evitar el error de asStateFlow en mutableStateOf
    val storeState by (storeViewModel?.uiState ?: remember { MutableStateFlow(StoreUiState()) }).collectAsState()
    
    // El ID de la tienda puede venir del perfil o del estado de la tienda recién creada
    val effectiveStoreId = userProfile.store_id ?: storeState.id

    var categoryInput by remember { mutableStateOf("") }
    var isSuggestionsExpanded by remember { mutableStateOf(false) }
    val filteredCategories = categories.filter { it.name.contains(categoryInput, ignoreCase = true) }

    val isLoadingProduct by productViewModel.isLoading.collectAsState()
    val isLoadingCategory by categoryViewModel.isLoading.collectAsState()
    val isLoading = isLoadingProduct || isLoadingCategory
    
    val successMessage by productViewModel.successMessage.collectAsState()
    val errorMessage by productViewModel.error.collectAsState()

    var localImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> localImageUri = uri }
    )

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            name = ""
            description = ""
            price = ""
            stock = "10"
            sku = ""
            categoryInput = ""
            localImageUri = null
        }
    }

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Agregar Producto") }, 
                navigationIcon = { 
                    IconButton(onClick = { navController.popBackStack() }) { 
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") 
                    } 
                } 
            ) 
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                
                if (successMessage != null) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), modifier = Modifier.fillMaxWidth()) {
                        Text(text = successMessage!!, color = Color(0xFF2E7D32), modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                    }
                }
                
                if (errorMessage != null) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), modifier = Modifier.fillMaxWidth()) {
                        Text(text = errorMessage!!, color = Color(0xFFC62828), modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                    }
                }

                // AVISO SI NO HAY TIENDA DETECTADA
                if (effectiveStoreId == null && userProfile.role == Role.COMERCIANTE) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Aviso: No se detecta el ID de tu tienda. Asegúrate de haberla guardado en la sección 'Tienda'.",
                            color = Color(0xFFE65100),
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del Producto") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
                
                OutlinedTextField(
                    value = sku, 
                    onValueChange = { sku = it }, 
                    label = { Text("Código de Barras (SKU)") }, 
                    modifier = Modifier.fillMaxWidth(), 
                    enabled = !isLoading,
                    trailingIcon = {
                        IconButton(onClick = { navController.navigate("scanner") }) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear", tint = Color(0xFFE65A00))
                        }
                    }
                )

                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), enabled = !isLoading)
                    OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f), enabled = !isLoading)
                }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = categoryInput,
                        onValueChange = { 
                            categoryInput = it
                            isSuggestionsExpanded = it.isNotEmpty()
                        },
                        label = { Text("Categoría (nueva o existente)") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                    
                    if (isSuggestionsExpanded && filteredCategories.isNotEmpty()) {
                        DropdownMenu(
                            expanded = isSuggestionsExpanded,
                            onDismissRequest = { isSuggestionsExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            filteredCategories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        categoryInput = category.name
                                        isSuggestionsExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Button(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) { 
                    Text(if (localImageUri == null) "Seleccionar Imagen" else "Cambiar Imagen") 
                }

                if (localImageUri != null) {
                    Image(painter = rememberAsyncImagePainter(model = localImageUri), contentDescription = null, modifier = Modifier.height(150.dp).fillMaxWidth().clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                }
            }

            Button(
                onClick = {
                    val existingCategory = categories.find { it.name.equals(categoryInput, ignoreCase = true) }
                    
                    val uniqueSlug = name.lowercase()
                        .replace(" ", "-")
                        .replace(Regex("[^a-z0-9-]"), "") + "-" + Random.nextInt(1000, 9999)

                    // Usamos el perfil actualizado o el ID de respaldo
                    val updatedProfileWithStore = if (userProfile.store_id == null && effectiveStoreId != null) {
                        userProfile.copy(store_id = effectiveStoreId)
                    } else {
                        userProfile
                    }

                    if (existingCategory != null) {
                        val product = Product(
                            id = null, 
                            name = name, 
                            slug = uniqueSlug, 
                            description = description,
                            price = price.toDoubleOrNull() ?: 0.0, 
                            stock = stock.toIntOrNull() ?: 0,
                            category_id = existingCategory.id!!, 
                            image_url = null,
                            sku = sku,
                            store_id = effectiveStoreId // MANDAMOS EL ID AQUÍ
                        )
                        productViewModel.addProductWithImage(product, localImageUri, updatedProfileWithStore)
                    } else {
                        // Si la categoría no existe, primero la creamos
                        categoryViewModel.addCategory(categoryInput)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && price.isNotBlank() && categoryInput.isNotBlank() && !isLoading && effectiveStoreId != null
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text("Guardar Producto")
            }
        }
    }
    
    // Si se acaba de crear una categoría, esperamos a que aparezca para guardar el producto
    LaunchedEffect(categories) {
        if (categoryInput.isNotBlank()) {
            val newCat = categories.find { it.name.equals(categoryInput, ignoreCase = true) }
            if (newCat != null && !isLoading && name.isNotBlank()) {
                 // Aquí podrías disparar el guardado automático si lo deseas
            }
        }
    }
}
