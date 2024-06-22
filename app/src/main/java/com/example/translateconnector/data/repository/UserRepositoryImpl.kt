package com.example.translateconnector.data.repository

import com.example.translateconnector.data.model.toEntity
import com.example.translateconnector.data.network.CommonError
import com.example.translateconnector.data.network.TranLookAPI
import com.example.translateconnector.domain.entity.UserEntity
import com.example.translateconnector.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(private val tranLookAPI: TranLookAPI) :
    UserRepository {
    override suspend fun getUserDetail(userId: String): Flow<UserEntity?> {
        try {
            return tranLookAPI.getUser().map { it?.toEntity() }
        } catch (error: Throwable) {
            throw CommonError("load user failed", error)
        }
    }
}