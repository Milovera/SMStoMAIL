package com.example.smstomail.data.repository

interface IRoomRepository<T> {
    suspend fun getItems(): List<T>
    suspend fun insertItem(item: T)
    suspend fun updateItem(item: T)
    suspend fun deleteItem(itemId: Int)
}