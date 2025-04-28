package com.example.smstomail.data.database.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smstomail.data.entity.Filter
import com.example.smstomail.data.entity.Recipient

@Database(
    entities = [Recipient::class, Filter::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    companion object {
        @Volatile
        private var Instance: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "application_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
    abstract fun recipientsDao(): IRecipientDao
    abstract fun filtersDao(): IFilterDao
}