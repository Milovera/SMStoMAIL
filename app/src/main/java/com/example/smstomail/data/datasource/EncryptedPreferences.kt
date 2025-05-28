package com.example.smstomail.data.datasource

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.security.crypto.MasterKeys.AES256_GCM_SPEC
import javax.inject.Inject

class EncryptedPreferences @Inject constructor(
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

    override fun read(key: String): String? {
        return try {
            sharedPreferences.getString(key, null)
        } catch (_: ClassCastException) {
            null
        }
    }

    override fun write(key: String, value: String) {
        sharedPreferences.edit {
                putString(key, value)
            }
    }
}

