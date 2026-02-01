package com.upisafety.scanner.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.upisafety.scanner.data.local.AppDatabase
import com.upisafety.scanner.data.repository.ScanRepository
import com.upisafety.scanner.domain.model.RiskAssessment
import com.upisafety.scanner.domain.model.UpiData
import com.upisafety.scanner.utils.RiskAnalyzer
import com.upisafety.scanner.utils.UpiParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScannerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val scanRepository = ScanRepository(AppDatabase.getDatabase(application).scanDao())
    
    private val _scanResult = MutableStateFlow<UpiData?>(null)
    val scanResult: StateFlow<UpiData?> = _scanResult.asStateFlow()
    
    private val _riskAssessment = MutableStateFlow<RiskAssessment?>(null)
    val riskAssessment: StateFlow<RiskAssessment?> = _riskAssessment.asStateFlow()
    
    private val _showResult = MutableStateFlow(false)
    val showResult: StateFlow<Boolean> = _showResult.asStateFlow()
    
    fun onQrCodeScanned(content: String) {
        val upiData = UpiParser.parseUpiQrCode(content)
        
        if (upiData != null) {
            _scanResult.value = upiData
            _riskAssessment.value = RiskAnalyzer.analyzeRisk(upiData)
            _showResult.value = true
            
            // Save to database
            viewModelScope.launch {
                _riskAssessment.value?.let { assessment ->
                    scanRepository.insertScan(
                        rawContent = upiData.rawContent,
                        payeeAddress = upiData.payeeAddress,
                        payeeName = upiData.payeeName,
                        amount = upiData.amount,
                        transactionNote = upiData.transactionNote,
                        riskLevel = assessment.level,
                        riskReasons = assessment.reasons
                    )
                }
            }
        }
    }
    
    fun dismissResult() {
        _showResult.value = false
        _scanResult.value = null
        _riskAssessment.value = null
    }
}
