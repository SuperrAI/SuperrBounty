package com.superr.bounty.utils

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.superr.bounty.data.repository.KvRepository
import com.superr.bounty.domain.model.Kv
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

private const val TAG = "Superr.Utils.KvProxy"

class KvProxy(context: Context) {
    private val kvRepository = KvRepository(context)

    fun <T> observeKv(key: String, serializer: KSerializer<T>, default: T): LiveData<T?> {
        return kvRepository.getLive(key).map { kv ->
            kv?.let {
                Json.decodeFromString(serializer, it.value)
            } ?: default
        }
    }

    fun <T> get(key: String, serializer: KSerializer<T>): T? {
        val kv = kvRepository.get(key)
        return Json.decodeFromString(serializer, kv!!.value)
    }

    fun <T> setKv(key: String, value: T, serializer: KSerializer<T>) {
        val jsonValue = Json.encodeToString(serializer, value)
        Log.i(TAG, jsonValue)
        kvRepository.set(Kv(key, jsonValue))
    }
}