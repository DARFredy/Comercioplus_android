package com.example.comercioplus

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Gestor de sesión real.
 */
object SessionManager {

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    // ELIMINADO: Ya no usamos un token de prueba que cause errores 401
    fun saveToken(token: String) {
        _token.value = token
    }

    fun clearToken() {
        _token.value = null
    }
}
