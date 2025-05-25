package com.example.smstomail.di

import android.content.Context
import androidx.room.Room
import com.example.smstomail.data.database.dao.AppDatabase
import com.example.smstomail.data.database.dao.IFilterDao
import com.example.smstomail.data.database.dao.IRecipientDao
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {
    @Volatile
    private var dbInstance: AppDatabase? = null

    @Provides
    fun provideDatabase(context: Context): AppDatabase {
        return dbInstance ?: synchronized(this) {
            Room.databaseBuilder(context, AppDatabase::class.java, "application_database")
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                .also { dbInstance = it }
        }
    }
    @Provides
    fun recipientsDao(db: AppDatabase): IRecipientDao {
        return db.recipientsDao()
    }
    @Provides
    fun filtersDao(db: AppDatabase): IFilterDao {
        return db.filtersDao()
    }
}