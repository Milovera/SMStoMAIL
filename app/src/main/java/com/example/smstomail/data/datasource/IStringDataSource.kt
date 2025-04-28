package com.example.smstomail.data.datasource

interface IStringDataSource {
    fun read(key: String): String?
    fun write(key: String, value: String)
}