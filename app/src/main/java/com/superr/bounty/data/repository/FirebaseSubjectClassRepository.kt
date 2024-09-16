package com.superr.bounty.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.superr.bounty.data.dto.SubjectClassDTO
import com.superr.bounty.data.mapper.SubjectClassMapper
import com.superr.bounty.domain.model.SubjectClass
import com.superr.bounty.domain.repository.SubjectClassRepository
import kotlinx.coroutines.tasks.await
import java.util.Date

private const val TAG = "Superr.FirebaseSubjectClassRepository"

class FirebaseSubjectClassRepository(private val firestore: FirebaseFirestore) :
    SubjectClassRepository {
    private val subjectClassesCollection = firestore.collection("classes")

    override suspend fun getSubjectClass(id: String): Result<SubjectClass> {
        return try {
            val snapshot = subjectClassesCollection.document(id).get().await()
            val subjectClassDto = snapshot.toObject<SubjectClassDTO>()
            subjectClassDto?.let {
                Result.success(SubjectClassMapper.dtoToDomain(it))
            } ?: Result.failure(NoSuchElementException("SubjectClass not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createSubjectClass(aSubjectClass: SubjectClass): Result<Unit> {
        return try {
            val classesDto = SubjectClassMapper.domainToDto(aSubjectClass)
            subjectClassesCollection.document(aSubjectClass.id).set(classesDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSubjectClass(aSubjectClass: SubjectClass): Result<Unit> {
        return try {
            val classesDto = SubjectClassMapper.domainToDto(aSubjectClass.copy(updatedAt = Date()))
            subjectClassesCollection.document(aSubjectClass.id).set(classesDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSubjectClass(id: String): Result<Unit> {
        return try {
            subjectClassesCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSubjectClassesForTeacher(teacherId: String): Result<List<SubjectClass>> {
        return try {
            val snapshot = subjectClassesCollection.whereEqualTo("teacherId", teacherId)
                .whereEqualTo("isActive", true).get().await()
            val classes = snapshot.toObjects(SubjectClassDTO::class.java)
                .map { SubjectClassMapper.dtoToDomain(it) }
            Result.success(classes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSubjectClassesForStudent(studentId: String): Result<List<SubjectClass>> {
        return try {
            val snapshot =
                subjectClassesCollection.whereArrayContains("enrolledStudents", studentId)
                    .whereEqualTo("isActive", true).get().await()

            Log.i(TAG, "getSubjectClassesForStudent: ${snapshot.size()}")
            val classes = snapshot.toObjects(SubjectClassDTO::class.java)
                .map { SubjectClassMapper.dtoToDomain(it) }
            Result.success(classes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSubjectClassesByGradeAndSubject(
        grade: Int,
        subject: String
    ): Result<List<SubjectClass>> {
        return try {
            val snapshot = subjectClassesCollection.whereEqualTo("grade", grade)
                .whereEqualTo("subject", subject).whereEqualTo("isActive", true).get().await()
            val classes = snapshot.toObjects(SubjectClassDTO::class.java)
                .map { SubjectClassMapper.dtoToDomain(it) }
            Result.success(classes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}