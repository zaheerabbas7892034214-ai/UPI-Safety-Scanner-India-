package com.upisafety.scanner.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.upisafety.scanner.data.local.AppDatabase
import com.upisafety.scanner.data.repository.BillingRepository
import com.upisafety.scanner.data.repository.ScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val scanRepository = ScanRepository(AppDatabase.getDatabase(application).scanDao())
    private val billingRepository = BillingRepository(application)
    
    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()
    
    private val _dailyScansRemaining = MutableStateFlow(10)
    val dailyScansRemaining: StateFlow<Int> = _dailyScansRemaining.asStateFlow()
    
    private val _canScan = MutableStateFlow(true)
    val canScan: StateFlow<Boolean> = _canScan.asStateFlow()
    
    init {
        viewModelScope.launch {
            billingRepository.isProUser.collect { isPro ->
                _isPro.value = isPro
                updateScanLimits()
            }
        }
    }
    
    fun refreshScanLimits() {
        viewModelScope.launch {
            updateScanLimits()
        }
    }
    
    private suspend fun updateScanLimits() {
        if (_isPro.value) {
            _dailyScansRemaining.value = -1 // Unlimited
            _canScan.value = true
        } else {
            val todayScans = scanRepository.getTodayScansCount()
            val remaining = 10 - todayScans
            _dailyScansRemaining.value = remaining.coerceAtLeast(0)
            _canScan.value = remaining > 0
        }
    }
}
