package com.rafalskrzypczyk.auth.domain

enum class UserRole(val value: String) {
    ADMIN("Admin"),
    CREATOR("Creator"),
    GUEST("Guest"),
    USER("User"),
}