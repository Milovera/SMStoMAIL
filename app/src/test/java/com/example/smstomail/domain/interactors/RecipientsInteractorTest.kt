package com.example.smstomail.domain.interactors

import com.example.smstomail.data.entity.Recipient
import com.example.smstomail.data.repository.IRecipientsRepository
import com.example.smstomail.data.repository.mockAndroidLog
import com.example.smstomail.data.repository.unmockAndroidLog
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RecipientsInteractorTest {
    class RecipientsRepositoryMock: IRecipientsRepository {
        val dataMap: MutableMap<Int, Recipient> = mutableMapOf()

        override suspend fun getItems(): List<Recipient> {
            return dataMap.values.toList()
        }

        override suspend fun insertItem(item: Recipient) {
            dataMap[item.id] = item
        }

        override suspend fun updateItem(item: Recipient) = insertItem(item)

        override suspend fun deleteItem(itemId: Int) {
            dataMap.remove(itemId)
        }
    }

    private val repository = RecipientsRepositoryMock()
    private val interactor = RecipientsInteractor(repository)

    suspend fun addOneItemToRepository() {
        val newItem = Recipient(id = 1, value = "recipient")
        repository.insertItem(newItem)
    }
    suspend fun addTwoItemToRepository() {
        addOneItemToRepository()
        val newItem = Recipient(id = 2, value = "recipient2")
        repository.insertItem(newItem)
    }

    @BeforeEach
    fun setUp() {
        mockAndroidLog()
    }

    @AfterEach
    fun tearDown() {
        unmockAndroidLog()
    }

    @Test
    fun emptyRecipientsList_invalidState() = runTest {
        // given
        addOneItemToRepository()
        interactor.reset()

        // when
        interactor.deleteItem(interactor.itemsStateFlow.value.item.first().id)

        // then
        assert(interactor.itemsStateFlow.value.item.isEmpty())
        assert(interactor.itemsStateFlow.value.isValid == false)
    }

    @Test
    fun emptyRecipientValue_invalidState() = runTest {
        // given
        addOneItemToRepository()
        interactor.reset()

        // when
        interactor.createNewItem()

        // then
        assert(interactor.itemsStateFlow.value.isValid == false)
    }

    @Test
    fun createNewItemAndSave_insertIntoRepository() = runTest {
        // when
        interactor.createNewItem()
        interactor.save()

        // then
        assert(interactor.itemsStateFlow.value.item.size == 1)
        assert(repository.dataMap.size == 1)
        assert(interactor.itemsStateFlow.value.isModified == false)
    }

    @Test
    fun createNewItemWithoutSave_repositoryNotAffected() = runTest {
        // when
        interactor.createNewItem()

        // then
        assert(interactor.itemsStateFlow.value.item.size == 1)
        assert(repository.dataMap.isEmpty())
        assert(interactor.itemsStateFlow.value.isModified == true)
    }

    @Test
    fun initialReset_gotItemsFromRepository() = runTest {
        // given
        addOneItemToRepository()

        // when
        interactor.reset()

        // then
        val values = interactor.itemsStateFlow.value.item
        assert(values.size == 1 && values.containsAll(repository.dataMap.values))
        assert(interactor.itemsStateFlow.value.isModified == false)
    }

    @Test
    fun addItemAndReset_syncWithRepository() = runTest {
        // given
        addOneItemToRepository()
        interactor.reset()

        // when
        interactor.createNewItem()
        interactor.reset()

        // then
        assert(interactor.itemsStateFlow.value.item.size == 1)
        assert(repository.dataMap.size == 1)
        assert(interactor.itemsStateFlow.value.item.contains(repository.dataMap.values.first()))
        assert(interactor.itemsStateFlow.value.isModified == false)
    }

    @Test
    fun deleteWithoutSave_repositoryNotAffected()  = runTest {
        // given
        addOneItemToRepository()
        interactor.reset()

        // when
        interactor.deleteItem(interactor.itemsStateFlow.value.item.first().id)

        // then
        assert(interactor.itemsStateFlow.value.item.isEmpty() && repository.dataMap.isNotEmpty())
        assert(interactor.itemsStateFlow.value.isModified == true)
    }

    @Test
    fun deleteItemAndSave_repositoryDeleteItem() = runTest {
        // given
        addOneItemToRepository()
        interactor.reset()

        // when
        interactor.deleteItem(interactor.itemsStateFlow.value.item.first().id)
        interactor.save()

        // then
        assert(interactor.itemsStateFlow.value.item.isEmpty() && repository.dataMap.isEmpty())
        assert(interactor.itemsStateFlow.value.isModified == false)
    }

    @Test
    fun updateWithoutSave_repositoryNotAffected()  = runTest {
        // given
        addOneItemToRepository()
        interactor.reset()

        // when
        val originalItem = interactor.itemsStateFlow.value.item.first()
        val updatedItem = Recipient(id = originalItem.id, value = "newValue")
        interactor.updateItem(updatedItem.id, updatedItem)

        // then
        assert(interactor.itemsStateFlow.value.item.size == 1 && interactor.itemsStateFlow.value.item.contains(updatedItem))
        assert(repository.dataMap.values.size == 1 && repository.dataMap.values.contains(originalItem))
        assert(interactor.itemsStateFlow.value.isModified == true)
    }

    @Test
    fun updateItemAndSave_repositoryUpdateItem() = runTest {
        // given
        addOneItemToRepository()
        interactor.reset()

        // when
        val originalItem = interactor.itemsStateFlow.value.item.first()
        val updatedItem = Recipient(id = originalItem.id, value = "newValue")
        interactor.updateItem(updatedItem.id, updatedItem)
        interactor.save()

        // then
        assert(interactor.itemsStateFlow.value.item.size == 1 && interactor.itemsStateFlow.value.item.contains(updatedItem))
        assert(repository.dataMap.values.size == 1 && repository.dataMap.values.contains(updatedItem))
        assert(interactor.itemsStateFlow.value.isModified == false)
    }

    @Test
    fun createNewItem_newItemHasNextId() = runTest {
        // given
        addOneItemToRepository()
        interactor.reset()

        // when
        val expectedId = repository.dataMap.keys.max() + 1
        interactor.createNewItem()

        // then
        assert(interactor.itemsStateFlow.value.item.size == 2 && interactor.itemsStateFlow.value.item.map { it.id }.contains(expectedId))
        assert(interactor.itemsStateFlow.value.isModified == true)
    }

    @Test
    fun deleteUpdatedItem_repositoryDeleteItem() = runTest {
        // given
        addOneItemToRepository()
        interactor.reset()

        // when
        val originalItem = interactor.itemsStateFlow.value.item.first()
        val updatedItem = Recipient(id = originalItem.id, value = "newValue")
        interactor.updateItem(updatedItem.id, updatedItem)
        interactor.deleteItem(updatedItem.id)
        interactor.save()

         // then
        assert(interactor.itemsStateFlow.value.item.isEmpty() && repository.dataMap.isEmpty())
        assert(interactor.itemsStateFlow.value.isModified == false)
    }

    @Test
    fun updateNewItem_repositoryInsertItem() = runTest {
        // given
        interactor.createNewItem()

        // when
        val originalItem = interactor.itemsStateFlow.value.item.first()
        val updatedItem = Recipient(id = originalItem.id, value = "newValue")
        interactor.updateItem(updatedItem.id, updatedItem)
        interactor.save()

        // then
        assert(repository.dataMap.size == 1 && repository.dataMap.values.contains(updatedItem))
        assert(interactor.itemsStateFlow.value.isModified == false)
    }

    @Test
    fun createNewItemAndDeleteExists_repositoryDeleteItemsAndSaveNew() = runTest {
        // given
        addTwoItemToRepository()
        interactor.reset()

        // when
        val expectedId = repository.dataMap.keys.max() + 1
        interactor.createNewItem()
        interactor.itemsStateFlow.value.item.forEach {
            if(it.id != expectedId) {
                interactor.deleteItem(it.id)
            }
        }
        interactor.save()

        // then
        assert(interactor.itemsStateFlow.value.item.size == 1 && interactor.itemsStateFlow.value.item.map { it.id }.contains(expectedId))
        assert(repository.dataMap.size == 1 && repository.dataMap.contains(expectedId))
        assert(interactor.itemsStateFlow.value.isModified == false)
    }
    @Test
    fun updateAndDeleteItem_repositoryDeleteItems() = runTest {
        // given
        addTwoItemToRepository()
        interactor.reset()

        // when
        val updatedItem = interactor.itemsStateFlow.value.item.first().copy(value = "updatedItem")
        interactor.updateItem(updatedItem.id, updatedItem)
        interactor.itemsStateFlow.value.item.map { it.id }.forEach {
            interactor.deleteItem(it)
        }
        interactor.save()

        // then
        assert(interactor.itemsStateFlow.value.item.isEmpty())
        assert(repository.dataMap.isEmpty())
        assert(interactor.itemsStateFlow.value.isModified == false)
    }
}