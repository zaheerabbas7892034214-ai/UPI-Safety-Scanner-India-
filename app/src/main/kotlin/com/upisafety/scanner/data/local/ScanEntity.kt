package com.upisafety.scanner.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rawContent: String,
    val payeeAddress: String?,
    val payeeName: String?,
    val amount: String?,
    val transactionNote: String?,
    val riskLevel: String,
    val riskReasons: String?,
    val timestamp: Long = System.currentTimeMillis()
)
