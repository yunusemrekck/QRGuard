package com.example.qrguard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [QrScanEntity::class, CreatedQrEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class QrGuardDatabase : RoomDatabase() {
    
    abstract fun qrScanDao(): QrScanDao
    abstract fun createdQrDao(): CreatedQrDao
    
    companion object {
        @Volatile
        private var INSTANCE: QrGuardDatabase? = null
        
        fun getInstance(context: Context): QrGuardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QrGuardDatabase::class.java,
                    "qrguard_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
