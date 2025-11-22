package com.example.comercioplus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comercioplus.model.LoginRequest
import com.example.comercioplus.model.RegisterRequest
import com.example.comercioplus.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false
)

class AuthViewModel(private val userViewModel: UserViewModel) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, role: Role) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val response = RetrofitInstance.api.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        SessionManager.saveToken(loginResponse.token)
                        // IMPORTANTE: Al hacer login con éxito, pedimos el perfil real
                        // para que el UserViewModel tenga los datos correctos del store_id, etc.
                        userViewModel.fetchUserProfile()
                        userViewModel.setRole(role)
                        _uiState.value = AuthUiState(isLoginSuccessful = true)
                    } else {
                        _uiState.value = AuthUiState(error = "Respuesta vacía del servidor")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val msg = try { JSONObject(errorBody ?: "").getString("message") } catch(e: Exception) { "Credenciales incorrectas" }
                    _uiState.value = AuthUiState(error = msg)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = "Error de red: ${e.message}")
            }
        }
    }

    /**
     * Inicia sesión utilizando un token de Google validado.
     */
    fun loginWithGoogle(idToken: String, role: Role) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val response = RetrofitInstance.api.googleLogin(mapOf("idToken" to idToken))
                if (response.isSuccessful && response.body() != null) {
                    SessionManager.saveToken(response.body()!!.token)
                    userViewModel.fetchUserProfile()
                    userViewModel.setRole(role)
                    _uiState.value = AuthUiState(isLoginSuccessful = true)
                } else {
                    _uiState.value = AuthUiState(error = "Error al autenticar con Google")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = "Fallo de conexión con Google: ${e.message}")
            }
        }
    }

    fun register(name: String, email: String, password: String, confirm: String, role: Role) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val dbRoleValue = if (role == Role.COMERCIANTE) "merchant" else "client"
                val request = RegisterRequest(
                    name = name,
                    email = email,
                    password = password,
                    password_confirmation = confirm,
                    role = dbRoleValue,
                    role_id = if (role == Role.COMERCIANTE) 2 else 3
                )
                
                val response = RetrofitInstance.api.register(request)
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        SessionManager.saveToken(loginResponse.token)
                        userViewModel.fetchUserProfile()
                        userViewModel.setRole(role)
                        _uiState.value = AuthUiState(isLoginSuccessful = true)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val json = JSONObject(errorBody ?: "")
                        json.optString("message", "Error en el registro")
                    } catch (e: Exception) {
                        "Error en el servidor Railway"
                    }
                    _uiState.value = AuthUiState(error = errorMessage)
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = "Fallo de conexión: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }
}
