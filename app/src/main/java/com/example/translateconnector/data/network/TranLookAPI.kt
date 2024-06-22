package com.example.translateconnector.data.network;

import com.example.translateconnector.data.model.UserModel;
import kotlinx.coroutines.flow.Flow

import retrofit2.http.GET;

public interface TranLookAPI {
    @GET("/repos/square/retrofit/stargazers")
    suspend fun getUser(): Flow<UserModel?>
}
