package com.example.smstomail.data.repository

import com.example.smstomail.data.database.dao.IRecipientDao
import com.example.smstomail.data.entity.Recipient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipientsRepository @Inject constructor(
    private val recipientDao: IRecipientDao
): IRecipientsRepository {
    override suspend fun getItems(): List<Recipient> {
        return recipientDao.getAllItems()
    }

    override suspend fun insertItem(item: Recipient) {
        recipientDao.insert(item)
    }

    override suspend fun updateItem(item: Recipient) {
        recipientDao.update(item)
    }

    override suspend fun deleteItem(itemId: Int) {
        recipientDao.delete(itemId)
    }
}