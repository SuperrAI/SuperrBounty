package com.superr.bounty.data.dto

import com.google.firebase.Timestamp
import com.superr.bounty.domain.model.SubjectClassSchedule

private const val TAG = "Superr.SubjectClassDTO"

data class SubjectClassDTO(
    val id: String = "",
    val grade: Int = 0,
    val section: String = "",
    val subject: String = "",
    val teacherId: String = "",
    val academicYear: String = "",
    val schedule: List<SubjectClassSchedule> = listOf(),
    val enrolledStudents: List<String> = listOf(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val isActive: Boolean = true,
    val description: String? = null,
    val syllabus: String? = null
)