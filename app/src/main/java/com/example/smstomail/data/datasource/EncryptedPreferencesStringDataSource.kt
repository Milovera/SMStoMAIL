package com.example.smstomail.data.datasource

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.security.crypto.MasterKeys.AES256_GCM_SPEC

class EncryptedPreferencesStringDataSource(
    context: Context
): IStringDataSource {
    companion object {
        const val APPLICATION_PREFERENCES_FILENAME = "SMStoMailApplicationPreferences"
    }

    private val masterKey = MasterKeys.getOrCreate(AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        APPLICATION_PREFERENCES_FILENAME,
        masterKey,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun read(key: String) = sharedPreferences.getString(key, null)

    override fun write(key: String, value: String) {
        sharedPreferences
            .edit()
            .putString(key, value)
            .apply()
    }
}

