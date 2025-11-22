package com.example.comercioplus

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comercioplus.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String, 
    val isUser: Boolean,
    val isBotAction: Boolean = false
)

data class AiUiState(
    val chatHistory: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false
)

class AiViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AiUiState())
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    private val model = GenerativeAiConfig.model
    private val TAG_FIX = "ComercioplusAI"

    fun sendMessage(
        userText: String, 
        products: List<Product> = emptyList(), 
        botName: String = "Asistente",
        personality: String = "Amigable"
    ) {
        val currentHistory = _uiState.value.chatHistory.toMutableList()
        currentHistory.add(ChatMessage(userText, true))
        _uiState.value = _uiState.value.copy(chatHistory = currentHistory, isLoading = true)

        viewModelScope.launch {
            try {
                val prompt = "Responde como $botName ($personality) al Administrador Fredy: $userText"
                val response = model.generateContent(prompt)
                val reply = response.text ?: "No pude procesar eso, Fredy."
                
                currentHistory.add(ChatMessage(reply, false))
                _uiState.value = _uiState.value.copy(chatHistory = currentHistory, isLoading = false)

            } catch (e: Exception) {
                Log.e(TAG_FIX, "Error real: ${e.message}")
                // Mostramos el error real en el chat para diagnosticar
                val errorMsg = "¡Fredy! Ya lo solucionaremos pronto. El error técnico es: ${e.localizedMessage ?: "Desconocido"}"
                currentHistory.add(ChatMessage(errorMsg, false))
                _uiState.value = _uiState.value.copy(chatHistory = currentHistory, isLoading = false)
            }
        }
    }
}
