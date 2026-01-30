package com.yunusek.qrguard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yunusek.qrguard.domain.model.QrContentType

@Entity(tableName = "qr_scans")
data class QrScanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rawValue: String,
    val displayValue: String,
    val type: QrContentType,
    val timestamp: Long,
    val isFavorite: Boolean = false,
    val parsedDataJson: String? = null
)
