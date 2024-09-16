package com.superr.bounty.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.superr.bounty.data.AppDatabase
import com.superr.bounty.data.mapper.KvMapper
import com.superr.bounty.domain.model.Kv

private const val TAG = "Superr.KvRepository"

class KvRepository(context: Context) {
    private val kvDao = AppDatabase.getDatabase(context).kvDao()

    fun get(key: String): Kv? {
        val kvDto = kvDao.get(key)
        return kvDto?.let { KvMapper.mapToDomain(it) }
    }

    fun getLive(key: String): LiveData<Kv?> {
        return kvDao.getLive(key).map { it?.let { KvMapper.mapToDomain(it) } }
    }

    fun set(kv: Kv) {
        kvDao.set(KvMapper.mapToDto(kv))
    }

    fun delete(key: String) {
        kvDao.delete(key)
    }
}