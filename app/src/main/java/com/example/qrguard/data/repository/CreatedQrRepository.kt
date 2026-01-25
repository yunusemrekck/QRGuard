package com.example.qrguard.data.repository

import com.example.qrguard.data.local.CreatedQrDao
import com.example.qrguard.data.local.CreatedQrEntity
import kotlinx.coroutines.flow.Flow

class CreatedQrRepository(private val dao: CreatedQrDao) {
    
    val allCreatedQrs: Flow<List<CreatedQrEntity>> = dao.getAllCreatedQrs()
    
    val favoriteCreatedQrs: Flow<List<CreatedQrEntity>> = dao.getFavoriteCreatedQrs()
    
    val createdQrCount: Flow<Int> = dao.getCreatedQrCount()
    
    fun getRecentCreatedQrs(limit: Int = 5): Flow<List<CreatedQrEntity>> {
        return dao.getRecentCreatedQrs(limit)
    }
    
    suspend fun saveCreatedQr(qr: CreatedQrEntity): Long {
        return dao.insert(qr)
    }
    
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean) {
        dao.updateFavoriteStatus(id, isFavorite)
    }
    
    suspend fun deleteCreatedQr(id: Long) {
        dao.deleteById(id)
    }
    
    suspend fun getCreatedQrById(id: Long): CreatedQrEntity? {
        return dao.getCreatedQrById(id)
    }
}
