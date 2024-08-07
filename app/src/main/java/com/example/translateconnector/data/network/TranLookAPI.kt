package com.example.translateconnector.data.network;

import com.example.translateconnector.data.model.UserModel;

import retrofit2.http.GET;

interface TranLookAPI {
    @GET("/repos/square/retrofit/stargazers")
    suspend fun getUsers(): List<UserModel>
}
