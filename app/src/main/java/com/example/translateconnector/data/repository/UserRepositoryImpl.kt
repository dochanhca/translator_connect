package com.example.translateconnector.data.repository

import com.example.translateconnector.data.network.TranLookAPI
import com.example.translateconnector.domain.entity.UserEntity
import com.example.translateconnector.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(private val tranLookAPI: TranLookAPI) : UserRepository {
    override fun getUserDetail(userId: String): Flow<UserEntity?> {
        TODO("Not yet implemented")
    }
}