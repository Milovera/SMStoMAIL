package com.example.smstomail.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smstomail.data.entity.Recipient

@Dao
interface IRecipientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Recipient)
    @Update
    suspend fun update(item: Recipient)
    @Query("DELETE FROM recipients where id = :itemId")
    suspend fun delete(itemId: Int)
    @Query("SELECT * FROM recipients")
    suspend fun getAllItems(): List<Recipient>
}