package com.example.comercioplus

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.comercioplus.model.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

data class StoreUiState(
    val id: Long? = null,
    val storeName: String = "Mi Tienda",
    val storeDescription: String = "Los mejores repuestos para tu moto",
    val coverImage: Uri? = null,
    val logoImage: Uri? = null,
    val coverImagePreview: Uri? = null,
    val logoImagePreview: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val allStores: List<Store> = emptyList(),
    val selectedStore: Store? = null
)

class StoreViewModel(app: Application) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(StoreUiState())
    val uiState = _uiState.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val BASE_DOMAIN = RetrofitInstance.BASE_DOMAIN

    private fun formatImageUrl(url: String?): Uri? {
        if (url.isNullOrBlank() || url == "null") return null
        val cleanUrl = url.replace("\\/", "/")
        val absoluteUrl = when {
            cleanUrl.startsWith("http") -> cleanUrl
            cleanUrl.startsWith("/") -> "$BASE_DOMAIN$cleanUrl"
            else -> "$BASE_DOMAIN/storage/$cleanUrl"
        }
        return Uri.parse(absoluteUrl)
    }

    fun fetchMyStore() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val cb = "v${System.currentTimeMillis()}"
                val response = RetrofitInstance.api.getMyStore(cb)

                if (response.isSuccessful) {
                    val storeData = response.body()
                    if (storeData != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            id = storeData.id,
                            storeName = storeData.name?.takeIf { it.isNotBlank() && it != "null" } ?: "Mi Tienda",
                            storeDescription = storeData.description?.takeIf { it.isNotBlank() && it != "null" } ?: "Los mejores repuestos para tu moto",
                            coverImage = formatImageUrl(storeData.coverUrl),
                            logoImage = formatImageUrl(storeData.logoUrl),
                            coverImagePreview = null,
                            logoImagePreview = null,
                            error = null
                        )
                    }
                } else {
                    val errorMsg = if (response.code() == 404) null else "Error ${response.code()}"
                    _uiState.value = _uiState.value.copy(isLoading = false, error = errorMsg, id = null)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun fetchAllStores() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val cb = System.currentTimeMillis().toString()
                val response = RetrofitInstance.api.getStores(cb)
                
                // Procesamos las tiendas para asegurar que las URLs sean absolutas
                val stores = response.items.map { store ->
                    store.copy(
                        logoUrl = formatImageUrl(store.logoUrl).toString(),
                        coverUrl = formatImageUrl(store.coverUrl).toString()
                    )
                }
                
                _uiState.value = _uiState.value.copy(
                    allStores = stores, 
                    isLoading = false, 
                    error = if (stores.isEmpty()) "No se encontraron tiendas" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, 
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun fetchStoreById(id: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val cb = System.currentTimeMillis().toString()
                val response = RetrofitInstance.api.getStoreById(id, cb)
                if (response.isSuccessful) {
                    val store = response.body()
                    _uiState.value = _uiState.value.copy(
                        selectedStore = store?.copy(
                            coverUrl = formatImageUrl(store.coverUrl).toString(),
                            logoUrl = formatImageUrl(store.logoUrl).toString()
                        ), 
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Error ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun rateStore(storeId: Long, rating: Int, comment: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.rateStore(storeId, RatingRequest(rating, comment))
                if (response.isSuccessful) {
                    _successMessage.value = "¡Gracias por calificar la tienda!"
                    delay(2000)
                    _successMessage.value = null
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Error al calificar")
            }
        }
    }

    fun updateAndSaveChanges(name: String, description: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                var currentId = _uiState.value.id
                if (currentId == null) {
                    val checkResponse = RetrofitInstance.api.getMyStore("v${System.currentTimeMillis()}")
                    if (checkResponse.isSuccessful) {
                        currentId = checkResponse.body()?.id
                    }
                }

                val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val descBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val coverPart = _uiState.value.coverImagePreview?.let { createMultipartBody(it, "cover") }
                val logoPart = _uiState.value.logoImagePreview?.let { createMultipartBody(it, "logo") }

                val response = if (currentId == null) {
                    RetrofitInstance.api.createStore(nameBody, descBody, coverPart, logoPart)
                } else {
                    val methodBody = "PUT".toRequestBody("text/plain".toMediaTypeOrNull())
                    RetrofitInstance.api.updateStoreWithImages(currentId, methodBody, nameBody, descBody, coverPart, logoPart)
                }

                if (response.isSuccessful) {
                    val updatedStore = response.body()
                    val newId = updatedStore?.id ?: currentId
                    
                    _uiState.value = _uiState.value.copy(
                        id = newId,
                        coverImagePreview = null,
                        logoImagePreview = null,
                        isLoading = false
                    )
                    
                    _successMessage.value = "¡Tienda guardada!"
                    delay(1500)
                    fetchMyStore()
                    delay(500)
                    _successMessage.value = null
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Error servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    private suspend fun createMultipartBody(uri: Uri, partName: String): MultipartBody.Part =
        withContext(Dispatchers.IO) {
            val context = getApplication<Application>()
            val bitmap = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
            val out = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, out)
            val bytes = out.toByteArray()
            val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, "${partName}_${System.currentTimeMillis()}.jpg", requestFile)
        }

    fun setCoverImagePreview(uri: Uri) { _uiState.value = _uiState.value.copy(coverImagePreview = uri) }
    fun setLogoImagePreview(uri: Uri) { _uiState.value = _uiState.value.copy(logoImagePreview = uri) }
}
