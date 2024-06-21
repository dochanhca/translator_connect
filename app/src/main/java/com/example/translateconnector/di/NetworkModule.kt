package com.example.translateconnector.di

import com.example.translateconnector.data.network.TranLookAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val RETROFIT_TIMEOUT : Long = 60 * 1000

    @Singleton
    @Provides
    fun provideOkHttpClient() = run {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(RETROFIT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(RETROFIT_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(RETROFIT_TIMEOUT, TimeUnit.MILLISECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.github.com/")
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideTranLookAPI(retrofit: Retrofit): TranLookAPI =
        retrofit.create(TranLookAPI::class.java)
}