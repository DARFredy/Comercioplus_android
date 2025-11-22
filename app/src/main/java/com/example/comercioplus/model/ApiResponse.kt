package com.example.comercioplus.model

import kotlinx.serialization.Serializable

/**
 * Envoltorio genérico para las respuestas de la API de Laravel.
 */
@Serializable
data class ApiResponse<T>(
    val status: String? = null,
    val message: String? = null,
    val data: T? = null
)
