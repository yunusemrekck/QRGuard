package com.example.qrguard.data.repository

import com.example.qrguard.data.local.QrScanDao
import com.example.qrguard.data.local.QrScanEntity
import com.example.qrguard.domain.model.QrContent
import com.example.qrguard.domain.model.QrContentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QrScanRepository(private val dao: QrScanDao) {
    
    val allScans: Flow<List<QrContent>> = dao.getAllScans().map { entities ->
        entities.map { it.toQrContent() }
    }
    
    val favorites: Flow<List<QrContent>> = dao.getFavorites().map { entities ->
        entities.map { it.toQrContent() }
    }
    
    val totalCount: Flow<Int> = dao.getTotalCount()
    
    val favoritesCount: Flow<Int> = dao.getFavoritesCount()
    
    fun getRecentScans(limit: Int = 5): Flow<List<QrContent>> {
        return dao.getRecentScans(limit).map { entities ->
            entities.map { it.toQrContent() }
        }
    }
    
    fun getScansByType(type: QrContentType): Flow<List<QrContent>> {
        return dao.getScansByType(type).map { entities ->
            entities.map { it.toQrContent() }
        }
    }
    
    suspend fun saveScan(content: QrContent): Long {
        return dao.insert(content.toEntity())
    }
    
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean) {
        dao.updateFavoriteStatus(id, isFavorite)
    }
    
    suspend fun deleteScan(id: Long) {
        dao.deleteById(id)
    }
    
    suspend fun deleteAllHistory() {
        dao.deleteAllNonFavorites()
    }
    
    suspend fun deleteAll() {
        dao.deleteAll()
    }
    
    private fun QrScanEntity.toQrContent(): QrContent {
        return QrContent(
            id = id,
            rawValue = rawValue,
            type = type,
            displayValue = displayValue,
            timestamp = timestamp,
            isFavorite = isFavorite
        )
    }
    
    private fun QrContent.toEntity(): QrScanEntity {
        return QrScanEntity(
            id = if (id == 0L) 0 else id,
            rawValue = rawValue,
            displayValue = displayValue,
            type = type,
            timestamp = timestamp,
            isFavorite = isFavorite
        )
    }
}
