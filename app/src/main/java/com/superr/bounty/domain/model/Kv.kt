package com.superr.bounty.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

private const val TAG = "Superr.Model.Kv"

data class Kv(
    val key: String, val value: String
)

@Parcelize
data class ParcelableKv(
    val key: String, val value: String
) : Parcelable

fun Kv.toParcelable() = ParcelableKv(key, value)

fun ParcelableKv.toKv() = Kv(key, value)