package com.upisafety.scanner.ui.screens

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_LANGUAGE = "language"
        const val LANG_ENGLISH = "en"
        const val LANG_HINDI = "hi"
    }
    
    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _selectedLanguage = MutableStateFlow(
        prefs.getString(KEY_LANGUAGE, LANG_ENGLISH) ?: LANG_ENGLISH
    )
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()
    
    fun setLanguage(language: String) {
        _selectedLanguage.value = language
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }
}
