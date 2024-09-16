package com.superr.bounty.data.mapper

import com.google.firebase.Timestamp
import com.superr.bounty.data.dto.SubjectClassDTO
import com.superr.bounty.domain.model.SubjectClass

private const val TAG = "Superr.SubjectClassMapper"

object SubjectClassMapper {
    fun dtoToDomain(dto: SubjectClassDTO): SubjectClass {
        return SubjectClass(
            id = dto.id,
            grade = dto.grade,
            section = dto.section,
            subject = dto.subject,
            teacherId = dto.teacherId,
            academicYear = dto.academicYear,
            schedule = dto.schedule,
            enrolledStudents = dto.enrolledStudents,
            createdAt = dto.createdAt.toDate(),
            updatedAt = dto.updatedAt.toDate(),
            isActive = dto.isActive,
            description = dto.description,
            syllabus = dto.syllabus
        )
    }

    fun domainToDto(aSubjectClass: SubjectClass): SubjectClassDTO {
        return SubjectClassDTO(
            id = aSubjectClass.id,
            grade = aSubjectClass.grade,
            section = aSubjectClass.section,
            subject = aSubjectClass.subject,
            teacherId = aSubjectClass.teacherId,
            academicYear = aSubjectClass.academicYear,
            schedule = aSubjectClass.schedule,
            enrolledStudents = aSubjectClass.enrolledStudents,
            createdAt = Timestamp(aSubjectClass.createdAt),
            updatedAt = Timestamp(aSubjectClass.updatedAt),
            isActive = aSubjectClass.isActive,
            description = aSubjectClass.description,
            syllabus = aSubjectClass.syllabus
        )
    }
}