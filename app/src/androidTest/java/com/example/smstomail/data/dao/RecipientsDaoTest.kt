package com.example.smstomail.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smstomail.data.database.dao.AppDatabase
import com.example.smstomail.data.database.dao.IRecipientDao
import com.example.smstomail.data.entity.Recipient
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipientsDaoTest {
    private lateinit var dao: IRecipientDao
    private lateinit var db: AppDatabase

    private val item1 = Recipient(id = 0, value = "recipient@mail.com")
    private val item2 = Recipient(id = 2, value = "recipient2@mail.com")

    @Before
    fun setupDatabase() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(
            context = context,
            klass = AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        
        dao = db.recipientsDao()
    }
    @After
    fun closeDatabase() = db.close()

    private suspend fun addOneItemToDao() = dao.insert(item1)
    private suspend fun addTwoItemsToDao() {
        dao.insert(item1)
        dao.insert(item2)
    }

    @Test
    fun repositoryInsert_insertItemIntoDao() = runTest {
        addOneItemToDao()
        val allItems =  dao.getAllItems()
        assert(allItems.first() == item1 && allItems.size == 1)
    }

    @Test
    fun repositoryGetItems_returnAllItemsFromDao() = runTest {
        addTwoItemsToDao()
        val allItems =  dao.getAllItems()
        assert(allItems.first() == item1 && allItems.last() == item2)
    }

    @Test
    fun repositoryInsertDuplicate_replaceItemInDao() = runTest {
        addOneItemToDao()
        val newItem = item1.copy(value = "newValue")
        dao.insert(newItem)
        val allItems =  dao.getAllItems()
        assert(allItems.first() == newItem)
    }

    @Test
    fun repositoryUpdateItem_updateItemInDao() = runTest {
        addOneItemToDao()
        val newItem = item1.copy(value = "updatedValue")
        dao.update(newItem)
        val allItems =  dao.getAllItems()
        assert(allItems.first() == newItem)
    }

    @Test
    fun repositoryDeleteItem_deleteItemInDao() = runTest {
        addTwoItemsToDao()
        dao.delete(item2.id)
        val allItems =  dao.getAllItems()
        assert(allItems.size == 1 && allItems.first() == item1)
    }
}