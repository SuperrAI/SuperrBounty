package com.superr.bounty.data.dto

import com.google.firebase.Timestamp

private const val TAG = "Superr.CardDTO"

data class CardDTO(
    val id: String = "",
    val type: String = "",
    val title: String = "",
    val description: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val content: Map<String, Any> = mapOf()
)