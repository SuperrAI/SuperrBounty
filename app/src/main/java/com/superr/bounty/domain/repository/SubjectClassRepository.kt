package com.superr.bounty.domain.repository

import com.superr.bounty.domain.model.SubjectClass

private const val TAG = "Superr.Domain.Repository.SubjectClass"

interface SubjectClassRepository {
    suspend fun getSubjectClass(id: String): Result<SubjectClass>
    suspend fun createSubjectClass(aSubjectClass: SubjectClass): Result<Unit>
    suspend fun updateSubjectClass(aSubjectClass: SubjectClass): Result<Unit>
    suspend fun deleteSubjectClass(id: String): Result<Unit>
    suspend fun getSubjectClassesForTeacher(teacherId: String): Result<List<SubjectClass>>
    suspend fun getSubjectClassesForStudent(studentId: String): Result<List<SubjectClass>>
    suspend fun getSubjectClassesByGradeAndSubject(
        grade: Int,
        subject: String
    ): Result<List<SubjectClass>>
}