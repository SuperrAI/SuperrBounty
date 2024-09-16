package com.superr.bounty.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.superr.bounty.data.dto.KvDTO

private const val TAG = "Superr.KvDAO"

@Dao
interface KvDAO {
    @Query("SELECT * FROM kv WHERE `key` = :key")
    fun get(key: String): KvDTO?

    @Query("SELECT * FROM kv WHERE `key` = :key")
    fun getLive(key: String): LiveData<KvDTO?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun set(kv: KvDTO)

    @Query("DELETE FROM kv WHERE `key` = :key")
    fun delete(key: String)
}