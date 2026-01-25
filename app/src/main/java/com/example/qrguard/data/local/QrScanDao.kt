package com.example.qrguard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.qrguard.domain.model.QrContentType
import kotlinx.coroutines.flow.Flow

@Dao
interface QrScanDao {
    
    @Query("SELECT * FROM qr_scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<QrScanEntity>>
    
    @Query("SELECT * FROM qr_scans ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentScans(limit: Int): Flow<List<QrScanEntity>>
    
    @Query("SELECT * FROM qr_scans WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<QrScanEntity>>
    
    @Query("SELECT * FROM qr_scans WHERE type = :type ORDER BY timestamp DESC")
    fun getScansByType(type: QrContentType): Flow<List<QrScanEntity>>
    
    @Query("SELECT * FROM qr_scans WHERE id = :id")
    suspend fun getScanById(id: Long): QrScanEntity?
    
    @Query("SELECT COUNT(*) FROM qr_scans")
    fun getTotalCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM qr_scans WHERE isFavorite = 1")
    fun getFavoritesCount(): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scan: QrScanEntity): Long
    
    @Update
    suspend fun update(scan: QrScanEntity)
    
    @Delete
    suspend fun delete(scan: QrScanEntity)
    
    @Query("DELETE FROM qr_scans WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM qr_scans WHERE isFavorite = 0")
    suspend fun deleteAllNonFavorites()
    
    @Query("DELETE FROM qr_scans")
    suspend fun deleteAll()
    
    @Query("UPDATE qr_scans SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
}
