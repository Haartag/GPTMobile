package com.llinsoft.gptmobile.domain

import android.content.Context

/**
 * Store API token in encrypted shared preferences.
 */
class SharedPreferencesManager(context: Context) {
    private val sharedPref = context.getSharedPreferences("encrypted_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        with(sharedPref.edit()) {
            putString("openai_token", token)
            apply()
        }
    }

    fun getToken(): String? {
        return sharedPref.getString("openai_token", null)
    }

    fun clearToken() {
        with(sharedPref.edit()) {
            remove("openai_token")
            apply()
        }
    }

}