package com.example.comercioplus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comercioplus.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val name: String = "", 
    val email: String = "", 
    val profilePictureUrl: String = "",
    val role: Role = Role.INVITADO,
    val store_id: Long? = null
)

class UserViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    fun fetchUserProfile() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getMyProfile()
                if (response.isSuccessful && response.body() != null) {
                    _userProfile.value = response.body()!!
                }
            } catch (e: Exception) {
                // Manejar error de red si es necesario
            }
        }
    }

    fun updateUserProfile(name: String, email: String, profilePictureUrl: String, role: Role) {
        _userProfile.value = UserProfile(name, email, profilePictureUrl, role)
    }

    fun setRole(role: Role) {
        _userProfile.value = _userProfile.value.copy(role = role)
    }
}