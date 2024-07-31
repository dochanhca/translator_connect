package com.example.translateconnector.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.translateconnector.domain.entity.FirebaseAuthEntity
import com.example.translateconnector.domain.repository.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepositoryImpl @Inject constructor(private val firebaseAuth: FirebaseAuth) :
    LoginRepository {
    override suspend fun login(email: String, password: String): Flow<FirebaseAuthEntity> {
        return flow {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()

            if (authResult.user != null) {
                Log.d(TAG, "signInWithEmail:success")
                val user = authResult.user!!
                emit(
                    FirebaseAuthEntity(
                        userId = user.uid,
                        email = user.email ?: "",
                        isEmailVerified = user.isEmailVerified,
                        displayName = user.displayName,
                        photoUrl = user.photoUrl?.toString()
                    )
                )
            } else {
                throw Exception("Login failed")
            }
        }.catch { exception ->
            Log.e(TAG, "Login Failed: ${exception.message}")
            throw Exception(exception.message)
        }
    }
}