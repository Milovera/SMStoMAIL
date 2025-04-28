package com.example.smstomail.domain.interactors

import com.example.smstomail.data.entity.Filter
import com.example.smstomail.data.repository.IFiltersRepository
import com.example.smstomail.data.repository.mockAndroidLog
import com.example.smstomail.data.repository.unmockAndroidLog
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FiltersInteractorTest {
    class FilterRepositoryMock: IFiltersRepository {
        val dataMap: MutableMap<Int, Filter> = mutableMapOf()

        override suspend fun getItems(): List<Filter> {
            return dataMap.values.toList()
        }

        override suspend fun insertItem(item: Filter) {
            dataMap[item.id] = item
        }

        override suspend fun updateItem(item: Filter) = insertItem(item)

        override suspend fun deleteItem(itemId: Int) {
            dataMap.remove(itemId)
        }
    }

    private val repository = FilterRepositoryMock()
    private val interactor = FiltersInteractor(repository)

    suspend fun addOneItemToRepository() {
        val newItem = Filter(id = 1, value = "filter")
        repository.insertItem(newItem)
    }
    suspend fun addTwoItemToRepository() {
        addOneItemToRepository()
        val newItem = Filter(id = 2, value = "filter2")
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
    fun createNewItemAndSave_insertIntoRepository() = runTest {
        // given
        interactor.createNewItem()

        // when
        interactor.save()

        // then
        assert(interactor.itemsStateFlow.value.item.size == 1)
        assert(repository.dataMap.size == 1)
    }

    @Test
    fun createNewItemWithoutSave_repositoryNotAffected() = runTest {
        // when
        interactor.createNewItem()

        // then
        assert(interactor.itemsStateFlow.value.item.size == 1)
        assert(repository.dataMap.isEmpty())
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
    }

    @Test
    fun addItemAndReset_syncWithRepository() = runTest {
        // given
        addOneItemToRepository()
        interactor.reset()
        interactor.createNewItem()

        // when
        interactor.reset()

        // then
        assert(interactor.itemsStateFlow.value.item.size == 1)
        assert(repository.dataMap.size == 1)
        assert(interactor.itemsStateFlow.value.item.contains(repository.getItems().first()))
    }

    @Test
    fun resetWithEmptyRepository_interactorIsEmpty() = runTest {
        // given
        interactor.createNewItem()

        // when
        interactor.reset()

        // then
        assert(interactor.itemsStateFlow.value.item.isEmpty())
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
    }

    @Test
    fun updateWithoutSave_repositoryNotAffected()  = runTest {
        // given
        addOneItemToRepository()
        interactor.reset()

        // when
        val originalItem = interactor.itemsStateFlow.value.item.first()
        val updatedItem = Filter(id = originalItem.id, value = "newValue")
        interactor.updateItem(updatedItem.id, updatedItem)

        // then
        assert(interactor.itemsStateFlow.value.item.size == 1 && interactor.itemsStateFlow.value.item.contains(updatedItem))
        assert(repository.dataMap.values.size == 1 && repository.dataMap.values.contains(originalItem))
    }

    @Test
    fun updateItemAndSave_repositoryUpdateItem() = runTest {
        // given
        addOneItemToRepository()
        interactor.reset()

        // when
        val originalItem = interactor.itemsStateFlow.value.item.first()
        val updatedItem = Filter(id = originalItem.id, value = "newValue")
        interactor.updateItem(updatedItem.id, updatedItem)
        interactor.save()

        // then
        assert(interactor.itemsStateFlow.value.item.size == 1 && interactor.itemsStateFlow.value.item.contains(updatedItem))
        assert(repository.dataMap.values.size == 1 && repository.dataMap.values.contains(updatedItem))
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
    }

    @Test
    fun deleteUpdatedItem_repositoryDeleteItem() = runTest {
        // given
        addOneItemToRepository()
        interactor.reset()

        // when
        val originalItem = interactor.itemsStateFlow.value.item.first()
        val updatedItem = Filter(id = originalItem.id, value = "newValue")
        interactor.updateItem(updatedItem.id, updatedItem)
        interactor.deleteItem(updatedItem.id)
        interactor.save()

        // then
        assert(interactor.itemsStateFlow.value.item.isEmpty() && repository.dataMap.isEmpty())
    }

    @Test
    fun updateNewItemAndSave_repositoryInsertItem() = runTest {
        // given
        interactor.createNewItem()

        // when
        val originalItem = interactor.itemsStateFlow.value.item.first()
        val updatedItem = Filter(id = originalItem.id, value = "newValue")
        interactor.updateItem(updatedItem.id, updatedItem)
        interactor.save()

        // then
        assert(repository.dataMap.size == 1 && repository.dataMap.values.contains(updatedItem))
    }

    @Test
    fun createNewItemAndDeleteExists_repositoryDeleteItemsAndSaveNew() = runTest {
        // given
        addTwoItemToRepository()
        interactor.reset()

        // when
        val expectedNewItemId = repository.dataMap.keys.max() + 1
        interactor.createNewItem()
        interactor.itemsStateFlow.value.item.forEach {
            if(it.id != expectedNewItemId) {
                interactor.deleteItem(it.id)
            }
        }
        interactor.save()

        // then
        assert(interactor.itemsStateFlow.value.item.size == 1 && interactor.itemsStateFlow.value.item.map { it.id }.contains(expectedNewItemId))
        assert(repository.dataMap.size == 1 && repository.dataMap.contains(expectedNewItemId))
    }
    @Test
    fun updateAndDeleteItems_repositoryDeleteItems() = runTest {
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
    }
}