package com.example.comercioplus.model

import kotlinx.serialization.Serializable

/**
 * Modelo para manejar la paginación automática de Laravel y respuestas personalizadas.
 */
@Serializable
data class PaginatedResponse<T>(
    val data: List<T> = emptyList(),
    val stores: List<T>? = null,
    val products: List<T>? = null,
    val current_page: Int? = null,
    val last_page: Int? = null,
    val total: Int? = null
) {
    /**
     * Resuelve la lista de items sin importar si la clave es 'data', 'stores' o 'products'.
     */
    val items: List<T> get() = stores ?: products ?: data
}
