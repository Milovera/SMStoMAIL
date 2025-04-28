package com.example.smstomail.domain.service

import com.example.smstomail.data.entity.Message
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FilterStrategiesTest {
    val message = Message("senderInclude", "bodyInclude")

    @Test
    fun senderInclude_validateSuccess() {
        val strategy = FilterStrategy.SenderInclude("sender")
        assert(strategy.pass(message))
    }

    @Test
    fun senderInclude_validateFail() {
        val strategy = FilterStrategy.SenderInclude("exclude")
        assertFalse(strategy.pass(message))
    }

    @Test
    fun senderExclude_validateSuccess() {
        val strategy = FilterStrategy.SenderExclude("exclude")
        assert(strategy.pass(message))
    }

    @Test
    fun senderExclude_validateFail() {
        val strategy = FilterStrategy.SenderExclude("sender")
        assertFalse(strategy.pass(message))
    }

    @Test
    fun messageInclude_validateSuccess() {
        val strategy = FilterStrategy.MessageInclude("body")
        assertTrue(strategy.pass(message))
    }

    @Test
    fun messageInclude_validateFail() {
        val strategy = FilterStrategy.MessageInclude("exclude")
        assertFalse(strategy.pass(message))
    }

    @Test
    fun messageExclude_validateSuccess() {
        val strategy = FilterStrategy.MessageExclude("exclude")
        assertTrue(strategy.pass(message))
    }

    @Test
    fun messageExclude_validateFail() {
        val strategy = FilterStrategy.MessageExclude("body")
        assertFalse(strategy.pass(message))
    }
}