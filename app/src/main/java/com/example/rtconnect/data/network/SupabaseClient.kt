package com.example.rtconnect.data.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object SupabaseClient {

    // Token JWT yang didapat setelah login berhasil
    // Digunakan untuk request yang memerlukan autentikasi user
    var userAccessToken: String? = null

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        // Gunakan JWT user jika tersedia, fallback ke anon key
        val bearerToken = userAccessToken ?: SupabaseConfig.API_KEY
        val authenticatedRequest = originalRequest.newBuilder()
            .header("apikey", SupabaseConfig.API_KEY)
            .header("Authorization", "Bearer $bearerToken")
            .header("Content-Type", "application/json")
            .build()
        chain.proceed(authenticatedRequest)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val apiService: SupabaseApiService by lazy {
        Retrofit.Builder()
            .baseUrl(SupabaseConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApiService::class.java)
    }
}
