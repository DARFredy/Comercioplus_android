package com.example.comercioplus.model

import kotlinx.serialization.Serializable

/**
 * DTO definitivo sincronizado con la DB de Railway.
 * Incluye role_id para la relación con la tabla 'roles'.
 */
@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val role: String, // Slug del rol (comerciante/cliente)
    val role_id: Long // ID numérico del rol en la DB
)
