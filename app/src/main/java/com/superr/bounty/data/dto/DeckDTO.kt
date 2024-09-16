package com.superr.bounty.data.dto

import com.google.firebase.Timestamp

private const val TAG = "Superr.DeckDTO"

data class DeckDTO(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val coverImage: String = "",
    val cardIds: List<String> = listOf(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)