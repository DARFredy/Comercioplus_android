package com.example.comercioplus

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    const val BASE_DOMAIN = "https://comercioplusoficial-production-d61e.up.railway.app"
    private const val BASE_URL = "$BASE_DOMAIN/api/"

    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestUrl = originalRequest.url.toString()
        
        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/json")
            .header("X-Requested-With", "XMLHttpRequest")
            .header("User-Agent", "Comercioplus-Android-App")

        // ANULACIÓN TOTAL DE CACHÉ para productos y tienda
        if (requestUrl.contains("store") || requestUrl.contains("product")) {
            requestBuilder.header("Cache-Control", "no-cache, no-store, must-revalidate")
            requestBuilder.header("Pragma", "no-cache")
            requestBuilder.header("Expires", "0")
        }

        if (!requestUrl.contains("login") && !requestUrl.contains("register")) {
            val token = SessionManager.token.value
            if (token != null) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
        }
        
        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
