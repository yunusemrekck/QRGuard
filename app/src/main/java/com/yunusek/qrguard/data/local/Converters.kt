package com.yunusek.qrguard.data.local

import androidx.room.TypeConverter
import com.yunusek.qrguard.domain.model.QrContentType
import com.yunusek.qrguard.generator.QrType

class Converters {
    
    @TypeConverter
    fun fromQrContentType(type: QrContentType): String {
        return type.name
    }
    
    @TypeConverter
    fun toQrContentType(value: String): QrContentType {
        return QrContentType.valueOf(value)
    }
    
    @TypeConverter
    fun fromQrType(type: QrType): String {
        return type.name
    }
    
    @TypeConverter
    fun toQrType(value: String): QrType {
        return QrType.valueOf(value)
    }
}
