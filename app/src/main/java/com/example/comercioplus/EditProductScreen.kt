package com.example.comercioplus

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.comercioplus.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    navController: NavController, 
    productViewModel: ProductViewModel, 
    categoryViewModel: CategoryViewModel,
    userViewModel: UserViewModel,
    productId: String?
) {
    LaunchedEffect(productId) {
        if (productId != null) {
            productViewModel.getProductById(productId)
        }
        categoryViewModel.fetchCategories()
    }

    val product by productViewModel.selectedProduct.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val userProfile by userViewModel.userProfile.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val successMessage by productViewModel.successMessage.collectAsState()
    val errorMessage by productViewModel.error.collectAsState()

    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var imageUrlInput by remember { mutableStateOf("") }
    
    var expanded by remember { mutableStateOf(false) }
    
    // ESTADO PARA IMAGEN LOCAL Y CLAVE PARA FORZAR REFRESCO
    var localImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageRefreshKey by remember { mutableLongStateOf(0L) }
    
    // Decidimos qué mostrar: prioridad a la selección local
    val imageToDisplay = localImageUri ?: imageUrlInput

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> localImageUri = uri }
    )

    LaunchedEffect(product) {
        product?.let {
            productName = it.name
            productDescription = it.description ?: ""
            productPrice = it.price.toString()
            selectedCategoryId = it.category_id
            imageUrlInput = it.image_url ?: ""
            localImageUri = null
            imageRefreshKey = System.currentTimeMillis()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (successMessage != null) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), modifier = Modifier.fillMaxWidth()) {
                        Text(text = successMessage!!, color = Color(0xFF2E7D32), modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }

                if (errorMessage != null) {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth()) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }

                OutlinedTextField(value = productName, onValueChange = { productName = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
                OutlinedTextField(value = productDescription, onValueChange = { productDescription = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
                OutlinedTextField(value = productPrice, onValueChange = { productPrice = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth(), enabled = !isLoading)
                
                // Selector de Categoría
                Box(modifier = Modifier.fillMaxWidth()) {
                    val selectedCategoryName = categories.find { it.id == selectedCategoryId }?.name ?: "Seleccionar Categoría"
                    OutlinedTextField(
                        value = selectedCategoryName,
                        onValueChange = {},
                        label = { Text("Categoría") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        enabled = !isLoading,
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown, 
                                "Dropdown", 
                                Modifier.clickable { if (!isLoading) expanded = true }
                            )
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategoryId = category.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Text("Imagen del producto", style = MaterialTheme.typography.titleMedium)
                
                val finalImageModel = remember(imageToDisplay, imageRefreshKey) {
                    val uriStr = imageToDisplay.toString()
                    if (uriStr.isEmpty()) R.drawable.casco2
                    else if (uriStr.startsWith("http")) {
                        if (uriStr.contains("?")) "$uriStr&t=$imageRefreshKey"
                        else "$uriStr?t=$imageRefreshKey"
                    } else imageToDisplay
                }

                Image(
                    painter = rememberAsyncImagePainter(model = finalImageModel),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { imagePickerLauncher.launch("image/*") }, modifier = Modifier.weight(1f), enabled = !isLoading) {
                        Text("Elegir de Galería")
                    }
                    OutlinedButton(onClick = { localImageUri = null; imageUrlInput = "" }, modifier = Modifier.weight(1f), enabled = !isLoading) {
                        Text("Limpiar")
                    }
                }

                OutlinedTextField(
                    value = imageUrlInput,
                    onValueChange = { imageUrlInput = it; localImageUri = null },
                    label = { Text("O usar URL de imagen") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
            }

            Button(
                onClick = {
                    val currentProduct = product
                    if (currentProduct != null) {
                        val updatedProduct = currentProduct.copy(
                            name = productName,
                            description = productDescription,
                            price = productPrice.toDoubleOrNull() ?: 0.0,
                            category_id = selectedCategoryId ?: currentProduct.category_id,
                            image_url = if (localImageUri == null) imageUrlInput else currentProduct.image_url,
                            image = if (localImageUri == null) imageUrlInput else currentProduct.image
                        )
                        
                        if (localImageUri != null) {
                            productViewModel.addProductWithImage(updatedProduct, localImageUri, userProfile)
                        } else {
                            productViewModel.updateProduct(updatedProduct, userProfile.store_id)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && productName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65A00))
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text("Guardar Cambios")
            }
        }
    }
}