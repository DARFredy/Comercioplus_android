package com.example.comercioplus.model

import kotlinx.serialization.Serializable

/**
 * Modelo de Pedido sincronizado con el dump SQL de Railway.
 */
@Serializable
data class Order(
    val id: Long? = null,
    val user_id: Long? = null,
    val store_id: Long? = null,
    val total: Double,
    val date: String? = null,
    val payment_method: String,
    val status: String = "pending",
    val items: List<OrderItemRequest> = emptyList()
)

/**
 * Modelo para enviar los productos del pedido a la tabla 'order_items'.
 */
@Serializable
data class OrderItemRequest(
    val product_id: Long,
    val quantity: Int,
    val price: Double
)
