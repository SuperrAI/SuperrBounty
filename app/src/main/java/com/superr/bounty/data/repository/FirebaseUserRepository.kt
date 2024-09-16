package com.superr.bounty.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.superr.bounty.data.dto.UserDTO
import com.superr.bounty.data.mapper.UserMapper
import com.superr.bounty.domain.model.User
import com.superr.bounty.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await

private const val TAG = "Superr.FirebaseUserRepository"

class FirebaseUserRepository(
    firestore: FirebaseFirestore
) : UserRepository {
    private val usersCollection = firestore.collection("users")

    override suspend fun getUser(id: String): Result<User> {
        return try {
            val snapshot = usersCollection.document(id).get().await()
            val userDto = snapshot.toObject<UserDTO>()
            userDto?.let {
                Result.success(UserMapper.dtoToDomain(it))
            } ?: Result.failure(NoSuchElementException("User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createUser(user: User): Result<Unit> {
        return try {
            val userDto = UserMapper.domainToDto(user)
            usersCollection.document(user.id).set(userDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            val userDto = UserMapper.domainToDto(user)
            usersCollection.document(user.id).set(userDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(id: String): Result<Unit> {
        return try {
            usersCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllTeachers(): Result<List<User>> {
        return try {
            val snapshot = usersCollection.whereEqualTo("role", "TEACHER").get().await()
            val teachers =
                snapshot.toObjects(UserDTO::class.java).map { UserMapper.dtoToDomain(it) }
            Result.success(teachers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getStudentsInClass(classId: String): Result<List<User>> {
        return try {
            val snapshot = usersCollection.whereEqualTo("role", "STUDENT")
                .whereArrayContains("enrolledClasses", classId).get().await()
            val students =
                snapshot.toObjects(UserDTO::class.java).map { UserMapper.dtoToDomain(it) }
            Result.success(students)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val snapshot = usersCollection
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .await()

            if (snapshot.documents.isEmpty()) {
                Result.failure(Exception("Invalid email or password"))
            } else {
                val userDto = snapshot.documents.first().toObject<UserDTO>()
                userDto?.let {
                    Result.success(UserMapper.dtoToDomain(it))
                } ?: Result.failure(Exception("User data is invalid"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}