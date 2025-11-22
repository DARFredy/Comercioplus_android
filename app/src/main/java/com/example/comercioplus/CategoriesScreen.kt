package com.example.comercioplus

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.comercioplus.model.Category

@Composable
fun CategoriesScreen(navController: NavController, viewModel: CategoryViewModel) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    CategoriesContent(
        categories = categories,
        isLoading = isLoading,
        error = error,
        onAddCategory = { viewModel.addCategory(it) },
        onDeleteCategory = { id -> viewModel.deleteCategory(id.toString()) },
        onRetry = { viewModel.fetchCategories() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesContent(
    categories: List<Category>,
    isLoading: Boolean,
    error: String?,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (Long) -> Unit,
    onRetry: () -> Unit
) {
    var newCategoryName by remember { mutableStateOf("") }
    val primaryOrange = Color(0xFFE65A00)

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { 
                    Column {
                        Text("ComercioPlus", color = primaryOrange, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                        Text("Gestión de Categorías", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    }
                }
            ) 
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // FORMULARIO DE NUEVA CATEGORÍA
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nueva Categoría", style = MaterialTheme.typography.titleMedium, color = primaryOrange, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newCategoryName,
                                onValueChange = { newCategoryName = it },
                                label = { Text("Nombre de la categoría") },
                                modifier = Modifier.weight(1f),
                                enabled = !isLoading,
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryOrange,
                                    focusedLabelColor = primaryOrange
                                )
                            )
                            Button(
                                onClick = {
                                    if (newCategoryName.isNotBlank()) {
                                        onAddCategory(newCategoryName)
                                        newCategoryName = ""
                                    }
                                },
                                enabled = newCategoryName.isNotBlank() && !isLoading,
                                colors = ButtonDefaults.buttonColors(containerColor = primaryOrange),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.size(56.dp)
                            ) {
                                if (isLoading && newCategoryName.isEmpty()) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = primaryOrange.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                Text("Categorías Disponibles", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(12.dp))

                // ESTADOS DE CARGA Y CONTENIDO
                if (isLoading && categories.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = primaryOrange)
                    }
                } else if (error != null) {
                    ErrorMessage(error, onRetry = onRetry)
                } else if (categories.isEmpty()) {
                    EmptyState()
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(categories) { category ->
                            CategoryCard(
                                name = category.name, 
                                onDelete = { category.id?.let { onDeleteCategory(it) } },
                                isLoading = isLoading,
                                orangeColor = primaryOrange
                            )
                        }
                    }
                }
            }
            
            // Indicador de actualización (cuando ya hay datos)
            if (isLoading && categories.isNotEmpty()) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter), color = primaryOrange)
            }
        }
    }
}

@Composable
fun CategoryCard(name: String, onDelete: () -> Unit, isLoading: Boolean, orangeColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Category, contentDescription = null, tint = orangeColor.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            }
            IconButton(onClick = onDelete, enabled = !isLoading) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
        TextButton(onClick = onRetry) { Text("Reintentar", color = Color(0xFFE65A00)) }
    }
}

@Composable
fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No hay categorías creadas aún.", color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriesScreenPreview() {
    val dummyCategories = listOf(
        Category(id = 1, name = "Accesorios"),
        Category(id = 2, name = "Repuestos"),
        Category(id = 3, name = "Equipamiento")
    )
    CategoriesContent(
        categories = dummyCategories,
        isLoading = false,
        error = null,
        onAddCategory = {},
        onDeleteCategory = {},
        onRetry = {}
    )
}
