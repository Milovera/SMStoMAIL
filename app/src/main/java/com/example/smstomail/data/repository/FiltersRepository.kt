package com.example.smstomail.data.repository

import com.example.smstomail.data.database.dao.IFilterDao
import com.example.smstomail.data.entity.Filter

class FiltersRepository(
    private val filtersDao: IFilterDao
): IFiltersRepository {
    override suspend fun getItems(): List<Filter> {
        return filtersDao.getAllItems()
    }

    override suspend fun insertItem(item: Filter) {
        filtersDao.insert(item)
    }

    override suspend fun updateItem(item: Filter) {
        filtersDao.update(item)
    }

    override suspend fun deleteItem(itemId: Int) {
        filtersDao.delete(itemId)
    }
}