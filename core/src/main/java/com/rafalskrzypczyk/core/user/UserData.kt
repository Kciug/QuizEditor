package com.rafalskrzypczyk.core.user

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole?
)