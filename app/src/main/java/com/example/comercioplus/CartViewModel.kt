package com.example.comercioplus

import androidx.lifecycle.ViewModel
import com.example.comercioplus.model.CartItem
import com.example.comercioplus.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    /**
     * Añade un producto al carrito comparando los IDs numéricos de Railway.
     */
    fun addToCart(product: Product) {
        val currentCart = _cartItems.value.toMutableList()
        // Sincronización: Comparamos usando el ID Long? de Railway
        val existingItem = currentCart.find { it.product.id == product.id }

        if (existingItem != null) {
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
            val itemIndex = currentCart.indexOf(existingItem)
            currentCart[itemIndex] = updatedItem
        } else {
            currentCart.add(CartItem(product = product, quantity = 1))
        }
        _cartItems.value = currentCart
    }

    /**
     * Quita un producto del carrito comparando los IDs numéricos de Railway.
     */
    fun removeFromCart(product: Product) {
        val currentCart = _cartItems.value.toMutableList()
        val existingItem = currentCart.find { it.product.id == product.id }

        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                val updatedItem = existingItem.copy(quantity = existingItem.quantity - 1)
                val itemIndex = currentCart.indexOf(existingItem)
                currentCart[itemIndex] = updatedItem
            } else {
                currentCart.remove(existingItem)
            }
        }
        _cartItems.value = currentCart
    }

    /**
     * Limpia completamente el carrito después de una compra exitosa.
     */
    fun clearCart() {
        _cartItems.value = emptyList()
    }
}
