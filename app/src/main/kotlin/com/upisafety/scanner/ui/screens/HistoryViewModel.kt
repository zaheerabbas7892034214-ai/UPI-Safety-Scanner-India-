package com.upisafety.scanner.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.upisafety.scanner.data.local.AppDatabase
import com.upisafety.scanner.data.repository.BillingRepository
import com.upisafety.scanner.data.repository.ScanRepository
import com.upisafety.scanner.domain.model.ScanHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val scanRepository = ScanRepository(AppDatabase.getDatabase(application).scanDao())
    private val billingRepository = BillingRepository(application)
    
    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()
    
    private val _scans = MutableStateFlow<List<ScanHistory>>(emptyList())
    val scans: StateFlow<List<ScanHistory>> = _scans.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    init {
        viewModelScope.launch {
            billingRepository.isProUser.collect { isPro ->
                _isPro.value = isPro
                loadScans()
            }
        }
    }
    
    private fun loadScans() {
        viewModelScope.launch {
            if (_searchQuery.value.isBlank()) {
                if (_isPro.value) {
                    scanRepository.getAllScans().collect { scans ->
                        _scans.value = scans
                    }
                } else {
                    scanRepository.getRecentScans(20).collect { scans ->
                        _scans.value = scans
                    }
                }
            } else {
                searchScans(_searchQuery.value)
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            loadScans()
        } else {
            searchScans(query)
        }
    }
    
    private fun searchScans(query: String) {
        viewModelScope.launch {
            scanRepository.searchScans(query).collect { scans ->
                _scans.value = if (_isPro.value) scans else scans.take(20)
            }
        }
    }
    
    fun clearHistory() {
        viewModelScope.launch {
            scanRepository.clearAllScans()
            _scans.value = emptyList()
        }
    }
    
    fun exportToCSV(): String {
        val csvBuilder = StringBuilder()
        csvBuilder.append("Timestamp,Payee Address,Payee Name,Amount,Risk Level\n")
        
        _scans.value.forEach { scan ->
            csvBuilder.append("${scan.timestamp},")
            csvBuilder.append("${scan.payeeAddress ?: ""},")
            csvBuilder.append("${scan.payeeName ?: ""},")
            csvBuilder.append("${scan.amount ?: ""},")
            csvBuilder.append("${scan.riskLevel}\n")
        }
        
        return csvBuilder.toString()
    }
}
