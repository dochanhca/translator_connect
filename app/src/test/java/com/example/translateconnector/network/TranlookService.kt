package com.imoktranslator.network

import com.imoktranslator.model.TestItem
import com.imoktranslator.network.param.UserRegisterParam
import com.imoktranslator.network.response.IntroduceResponse
import com.imoktranslator.network.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TranlookService {
    @GET("posts/{number}")
    suspend fun testAPI(@Path("number") number: Int): Call<TestItem?>?

    @GET("tutorials")
    suspend fun fetchIntroduceData(): Call<IntroduceResponse?>?

    @POST("register")
    suspend fun register(@Body param: UserRegisterParam?): Call<RegisterResponse?>?
}