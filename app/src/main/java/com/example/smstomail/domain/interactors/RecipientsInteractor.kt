package com.example.smstomail.domain.interactors

import com.example.smstomail.data.entity.Recipient
import com.example.smstomail.data.repository.IRecipientsRepository
import javax.inject.Inject

class RecipientsInteractor @Inject constructor(
    recipientsRepository: IRecipientsRepository
): AbstractItemListBufferInteractor<Recipient>(recipientsRepository) {
    override fun createNewItem(nextItemId: Int) = Recipient(nextItemId)
    override fun getItemId(item: Recipient) = item.id
    override fun isValidItems(): Boolean {
        return items.let {
            it.isNotEmpty() && it.find {
                item -> !item.isValid
            } == null
        }
    }

    override suspend fun reset() {
        super.reset()
        if(items.isEmpty()) {
            createNewItem()
        }
    }
}