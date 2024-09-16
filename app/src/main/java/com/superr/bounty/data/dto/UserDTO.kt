package com.superr.bounty.data.dto

import java.util.Date

private const val TAG = "Superr.UserDTO"

data class UserDTO(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = "",
    val createdAt: Date = Date(),
    val lastLogin: Date = Date(),
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    val grade: Int? = null,
    val enrolledClasses: List<String>? = null,
    val subjects: List<String>? = null,
    val teachingClasses: List<String>? = null
)