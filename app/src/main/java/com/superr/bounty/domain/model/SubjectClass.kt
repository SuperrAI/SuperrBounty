package com.superr.bounty.domain.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import java.util.Date

private const val TAG = "Superr.Domain.SubjectClass"

data class SubjectClass(
    val id: String,
    val grade: Int,
    val section: String,
    val subject: String,
    val teacherId: String,
    val academicYear: String,
    val schedule: List<SubjectClassSchedule>,
    val enrolledStudents: List<String>,
    val createdAt: Date,
    val updatedAt: Date,
    val isActive: Boolean = true,
    val description: String? = null,
    val syllabus: String? = null
)

data class SubjectClassSchedule(
    @PropertyName("dayOfWeek") val dayOfWeek: Int = 0, // 1 (Monday) to 7 (Sunday)
    @PropertyName("startTime") val startTime: String = "", // HH:mm format
    @PropertyName("endTime") val endTime: String = "", // HH:mm format
    @PropertyName("roomNumber") val roomNumber: String? = null
) {
    constructor() : this(0, "", "", null)
}

@Parcelize
data class ParcelableSubjectClass(
    val id: String,
    val grade: Int,
    val section: String,
    val subject: String,
    val teacherId: String,
    val academicYear: String,
    val schedule: List<ParcelableSubjectClassSchedule>,
    val enrolledStudents: List<String>,
    val createdAt: Long,
    val updatedAt: Long,
    val isActive: Boolean,
    val description: String?,
    val syllabus: String?
) : Parcelable

@Parcelize
data class ParcelableSubjectClassSchedule(
    val dayOfWeek: Int, val startTime: String, val endTime: String, val roomNumber: String?
) : Parcelable

// Conversion Functions
fun SubjectClass.toParcelable() = ParcelableSubjectClass(
    id,
    grade,
    section,
    subject,
    teacherId,
    academicYear,
    schedule.map { it.toParcelable() },
    enrolledStudents,
    createdAt.time,
    updatedAt.time,
    isActive,
    description,
    syllabus
)

fun ParcelableSubjectClass.toSubjectClass() = SubjectClass(
    id,
    grade,
    section,
    subject,
    teacherId,
    academicYear,
    schedule.map { it.toSubjectClassSchedule() },
    enrolledStudents,
    Date(createdAt),
    Date(updatedAt),
    isActive,
    description,
    syllabus
)

fun SubjectClassSchedule.toParcelable() = ParcelableSubjectClassSchedule(
    dayOfWeek, startTime, endTime, roomNumber
)

fun ParcelableSubjectClassSchedule.toSubjectClassSchedule() = SubjectClassSchedule(
    dayOfWeek, startTime, endTime, roomNumber
)