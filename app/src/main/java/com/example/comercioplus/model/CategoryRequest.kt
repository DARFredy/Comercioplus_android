package com.example.comercioplus.model

import kotlinx.serialization.Serializable

/**
 * Modelo para enviar una nueva categoría a Railway.
 * Al no incluir el ID, permitimos que el servidor lo genere automáticamente.
 */
@Serializable
data class CategoryRequest(
    val name: String,
    val description: String = ""
)
