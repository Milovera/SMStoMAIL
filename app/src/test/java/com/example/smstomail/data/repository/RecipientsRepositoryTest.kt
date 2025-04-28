package com.example.smstomail.data.repository

import com.example.smstomail.data.database.dao.IRecipientDao
import com.example.smstomail.data.entity.Recipient
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RecipientsRepositoryTest {
    class DaoMock: IRecipientDao {
        val dataMap: MutableMap<Int, Recipient> = mutableMapOf()

        override suspend fun insert(item: Recipient) {
            dataMap[item.id] = item
        }

        override suspend fun update(item: Recipient) {
            if(dataMap.contains(item.id)) {
                dataMap[item.id] = item
            } else {
                throw NoSuchElementException()
            }
        }

        override suspend fun delete(itemId: Int) {
            if (dataMap.contains(itemId)) {
                dataMap.remove(itemId)
            } else {
                throw NoSuchElementException()
            }
        }

        override suspend fun getAllItems(): List<Recipient> {
            return dataMap.values.toList()
        }
    }
    
    private val dao = DaoMock()
    private val repository = RecipientsRepository(recipientDao = dao)

    private val item1 = Recipient(id = 0, value = "recipient@mail.com")
    private val item2 = Recipient(id = 2, value = "recipient2@mail.com")

    private suspend fun addOneItemToDao() = repository.insertItem(item1)
    private suspend fun addTwoItemsToDao() {
        repository.insertItem(item1)
        repository.insertItem(item2)
    }

    @Test
    fun repositoryInsert_insertItemIntoDao() = runTest {
        // when
        addOneItemToDao()

        // then
        val allItems =  dao.dataMap.values
        assert(allItems.first() == item1 && allItems.size == 1)
    }

    @Test
    fun repositoryGetItems_returnAllItemsFromDao() = runTest {
        // when
        addTwoItemsToDao()

        // then
        val allItems =  dao.dataMap.values
        assert(allItems.first() == item1 && allItems.last() == item2)
    }

    @Test
    fun repositoryInsertDuplicate_replaceItemInDao() = runTest {
        // given
        addOneItemToDao()

        // when
        val newItem = item1.copy(value = "newValue")
        repository.insertItem(newItem)

        // then
        val allItems =  dao.dataMap.values
        assert(allItems.first() == newItem)
    }

    @Test
    fun repositoryUpdateItem_updateItemInDao() = runTest {
        // given
        addOneItemToDao()

        // when
        val newItem = item1.copy(value = "updatedValue")
        repository.updateItem(newItem)

        // then
        val allItems =  dao.dataMap.values
        assert(allItems.first() == newItem)
    }

    @Test
    fun repositoryDeleteItem_deleteItemInDao() = runTest {
        // given
        addTwoItemsToDao()

        // when
        repository.deleteItem(item2.id)

        // then
        val allItems =  dao.dataMap.values
        assert(allItems.size == 1 && allItems.first() == item1)
    }
}