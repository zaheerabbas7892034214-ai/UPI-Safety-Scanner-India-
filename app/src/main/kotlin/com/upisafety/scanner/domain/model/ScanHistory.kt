package com.upisafety.scanner.domain.model

data class ScanHistory(
    val id: Long,
    val rawContent: String,
    val payeeAddress: String?,
    val payeeName: String?,
    val amount: String?,
    val transactionNote: String?,
    val riskLevel: RiskLevel,
    val riskReasons: List<String>,
    val timestamp: Long
)
