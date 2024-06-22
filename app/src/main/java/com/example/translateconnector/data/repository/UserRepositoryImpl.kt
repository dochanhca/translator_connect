package com.example.translateconnector.data.repository

import com.example.translateconnector.data.model.toEntity
import com.example.translateconnector.data.network.CommonError
import com.example.translateconnector.data.network.TranLookAPI
import com.example.translateconnector.domain.entity.UserEntity
import com.example.translateconnector.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(private val tranLookAPI: TranLookAPI) :
    UserRepository {
    override suspend fun getUserDetail(userId: String): Flow<UserEntity> {
        return flow {
            emit(tranLookAPI.getUsers())
        }.map { it.first().toEntity() }
            .flowOn(Dispatchers.IO)
            .catch { exception ->
                throw CommonError("load user failed", exception)
            }
    }
}