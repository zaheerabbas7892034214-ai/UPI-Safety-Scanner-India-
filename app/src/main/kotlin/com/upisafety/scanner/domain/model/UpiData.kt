package com.upisafety.scanner.domain.model

data class UpiData(
    val rawContent: String,
    val payeeAddress: String?,
    val payeeName: String?,
    val amount: String?,
    val transactionNote: String?,
    val merchantCode: String? = null,
    val url: String? = null
)
