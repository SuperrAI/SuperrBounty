package com.superr.bounty.data.dto

import com.google.firebase.Timestamp

private const val TAG = "Superr.SessionDTO"

data class SessionDTO(
    val id: String = "",
    val classId: String = "",
    val subjectId: String = "",
    val title: String = "",
    val description: String? = null,
    val startTime: Timestamp = Timestamp.now(),
    val endTime: Timestamp = Timestamp.now(),
    val deckIds: List<String> = listOf(),
    val presentationMode: String = "",
    val timedDurationMinutes: Int? = null,
    val status: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val code: String = ""
)