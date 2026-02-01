package com.upisafety.scanner.data.repository

import com.upisafety.scanner.data.local.ScanDao
import com.upisafety.scanner.data.local.ScanEntity
import com.upisafety.scanner.domain.model.RiskLevel
import com.upisafety.scanner.domain.model.ScanHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ScanRepository(private val scanDao: ScanDao) {
    
    fun getAllScans(): Flow<List<ScanHistory>> {
        return scanDao.getAllScans().map { entities ->
            entities.map { it.toScanHistory() }
        }
    }
    
    fun getRecentScans(limit: Int): Flow<List<ScanHistory>> {
        return scanDao.getRecentScans(limit).map { entities ->
            entities.map { it.toScanHistory() }
        }
    }
    
    fun searchScans(query: String): Flow<List<ScanHistory>> {
        return scanDao.searchScans(query).map { entities ->
            entities.map { it.toScanHistory() }
        }
    }
    
    suspend fun insertScan(
        rawContent: String,
        payeeAddress: String?,
        payeeName: String?,
        amount: String?,
        transactionNote: String?,
        riskLevel: RiskLevel,
        riskReasons: List<String>
    ): Long {
        val entity = ScanEntity(
            rawContent = rawContent,
            payeeAddress = payeeAddress,
            payeeName = payeeName,
            amount = amount,
            transactionNote = transactionNote,
            riskLevel = riskLevel.name,
            riskReasons = riskReasons.joinToString("|")
        )
        return scanDao.insertScan(entity)
    }
    
    suspend fun clearAllScans() {
        scanDao.clearAllScans()
    }
    
    suspend fun getScanCount(): Int {
        return scanDao.getScanCount()
    }
    
    suspend fun getTodayScansCount(): Int {
        val todayStart = getTodayStartTimestamp()
        return scanDao.getScansCountSince(todayStart)
    }
    
    private fun getTodayStartTimestamp(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun ScanEntity.toScanHistory(): ScanHistory {
        return ScanHistory(
            id = id,
            rawContent = rawContent,
            payeeAddress = payeeAddress,
            payeeName = payeeName,
            amount = amount,
            transactionNote = transactionNote,
            riskLevel = RiskLevel.valueOf(riskLevel),
            riskReasons = riskReasons?.split("|")?.filter { it.isNotBlank() } ?: emptyList(),
            timestamp = timestamp
        )
    }
}
