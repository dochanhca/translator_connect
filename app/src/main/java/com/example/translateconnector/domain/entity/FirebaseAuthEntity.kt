package com.example.translateconnector.domain.entity

data class FirebaseAuthEntity(
    val userId : String,
    val email: String,
    val isEmailVerified: Boolean?,
    val photoUrl: String?,
    val displayName : String?,
)
