package com.example.comercioplus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comercioplus.model.CartItem
import com.example.comercioplus.model.Order
import com.example.comercioplus.model.OrderItemRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {

    private val _orderStatus = MutableStateFlow<OrderStatus>(OrderStatus.Idle)
    val orderStatus: StateFlow<OrderStatus> = _orderStatus

    /**
     * Envía el pedido real a Railway y sincroniza con las tablas de la base de datos.
     */
    fun placeOrder(cart: List<CartItem>, paymentMethod: String) {
        viewModelScope.launch {
            _orderStatus.value = OrderStatus.Processing
            try {
                // Preparamos los items en el formato exacto que espera el servidor (Long IDs)
                val orderItems = cart.map { 
                    OrderItemRequest(
                        product_id = it.product.id ?: 0L,
                        quantity = it.quantity,
                        price = it.product.price
                    )
                }

                val total = cart.sumOf { it.product.price * it.quantity }

                val orderRequest = Order(
                    total = total,
                    payment_method = paymentMethod,
                    items = orderItems
                )
                
                // LLAMADA SINCRONIZADA CON RAILWAY
                val response = RetrofitInstance.api.createOrder(orderRequest)

                if (response.isSuccessful && response.body()?.data != null) {
                    // ÉXITO: Recibimos la confirmación real del servidor
                    _orderStatus.value = OrderStatus.Success(response.body()!!.data!!)
                } else {
                    _orderStatus.value = OrderStatus.Error("Railway rechazó el pedido. Código: ${response.code()}")
                }
            } catch (e: Exception) {
                _orderStatus.value = OrderStatus.Error("Error de conexión con la nube: ${e.message}")
            }
        }
    }

    fun resetOrderStatus() {
        _orderStatus.value = OrderStatus.Idle
    }
}

sealed class OrderStatus {
    object Idle : OrderStatus()
    object Processing : OrderStatus()
    data class Success(val order: Order) : OrderStatus()
    data class Error(val message: String) : OrderStatus()
}
