package com.example.translateconnector.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseAuthModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        val firebaseAuth = Firebase.auth
        return firebaseAuth
    }
}