package com.example.comercioplus

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStoreScreen(navController: NavController, storeViewModel: StoreViewModel) {
    val uiState by storeViewModel.uiState.collectAsState()
    val successMessage by storeViewModel.successMessage.collectAsState()
    
    // Clave para forzar el refresco de imágenes de red
    var imageRefreshKey by remember { mutableLongStateOf(0L) }
    
    LaunchedEffect(Unit) {
        storeViewModel.fetchMyStore()
    }

    // Cuando el guardado es exitoso, actualizamos la clave de refresco
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            imageRefreshKey = System.currentTimeMillis()
        }
    }
    
    var name by remember(uiState.storeName) { mutableStateOf(uiState.storeName) }
    var description by remember(uiState.storeDescription) { mutableStateOf(uiState.storeDescription) }

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { storeViewModel.setCoverImagePreview(it) } }
    )

    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { storeViewModel.setLogoImagePreview(it) } }
    )

    // Lógica para decidir qué imagen mostrar y evitar caché vieja
    val finalCoverModel = remember(uiState.coverImagePreview, uiState.coverImage, imageRefreshKey) {
        val base = uiState.coverImagePreview ?: uiState.coverImage
        val uriStr = base.toString()
        if (uriStr.isEmpty()) R.drawable.casco2
        else if (uriStr.startsWith("http") && uiState.coverImagePreview == null) {
            if (uriStr.contains("?")) "$uriStr&t=$imageRefreshKey" else "$uriStr?t=$imageRefreshKey"
        } else base
    }

    val finalLogoModel = remember(uiState.logoImagePreview, uiState.logoImage, imageRefreshKey) {
        val base = uiState.logoImagePreview ?: uiState.logoImage
        val uriStr = base.toString()
        if (uriStr.isEmpty()) R.drawable.casco
        else if (uriStr.startsWith("http") && uiState.logoImagePreview == null) {
            if (uriStr.contains("?")) "$uriStr&t=$imageRefreshKey" else "$uriStr?t=$imageRefreshKey"
        } else base
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Tienda") },
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (successMessage != null) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), modifier = Modifier.fillMaxWidth()) {
                        Text(text = successMessage!!, color = Color(0xFF2E7D32), modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                    }
                }

                // Portada
                Image(
                    painter = rememberAsyncImagePainter(model = finalCoverModel),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                // Logo
                Image(
                    painter = rememberAsyncImagePainter(model = finalLogoModel),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(40.dp)),
                    contentScale = ContentScale.Crop
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la tienda") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción de la tienda") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { coverPickerLauncher.launch("image/*") }, modifier = Modifier.weight(1f), enabled = !uiState.isLoading) {
                        Text("Cambiar Portada")
                    }
                    Button(onClick = { logoPickerLauncher.launch("image/*") }, modifier = Modifier.weight(1f), enabled = !uiState.isLoading) {
                        Text("Cambiar Logo")
                    }
                }

                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!, 
                        color = MaterialTheme.colorScheme.error, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Button(
                onClick = { storeViewModel.updateAndSaveChanges(name, description) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65A00))
            ) {
                if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                else Text("Guardar Cambios")
            }
        }
    }
}
