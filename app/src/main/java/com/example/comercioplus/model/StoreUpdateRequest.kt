package com.example.comercioplus.model

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) para enviar las actualizaciones de la tienda a la API.
 * Contiene solo los campos que el backend espera recibir.
 */
@Serializable
data class StoreUpdateRequest(
    val name: String,
    val description: String
)
