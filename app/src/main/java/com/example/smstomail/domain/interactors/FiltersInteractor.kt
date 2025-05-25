package com.example.smstomail.domain.interactors

import android.util.Log
import com.example.smstomail.data.entity.Filter
import com.example.smstomail.data.repository.IFiltersRepository
import javax.inject.Inject

class FiltersInteractor @Inject constructor(
    filtersRepository: IFiltersRepository
): AbstractItemListBufferInteractor<Filter>(filtersRepository) {
    override fun createNewItem(nextItemId: Int) = Filter(nextItemId)
    override fun getItemId(item: Filter) = item.id
    override fun isValidItems(): Boolean {
        return items.find { it.value.isEmpty() } == null
    }

    init {
        Log.v("init", "FiltersInteractor")
    }
}