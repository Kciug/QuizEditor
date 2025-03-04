package com.rafalskrzypczyk.auth.data

import com.rafalskrzypczyk.core.user.UserData
import com.rafalskrzypczyk.core.user.UserRole
import com.rafalskrzypczyk.firestore.data.models.UserDataDTO

fun UserDataDTO.toDomain(email: String) = UserData(
    id = id,
    email = email,
    name = name,
    role = role.toUserRole()
)

fun String.toUserRole() : UserRole {
    return UserRole.entries.find { it.value == this } ?: UserRole.USER
}