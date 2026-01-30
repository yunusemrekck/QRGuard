package com.yunusek.qrguard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yunusek.qrguard.generator.QrType

@Entity(tableName = "created_qr_codes")
data class CreatedQrEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val type: QrType,
    val label: String,
    val timestamp: Long = System.currentTimeMillis(),
    val foregroundColor: Int,
    val backgroundColor: Int,
    val isFavorite: Boolean = false
)
