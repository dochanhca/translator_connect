package com.example.translateconnector.domain.repository

import com.example.translateconnector.domain.entity.FirebaseAuthEntity
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun login(email: String, password: String): Flow<FirebaseAuthEntity>
}