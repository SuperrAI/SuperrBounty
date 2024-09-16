package com.superr.bounty.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

private const val TAG = "Superr.KvDTO"

@Entity(
    tableName = "kv"
)
data class KvDTO(
    @PrimaryKey val key: String,
    val value: String
)