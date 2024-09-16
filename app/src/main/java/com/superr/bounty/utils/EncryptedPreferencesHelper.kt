package com.superr.bounty.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.superr.bounty.domain.model.User

private const val TAG = "Superr.EncryptedPreferencesHelper"

class EncryptedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences
    private val gson = Gson()

    init {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secure_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        private const val KEY_USER = "user_data"
    }

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit().putString(KEY_USER, userJson).apply()
    }

    fun getUser(): User {
        val userJson = sharedPreferences.getString(KEY_USER, null)
        return gson.fromJson(userJson, User::class.java)
    }

    fun clearUser() {
        sharedPreferences.edit().remove(KEY_USER).apply()
    }

    fun saveData(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun saveBooleanData(key: String, value: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    fun getBooleanData(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun getData(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun removeData(key: String) {
        with(sharedPreferences.edit()) {
            remove(key)
            apply()
        }
    }

    fun clearAllData() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}