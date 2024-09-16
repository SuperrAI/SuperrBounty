package com.superr.bounty.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import kotlin.random.Random

private const val TAG = "Superr.Domain.Session"

// Domain Model
data class Session(
    val id: String,
    val classId: String,
    val subjectId: String,
    val title: String,
    val description: String?,
    val startTime: Date,
    val endTime: Date,
    val deckIds: List<String>,
    val presentationMode: PresentationMode,
    val timedDurationMinutes: Int?,
    val status: SessionStatus,
    val createdAt: Date,
    val updatedAt: Date,
    val code: String
) {
    fun verifyCode(inputCode: String): Boolean {
        return inputCode == code
    }

    companion object {
        fun generateCode(): String {
            return String.format("%06d", Random.nextInt(1000000))
        }
    }
}

enum class PresentationMode {
    INSTRUCTOR_LED, SELF_PACED, TIMED, DEFAULT
}

enum class SessionStatus {
    SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
}

// Parcelable Implementation
@Parcelize
data class ParcelableSession(
    val id: String,
    val classId: String,
    val subjectId: String,
    val title: String,
    val description: String?,
    val startTime: Long,
    val endTime: Long,
    val deckIds: List<String>,
    val presentationMode: ParcelablePresentationMode,
    val timedDurationMinutes: Int?,
    val status: ParcelableSessionStatus,
    val createdAt: Long,
    val updatedAt: Long,
    val code: String
) : Parcelable

@Parcelize
enum class ParcelablePresentationMode : Parcelable {
    INSTRUCTOR_LED, SELF_PACED, TIMED, DEFAULT
}

@Parcelize
enum class ParcelableSessionStatus : Parcelable {
    SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
}

// Conversion Functions
fun Session.toParcelable() = ParcelableSession(
    id,
    classId,
    subjectId,
    title,
    description,
    startTime.time,
    endTime.time,
    deckIds,
    ParcelablePresentationMode.valueOf(presentationMode.name),
    timedDurationMinutes,
    ParcelableSessionStatus.valueOf(status.name),
    createdAt.time,
    updatedAt.time,
    code
)

fun ParcelableSession.toSession() = Session(
    id,
    classId,
    subjectId,
    title,
    description,
    Date(startTime),
    Date(endTime),
    deckIds,
    PresentationMode.valueOf(presentationMode.name),
    timedDurationMinutes,
    SessionStatus.valueOf(status.name),
    Date(createdAt),
    Date(updatedAt),
    code
)