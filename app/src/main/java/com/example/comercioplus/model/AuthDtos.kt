package com.example.comercioplus.model

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) para enviar las credenciales de inicio de sesión.
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Data Transfer Object (DTO) para recibir la respuesta del inicio de sesión.
 * El nombre de la variable (`token`) debe coincidir con la clave en el JSON de la respuesta de tu API.
 */
@Serializable
data class LoginResponse(
    val token: String
)
