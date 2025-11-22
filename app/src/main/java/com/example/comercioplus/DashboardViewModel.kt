package com.example.comercioplus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comercioplus.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

// Estado sincronizado con datos reales
data class DashboardUiState(
    val totalRevenue: Double = 0.0,
    val ordersThisMonth: Int = 0,
    val newCustomers: Int = 0,
    val lowStockItems: Int = 0,
    val totalProducts: Int = 0
)

class DashboardViewModel(
    private val productViewModel: ProductViewModel,
    private val userViewModel: UserViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Combinamos el perfil de usuario y los productos para filtrar correctamente
        combine(
            productViewModel.products,
            userViewModel.userProfile
        ) { products, profile ->
            val filteredProducts = if (profile.role == Role.COMERCIANTE && profile.store_id != null) {
                products.filter { it.store_id == profile.store_id }
            } else {
                products
            }
            calculateStats(filteredProducts)
        }.launchIn(viewModelScope)
    }

    private fun calculateStats(products: List<com.example.comercioplus.model.Product>) {
        // Calculamos cuántos productos tienen stock bajo (menos de 5)
        val lowStockCount = products.count { it.stock < 5 }
        
        // Actualizamos el estado con datos REALES filtrados
        _uiState.value = _uiState.value.copy(
            lowStockItems = lowStockCount,
            totalProducts = products.size,
            // Mantener estos como simulados hasta tener el endpoint de pedidos real filtrado
            totalRevenue = if (products.isEmpty()) 0.0 else 2345.67,
            ordersThisMonth = if (products.isEmpty()) 0 else 102,
            newCustomers = if (products.isEmpty()) 0 else 25
        )
    }
}
