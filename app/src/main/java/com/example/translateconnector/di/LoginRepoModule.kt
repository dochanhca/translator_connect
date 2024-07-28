package com.example.translateconnector.di

import com.example.translateconnector.data.repository.LoginRepositoryImpl
import com.example.translateconnector.domain.repository.LoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class LoginRepoModule {

    @Binds
    abstract fun bindLoginRepository(impl: LoginRepositoryImpl): LoginRepository
}