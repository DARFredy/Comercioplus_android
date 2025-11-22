package com.example.comercioplus.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Modelo de Producto sincronizado con Railway y Laravel.
 * Ajustado para aceptar estados en formato texto (ej: "active").
 */
@Serializable
data class Product(
    val id: Long? = null,
    val name: String,
    val slug: String = "",
    val description: String? = null,
    val image_path: String? = null,
    val image_url: String? = null,
    val price: Double,
    val stock: Int = 0,
    val image: String? = null,
    val category_id: Long,
    val sku: String? = null,
    val offer: Int = 0,
    val average_rating: Double = 0.0,
    val user_id: Long? = null,
    val store_id: Long? = null,
    // CORRECCIÓN: Cambiado a String para aceptar valores como "active" o "inactive" de la DB
    @SerialName("status") val product_status: String? = "active",
    val is_on_promotion: Int = 0,
    val is_promo: Int = 0,
    val promo_price: Double? = null
)
