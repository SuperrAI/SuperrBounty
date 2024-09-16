package com.superr.bounty.domain.repository

import com.superr.bounty.domain.model.User

private const val TAG = "Superr.Domain.Repository.User"

interface UserRepository {
    suspend fun getUser(id: String): Result<User>
    suspend fun createUser(user: User): Result<Unit>
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun deleteUser(id: String): Result<Unit>
    suspend fun getAllTeachers(): Result<List<User>>
    suspend fun getStudentsInClass(classId: String): Result<List<User>>
    suspend fun loginUser(email: String, password: String): Result<User>

}