package com.example.smstomail.domain.service

import com.example.smstomail.data.entity.Filter
import com.example.smstomail.data.entity.Message

sealed class FilterStrategy(val filterValue: String) {
    abstract fun pass(message: Message): Boolean

    class SenderInclude(filterValue: String): FilterStrategy(filterValue) {
        override fun pass(message: Message): Boolean {
            return message.sender.contains(filterValue, ignoreCase = true)
        }
    }

    class SenderExclude(filterValue: String): FilterStrategy(filterValue) {
        override fun pass(message: Message): Boolean {
            return !message.sender.contains(filterValue, ignoreCase = true)
        }
    }

    class MessageInclude(filterValue: String): FilterStrategy(filterValue) {
        override fun pass(message: Message): Boolean {
            return message.body.contains(filterValue, ignoreCase = true)
        }
    }

    class MessageExclude(filterValue: String): FilterStrategy(filterValue) {
        override fun pass(message: Message): Boolean {
            return !message.body.contains(filterValue, ignoreCase = true)
        }
    }
}

fun Filter.toStrategy(): FilterStrategy {
    return when(type) {
        Filter.Type.SenderInclude -> FilterStrategy.SenderInclude(value)
        Filter.Type.SenderExclude -> FilterStrategy.SenderExclude(value)
        Filter.Type.MessageInclude -> FilterStrategy.MessageInclude(value)
        Filter.Type.MessageExclude -> FilterStrategy.MessageExclude(value)
    }
}