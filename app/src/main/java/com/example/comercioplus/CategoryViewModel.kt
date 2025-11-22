package com.example.comercioplus

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comercioplus.model.Category
import com.example.comercioplus.model.CategoryRequest
import com.example.comercioplus.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CategoryViewModel(private val productViewModel: ProductViewModel) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _productCounts = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val productCounts: StateFlow<Map<Long, Int>> = _productCounts.asStateFlow()

    init {
        fetchCategories()

        productViewModel.products.onEach { products ->
            updateProductCounts(products)
        }.launchIn(viewModelScope)
    }

    /**
     * Carga las categorías sincronizadas con la respuesta real de Railway (List).
     */
    fun fetchCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // CORRECCIÓN: Leemos directamente la lista para evitar el error de JSON
                val response = RetrofitInstance.api.getCategories()
                _categories.value = response
                updateProductCounts(productViewModel.products.value)
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Error fetching categories", e)
                _error.value = "Error al cargar categorías: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateProductCounts(products: List<Product>) {
        val counts = products.groupingBy { it.category_id }.eachCount()
        _productCounts.value = counts
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val request = CategoryRequest(name = name)
                val response = RetrofitInstance.api.addCategory(request)

                if (response.isSuccessful) {
                    fetchCategories()
                } else {
                    _error.value = "Error al crear categoría."
                }
            } catch (e: Exception) {
                _error.value = "Fallo de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitInstance.api.deleteCategory(categoryId, "DELETE")
                if (response.isSuccessful) {
                    fetchCategories()
                } else {
                    _error.value = "No se pudo eliminar."
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCategory(updatedCategory: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val idStr = updatedCategory.id.toString()
                val response = RetrofitInstance.api.updateCategory(idStr, updatedCategory, "PUT")
                if (response.isSuccessful) {
                    fetchCategories()
                } else {
                    _error.value = "No se pudo actualizar."
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
