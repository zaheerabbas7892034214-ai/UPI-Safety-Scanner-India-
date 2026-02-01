package com.upisafety.scanner.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.upisafety.scanner.data.local.AppDatabase
import com.upisafety.scanner.data.repository.BillingRepository
import com.upisafety.scanner.data.repository.ScanRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : AndroidViewModel(application) {
    
    private val billingRepository = BillingRepository(application)
    
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()
    
    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()
    
    init {
        viewModelScope.launch {
            // Query purchases to verify entitlement
            billingRepository.queryPurchases()
            
            // Collect pro status
            billingRepository.isProUser.collect { isPro ->
                _isPro.value = isPro
            }
        }
        
        viewModelScope.launch {
            // Minimum splash duration
            delay(2000)
            _isReady.value = true
        }
    }
}
