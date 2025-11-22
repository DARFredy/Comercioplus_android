package com.example.comercioplus.model

import kotlinx.serialization.Serializable

/**
 * Modelo de Categoría sincronizado con los tipos de MySQL de Railway.
 */
@Serializable
data class Category(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val parent_id: Long? = null,
    val slug: String? = null,
    val sales_count: Long = 0,
    val popularity: Int = 0,
    // CAMBIO CRUCIAL: Usamos Int para is_popular porque MySQL envía 0/1, no true/false.
    val is_popular: Int = 0,
    val short_description: String? = null,
    val sort_order: Int? = null,
    val store_id: Long? = null
)
