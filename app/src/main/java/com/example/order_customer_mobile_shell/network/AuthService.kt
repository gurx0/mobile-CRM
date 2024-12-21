package com.example.order_customer_mobile_shell.network

import com.example.order_customer_mobile_shell.data.LoginRequest
import com.example.order_customer_mobile_shell.data.TokenResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

class AuthService {
    val client: OkHttpClient
    val moshi: Moshi
    private var accessToken: String? = null
    private var refreshToken: String? = null

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        client = OkHttpClient.Builder()
            .cookieJar(CustomCookieJar())
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    // Авторизация
    fun authenticate(username: String, password: String, callback: (Boolean) -> Unit) {
        val loginRequest = LoginRequest(username, password)
        val json = moshi.adapter(LoginRequest::class.java).toJson(loginRequest)
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("http://95.164.3.6:8001/api/token/")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val tokenResponse = moshi.adapter(TokenResponse::class.java).fromJson(responseBody!!)
                    accessToken = tokenResponse?.access
                    refreshToken = tokenResponse?.refresh
                    callback(true)
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }
        })
    }

    // Обновление токена
    fun refreshToken(callback: (Boolean) -> Unit) {
        if (refreshToken == null) {
            callback(false)
            return
        }

        val requestBody = """{"refresh":"$refreshToken"}"""
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("http://95.164.3.6:8001/api/token/refresh/")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val tokenResponse = moshi.adapter(TokenResponse::class.java).fromJson(responseBody!!)
                    accessToken = tokenResponse?.access
                    callback(true)
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }
        })
    }

    // Проверка токенов
    fun ensureAccessTokenValid(callback: (Boolean) -> Unit) {
        if (accessToken == null) {
            refreshToken(callback)
        } else {
            callback(true)
        }
    }

    // Получение токенов
    fun getAccessToken(): String? = accessToken
}
