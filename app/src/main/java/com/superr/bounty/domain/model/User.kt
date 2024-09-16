package com.superr.bounty.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

private const val TAG = "Superr.Domain.User"

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole,
    val createdAt: Date,
    val lastLogin: Date,
    val profilePictureUrl: String? = null,
    val bio: String? = null,
    // Student-specific fields
    val grade: Int? = null,
    val enrolledClasses: List<String>? = null,
    // Teacher-specific fields
    val subjects: List<String>? = null,
    val teachingClasses: List<String>? = null
)

enum class UserRole {
    STUDENT, TEACHER
}

@Parcelize
data class ParcelableUser(
    val id: String,
    val email: String,
    val name: String,
    val role: ParcelableUserRole,
    val createdAt: Long,
    val lastLogin: Long,
    val profilePictureUrl: String?,
    val bio: String?,
    // Student-specific fields
    val grade: Int?,
    val enrolledClasses: List<String>?,
    // Teacher-specific fields
    val subjects: List<String>?,
    val teachingClasses: List<String>?
) : Parcelable

@Parcelize
enum class ParcelableUserRole : Parcelable {
    STUDENT, TEACHER
}

// Conversion Functions
fun User.toParcelable() = ParcelableUser(
    id,
    email,
    name,
    ParcelableUserRole.valueOf(role.name),
    createdAt.time,
    lastLogin.time,
    profilePictureUrl,
    bio,
    grade,
    enrolledClasses,
    subjects,
    teachingClasses
)

fun ParcelableUser.toUser() = User(
    id,
    email,
    name,
    UserRole.valueOf(role.name),
    Date(createdAt),
    Date(lastLogin),
    profilePictureUrl,
    bio,
    grade,
    enrolledClasses,
    subjects,
    teachingClasses
)