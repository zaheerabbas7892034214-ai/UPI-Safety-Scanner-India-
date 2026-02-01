package com.upisafety.scanner.ui.screens

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.upisafety.scanner.data.repository.BillingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaywallViewModel(application: Application) : AndroidViewModel(application) {
    
    private val billingRepository = BillingRepository(application)
    
    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()
    
    init {
        viewModelScope.launch {
            billingRepository.isProUser.collect { isPro ->
                _isPro.value = isPro
            }
        }
    }
    
    fun subscribe(activity: Activity) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = billingRepository.launchPurchaseFlow(activity)
            _isLoading.value = false
            
            if (!success) {
                _message.value = "Failed to start purchase flow"
            }
        }
    }
    
    fun restorePurchases() {
        viewModelScope.launch {
            _isLoading.value = true
            billingRepository.restorePurchases()
            _isLoading.value = false
            
            // Wait a moment for the status to update
            kotlinx.coroutines.delay(1000)
            
            if (_isPro.value) {
                _message.value = "Purchases restored successfully!"
            } else {
                _message.value = "No purchases found to restore"
            }
        }
    }
    
    fun clearMessage() {
        _message.value = null
    }
}
