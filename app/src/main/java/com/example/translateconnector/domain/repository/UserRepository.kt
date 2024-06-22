package com.example.translateconnector.domain.repository

import com.example.translateconnector.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserDetail(userId: String): Flow<UserEntity>
}