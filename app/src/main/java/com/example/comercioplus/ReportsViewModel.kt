package com.example.comercioplus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportsViewModel : ViewModel() {
    private val _reportData = MutableStateFlow<AiReportResponse?>(null)
    val reportData = _reportData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun fetchAiReport() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val cb = System.currentTimeMillis().toString()
                val response = RetrofitInstance.api.getAiSummary(cb)
                if (response.isSuccessful) {
                    _reportData.value = response.body()
                } else {
                    _error.value = "Error al obtener reporte: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun adjustIVA(percentage: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.adjustIVA(IVARequest(percentage))
                if (response.isSuccessful) {
                    // Refrescar datos si es necesario
                    fetchAiReport()
                }
            } catch (e: Exception) {
                _error.value = "Error al ajustar IVA: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
