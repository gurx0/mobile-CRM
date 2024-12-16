package com.example.order_customer_mobile_shell.network

import com.example.order_customer_mobile_shell.data.LoginRequest
import com.example.order_customer_mobile_shell.data.ProtectedDataRequest
import com.example.order_customer_mobile_shell.data.TokenResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class ApiClient {
    private val client: OkHttpClient
    private val moshi: Moshi
    private var accessToken: String? = null
    private var refreshToken: String? = null

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    // Authentication
    fun authenticate(username: String, password: String, callback: (Boolean) -> Unit) {
        val loginRequest = LoginRequest(username, password)
        val json = moshi.adapter(LoginRequest::class.java).toJson(loginRequest)
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

        val request = Request.Builder()
            .url("http://127.0.0.1:8000/api/token/")
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

    // Refresh Token
    private fun refreshToken(callback: (Boolean) -> Unit) {
        if (refreshToken == null) {
            callback(false)
            return
        }

        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(),
            """{"refresh":"$refreshToken"}"""
        )

        val request = Request.Builder()
            .url("http://127.0.0.1:8000/api/token/refresh/")
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

    fun makeProtectedRequest(data: ProtectedDataRequest, callback: (Boolean, String?) -> Unit) {
        if (accessToken == null) {
            callback(false, "No Access Token")
            return
        }

        val json = moshi.adapter(ProtectedDataRequest::class.java).toJson(data)
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

        val request = Request.Builder()
            .url("http://127.0.0.1:8000/api/clients/add/")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true, response.body?.string())
                } else if (response.code == 401) {
                    refreshToken { success ->
                        if (success) makeProtectedRequest(data, callback)
                        else callback(false, "Token refresh failed")
                    }
                } else {
                    callback(false, "Request failed: ${response.code}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.localizedMessage)
            }
        })
    }
}
