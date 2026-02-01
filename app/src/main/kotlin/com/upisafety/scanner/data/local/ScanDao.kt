package com.upisafety.scanner.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanEntity): Long

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanEntity>>

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentScans(limit: Int): Flow<List<ScanEntity>>

    @Query("SELECT * FROM scan_history WHERE payeeAddress LIKE '%' || :query || '%' OR payeeName LIKE '%' || :query || '%' OR transactionNote LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchScans(query: String): Flow<List<ScanEntity>>

    @Query("DELETE FROM scan_history")
    suspend fun clearAllScans()

    @Query("SELECT COUNT(*) FROM scan_history")
    suspend fun getScanCount(): Int

    @Query("SELECT COUNT(*) FROM scan_history WHERE timestamp >= :startTime")
    suspend fun getScansCountSince(startTime: Long): Int
}
