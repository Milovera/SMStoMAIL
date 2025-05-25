package com.example.smstomail.data.database.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.smstomail.data.entity.Filter
import com.example.smstomail.data.entity.Recipient

@Database(
    entities = [Recipient::class, Filter::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun recipientsDao(): IRecipientDao
    abstract fun filtersDao(): IFilterDao
}