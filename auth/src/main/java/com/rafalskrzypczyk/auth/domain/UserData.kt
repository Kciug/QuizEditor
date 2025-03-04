package com.rafalskrzypczyk.auth.domain

data class UserData(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole?
)