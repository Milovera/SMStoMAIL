package com.example.smstomail.data.repository

import android.util.Log
import com.example.smstomail.data.database.dao.IFilterDao
import com.example.smstomail.data.entity.Filter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FiltersRepository @Inject constructor(
    private val filtersDao: IFilterDao
): IFiltersRepository {
    init {
        Log.v("init", "FiltersRepository")
    }
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