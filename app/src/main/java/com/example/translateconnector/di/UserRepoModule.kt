package com.example.translateconnector.di

import com.example.translateconnector.data.repository.UserRepositoryImpl
import com.example.translateconnector.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class UserRepoModule {

    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}