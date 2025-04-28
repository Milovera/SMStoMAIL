package com.example.smstomail.domain.interactors

import android.util.Log
import com.example.smstomail.data.entity.ItemSnapshot
import com.example.smstomail.data.repository.IRoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class AbstractItemListBufferInteractor<T>(
    protected val itemRepository: IRoomRepository<T>
) {
    companion object {
        const val LOG_TAG = "AbstractItemBufferInteractor"
    }

    private var itemsMap = mutableMapOf<Int, T>()
    private val itemsMutableStateFlow = MutableStateFlow<ItemSnapshot<List<T>>>(
        ItemSnapshot<List<T>>(
            item = emptyList(),
            isModified = false,
            isValid = false
        )
    )
    private var repositoryItemsHash: Int? = null
    private var newItemsIdList = mutableListOf<Int>()
    private var deleteItemsIdList = mutableListOf<Int>()
    private var updateItemsIdList = mutableListOf<Int>()
    private var nextItemId = 0

    val itemsStateFlow = itemsMutableStateFlow.asStateFlow()
    val items: List<T>
        get() {
            return itemsMap
                .filter { entry ->
                    !deleteItemsIdList.contains(entry.key)
                }
                .values
                .toList()
        }

    private fun emitUpdates() {
        Log.v(LOG_TAG, "emitUpdates")

        val snapshot = items.let { item ->
            ItemSnapshot(
                item = item,
                isModified = item.hashCode() != repositoryItemsHash,
                isValid = isValidItems()
            )
        }

        itemsMutableStateFlow.update {
            snapshot
        }
    }
    protected abstract fun createNewItem(nextItemId: Int): T
    protected abstract fun getItemId(item: T): Int
    protected abstract fun isValidItems(): Boolean
    fun createNewItem() {
        Log.v(LOG_TAG, "createNewItem with id $nextItemId")
        itemsMap.put(nextItemId, createNewItem(nextItemId))
        newItemsIdList += nextItemId++
        emitUpdates()
    }
    fun deleteItem(itemId: Int) {
        Log.v(LOG_TAG, "deleteItem with id=$itemId")
        if(newItemsIdList.contains(itemId)) {
            newItemsIdList.remove(itemId)
        } else {
            deleteItemsIdList.add(itemId)
        }

        itemsMap.remove(itemId)

        emitUpdates()
    }
    fun updateItem(id: Int, item: T) {
        Log.v(LOG_TAG, "Update item with id=$id")
        itemsMap[id] = item

        if(!newItemsIdList.contains(id)) {
            updateItemsIdList += id
        }

        emitUpdates()
    }
    open suspend fun save() {
        Log.v(LOG_TAG, "save")

        // update
        itemsMap
            .filter { entry ->
                !deleteItemsIdList.contains(entry.key)
            }
            .filter { entry ->
                updateItemsIdList.contains(entry.key)
            }
            .values
            .also {
                Log.v(LOG_TAG, "Update entries:\n$it")
            }
            .forEach { item ->
                itemRepository.updateItem(item)
            }

        // delete
        deleteItemsIdList
            .also {
                Log.v(LOG_TAG, "Delete entries:\n$it")
            }
            .forEach { entryId ->
                itemRepository.deleteItem(entryId)
            }

        // insert
        newItemsIdList
            .mapNotNull { id ->
                itemsMap[id]
            }
            .also {
                Log.v(LOG_TAG, "Insert entries:\n$it")
            }
            .forEach { item ->
                itemRepository.insertItem(item)
            }

        // result
        itemsMap = itemsMap
            .filter {  entry ->
                !deleteItemsIdList.contains(entry.key)
            }
            .toMutableMap()

        deleteItemsIdList.clear()
        updateItemsIdList.clear()
        newItemsIdList.clear()
        repositoryItemsHash = itemsMap.values.toList().hashCode()
        emitUpdates()
    }
    open suspend fun reset() {
        Log.v(LOG_TAG, "reset")

        newItemsIdList.clear()
        deleteItemsIdList.clear()
        updateItemsIdList.clear()
        itemsMap.clear()
        nextItemId = 0

        val items = itemRepository.getItems()

        if (items.isNotEmpty()) {
            itemsMap = items
                .associateBy { item ->
                    getItemId(item)
                }
                .toMutableMap()

            nextItemId = itemsMap.keys.max() + 1
        }

        repositoryItemsHash = items.toList().hashCode()

        emitUpdates()
    }
}

