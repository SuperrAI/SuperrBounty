package com.superr.bounty.data.mapper

import com.superr.bounty.data.dto.UserDTO
import com.superr.bounty.domain.model.User
import com.superr.bounty.domain.model.UserRole

private const val TAG = "Superr.UserMapper"

object UserMapper {
    fun dtoToDomain(dto: UserDTO): User {
        return User(
            id = dto.id,
            email = dto.email,
            name = dto.name,
            role = UserRole.valueOf(dto.role),
            createdAt = dto.createdAt,
            lastLogin = dto.lastLogin,
            profilePictureUrl = dto.profilePictureUrl,
            bio = dto.bio,
            grade = dto.grade,
            enrolledClasses = dto.enrolledClasses,
            subjects = dto.subjects,
            teachingClasses = dto.teachingClasses
        )
    }

    fun domainToDto(user: User): UserDTO {
        return UserDTO(
            id = user.id,
            email = user.email,
            name = user.name,
            role = user.role.name,
            createdAt = user.createdAt,
            lastLogin = user.lastLogin,
            profilePictureUrl = user.profilePictureUrl,
            bio = user.bio,
            grade = user.grade,
            enrolledClasses = user.enrolledClasses,
            subjects = user.subjects,
            teachingClasses = user.teachingClasses
        )
    }
}