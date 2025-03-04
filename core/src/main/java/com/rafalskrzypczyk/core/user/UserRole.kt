package com.rafalskrzypczyk.core.user

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole(val value: String) {
    ADMIN("Admin"),
    CREATOR("Creator"),
    GUEST("Guest"),
    USER("User"),
}