package com.example.qrguard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CreatedQrDao {
    
    @Query("SELECT * FROM created_qr_codes ORDER BY timestamp DESC")
    fun getAllCreatedQrs(): Flow<List<CreatedQrEntity>>
    
    @Query("SELECT * FROM created_qr_codes ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentCreatedQrs(limit: Int): Flow<List<CreatedQrEntity>>
    
    @Query("SELECT * FROM created_qr_codes WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteCreatedQrs(): Flow<List<CreatedQrEntity>>
    
    @Query("SELECT * FROM created_qr_codes WHERE id = :id")
    suspend fun getCreatedQrById(id: Long): CreatedQrEntity?
    
    @Query("SELECT COUNT(*) FROM created_qr_codes")
    fun getCreatedQrCount(): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qr: CreatedQrEntity): Long
    
    @Update
    suspend fun update(qr: CreatedQrEntity)
    
    @Delete
    suspend fun delete(qr: CreatedQrEntity)
    
    @Query("DELETE FROM created_qr_codes WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("UPDATE created_qr_codes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
}
