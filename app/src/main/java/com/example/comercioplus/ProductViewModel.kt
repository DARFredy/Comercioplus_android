package com.example.comercioplus

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.comercioplus.model.Product
import com.example.comercioplus.model.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _myProducts = MutableStateFlow<List<Product>>(emptyList())
    val myProducts: StateFlow<List<Product>> = _myProducts.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val BASE_DOMAIN = RetrofitInstance.BASE_DOMAIN

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val cb = System.currentTimeMillis().toString()
                // Usamos .items del PaginatedResponse para obtener la lista real de la base de datos
                val response = RetrofitInstance.api.getProducts(cb)
                val productList = response.items // Accede a data, products o stores dinámicamente
                
                val timestamp = System.currentTimeMillis()
                val freshProducts = productList.map { product ->
                    val rawUrl = (product.image_url ?: product.image ?: "").replace("\\/", "/")
                    
                    val absoluteUrl = when {
                        rawUrl.isBlank() -> null
                        rawUrl.startsWith("http") -> rawUrl
                        rawUrl.startsWith("/") -> "$BASE_DOMAIN$rawUrl"
                        else -> "$BASE_DOMAIN/storage/$rawUrl"
                    }
                    
                    product.copy(image_url = absoluteUrl?.let { 
                        if (it.contains("?")) "$it&v=$timestamp" else "$it?v=$timestamp" 
                    })
                }
                
                _products.value = freshProducts
                _selectedProduct.value?.id?.let { id ->
                    _selectedProduct.value = freshProducts.find { it.id == id }
                }
            } catch (e: Exception) {
                _error.value = "Fallo al cargar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchMyProducts(storeId: Long?) {
        if (storeId == null) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val cb = System.currentTimeMillis().toString()
                val response = RetrofitInstance.api.getProducts(cb)
                val filtered = response.items.filter { it.store_id == storeId }.map { product ->
                     val rawUrl = (product.image_url ?: product.image ?: "").replace("\\/", "/")
                     val absoluteUrl = when {
                        rawUrl.isBlank() -> null
                        rawUrl.startsWith("http") -> rawUrl
                        rawUrl.startsWith("/") -> "$BASE_DOMAIN$rawUrl"
                        else -> "$BASE_DOMAIN/storage/$rawUrl"
                    }
                    product.copy(image_url = absoluteUrl)
                }
                _myProducts.value = filtered
            } catch (e: Exception) {
                _error.value = "Error al obtener mis productos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getProductById(productId: String) {
        val id = productId.toLongOrNull()
        _selectedProduct.value = _products.value.find { it.id == id }
    }

    private fun cleanProductForServer(product: Product): Product {
        val currentImage = product.image ?: product.image_url
        val isUrl = currentImage?.startsWith("http") == true
        
        return product.copy(
            image = if (isUrl) null else product.image,
            image_url = if (isUrl) currentImage?.substringBefore("?") else product.image_url
        )
    }

    fun addProductWithImage(product: Product, imageUri: Uri?, currentUserProfile: UserProfile?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null
            
            // Validar que sea un comerciante y tenga tienda
            if (currentUserProfile?.role != Role.COMERCIANTE || currentUserProfile.store_id == null) {
                _error.value = "No tienes permisos para gestionar productos. Crea una tienda primero."
                _isLoading.value = false
                return@launch
            }

            try {
                val storeId = currentUserProfile.store_id
                val productWithStore = product.copy(store_id = storeId)

                if (imageUri != null) {
                    val imagePart = createMultipartBody(imageUri, "image")
                    val uploadResponse = RetrofitInstance.api.uploadProductImage(imagePart)
                    
                    if (uploadResponse.isSuccessful && uploadResponse.body() != null) {
                        val serverUrl = uploadResponse.body()!!.getAnyUrl()
                        val finalProduct = productWithStore.copy(image_url = serverUrl, image = null)
                        
                        if (finalProduct.id != null) {
                            performUpdate(finalProduct, storeId)
                        } else {
                            performAdd(finalProduct, storeId)
                        }
                    } else {
                        val errorBody = uploadResponse.errorBody()?.string()
                        _error.value = "Error al subir imagen (${uploadResponse.code()}): ${parseErrorMessage(errorBody)}"
                        _isLoading.value = false
                    }
                } else {
                    if (productWithStore.id != null) {
                        performUpdate(cleanProductForServer(productWithStore), storeId)
                    } else {
                        performAdd(cleanProductForServer(productWithStore), storeId)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Fallo: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    private suspend fun performAdd(product: Product, storeId: Long) {
        try {
            val response = RetrofitInstance.api.addProduct(product)
            if (response.isSuccessful) {
                _successMessage.value = "¡Producto creado con éxito!"
                fetchProducts()
                fetchMyProducts(storeId)
                delay(2000)
                _successMessage.value = null
            } else {
                _error.value = parseErrorMessage(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            _error.value = "Error al guardar: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun updateProduct(product: Product, storeId: Long?) {
        viewModelScope.launch {
            if (storeId == null || product.store_id != storeId) {
                _error.value = "No autorizado: Este producto no pertenece a tu tienda."
                return@launch
            }
            _isLoading.value = true
            performUpdate(cleanProductForServer(product), storeId)
        }
    }

    private suspend fun performUpdate(product: Product, storeId: Long) {
        try {
            val idStr = product.id?.toString() ?: ""
            val response = RetrofitInstance.api.updateProduct(idStr, product, "PUT")
            if (response.isSuccessful) {
                _successMessage.value = "¡Actualización exitosa!"
                fetchProducts() 
                fetchMyProducts(storeId)
                delay(2000)
                _successMessage.value = null
            } else {
                _error.value = parseErrorMessage(response.errorBody()?.string())
            }
        } catch (e: Exception) {
            _error.value = "Error de red."
        } finally {
            _isLoading.value = false
        }
    }

    fun deleteProduct(productId: String, storeId: Long?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.deleteProduct(productId, "DELETE")
                if (response.isSuccessful) {
                    _successMessage.value = "¡Producto eliminado!"
                    fetchProducts()
                    fetchMyProducts(storeId)
                    delay(2000)
                    _successMessage.value = null
                } else {
                    _error.value = "No se pudo eliminar el producto."
                }
            } catch (e: Exception) {
                _error.value = "Error al borrar."
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseErrorMessage(errorBody: String?): String {
        if (errorBody.isNullOrBlank()) return "Error desconocido"
        return try {
            val json = JSONObject(errorBody)
            if (json.has("errors")) {
                val errors = json.getJSONObject("errors")
                errors.getJSONArray(errors.keys().next()).getString(0)
            } else json.optString("message", "Error de servidor")
        } catch (e: Exception) { 
            if (errorBody.contains("Unauthorized") || errorBody.contains("401")) "No autorizado (Sesión expirada)"
            else "Error de validación" 
        }
    }

    private suspend fun createMultipartBody(uri: Uri, partName: String): MultipartBody.Part =
        withContext(Dispatchers.IO) {
            val context = getApplication<Application>()
            val bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
            val out = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, out)
            val bytes = out.toByteArray()
            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, "${partName}_${System.currentTimeMillis()}.jpg", requestFile)
        }
}