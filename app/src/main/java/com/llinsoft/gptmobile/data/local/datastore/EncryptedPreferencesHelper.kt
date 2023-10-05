package com.llinsoft.gptmobile.data.local.datastore

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

// in xml/backup_rules add <exclude domain="sharedpref" path="encrypted_preferences.xml"/>
/**
 * Class for Encrypted Preferences.
 * @property getPreference
 * @property putPreference
 * @property removePreference
 * @property clearAllPreferences
 */
class EncryptedPreferencesHelper(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val keyEncryptionScheme = EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
    private val valueEncryptionScheme = EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "encrypted_preferences",
        masterKey,
        keyEncryptionScheme,
        valueEncryptionScheme
    )
    fun <T> getPreference(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue as String) as T
            is Int -> sharedPreferences.getInt(key, defaultValue as Int) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue as Float) as T
            is Long -> sharedPreferences.getLong(key, defaultValue as Long) as T
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    fun <T> putPreference(key: String, value: T) {
        with(sharedPreferences.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
            apply()
        }
    }

    fun removePreference(key: String) {
        with(sharedPreferences.edit()) {
            remove(key)
            apply()
        }
    }

    fun clearAllPreferences() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}