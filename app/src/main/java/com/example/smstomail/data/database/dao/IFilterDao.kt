package com.example.smstomail.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smstomail.data.entity.Filter

@Dao
interface IFilterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Filter)
    @Update
    suspend fun update(item: Filter)
    @Query("DELETE FROM filters where id = :itemId")
    suspend fun delete(itemId: Int)
    @Query("SELECT * FROM filters")
    suspend fun getAllItems(): List<Filter>
}