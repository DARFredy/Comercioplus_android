package com.example.comercioplus

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeState {
    LIGHT,
    DARK,
    SYSTEM
}

class ThemeViewModel : ViewModel() {
    private val _themeState = MutableStateFlow(ThemeState.DARK) // Default to Dark Mode
    val themeState: StateFlow<ThemeState> = _themeState.asStateFlow()

    fun setTheme(theme: ThemeState) {
        _themeState.value = theme
    }
}
