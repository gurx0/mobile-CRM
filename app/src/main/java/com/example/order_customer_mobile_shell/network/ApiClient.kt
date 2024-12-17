package com.example.order_customer_mobile_shell.network

import com.example.order_customer_mobile_shell.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
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

    // Авторизация
    fun authenticate(username: String, password: String, callback: (Boolean) -> Unit) {
        val loginRequest = LoginRequest(username, password)
        val json = moshi.adapter(LoginRequest::class.java).toJson(loginRequest)
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

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

    // Обновление токена
    private fun refreshToken(callback: (Boolean) -> Unit) {
        if (refreshToken == null) {
            callback(false)
            return
        }

        val requestBody = """{"refresh":"$refreshToken"}"""
            .toRequestBody("application/json".toMediaTypeOrNull())

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

    // Вспомогательный метод для проверки токенов
    private fun ensureAccessTokenValid(callback: (Boolean) -> Unit) {
        if (accessToken == null) {
            refreshToken(callback)
        } else {
            callback(true)
        }
    }

    // Запросы для работы с клиентами
    fun addClient(data: ClientRequest, callback: (Boolean, String?) -> Unit) {
        ensureAccessTokenValid { isValid ->
            if (isValid) {
                val json = moshi.adapter(ClientRequest::class.java).toJson(data)
                executePostRequest("/api/clients/add/", json, callback)
            } else {
                callback(false, "Failed to refresh token")
            }
        }
    }

    fun getClient(id: Int, callback: (Boolean, String?) -> Unit) {
        ensureAccessTokenValid { isValid ->
            if (isValid) {
                executeGetRequest("/api/clients/get/$id/", callback)
            } else {
                callback(false, "Failed to refresh token")
            }
        }
    }

    fun deleteClient(id: Int, callback: (Boolean) -> Unit) {
        ensureAccessTokenValid { isValid ->
            if (isValid) {
                executeDeleteRequest("/api/clients/delete/$id/", callback)
            } else {
                callback(false)
            }
        }
    }

    // Вспомогательный метод для POST запросов
    private fun executePostRequest(url: String, json: String, callback: (Boolean, String?) -> Unit) {
        if (accessToken == null) {
            callback(false, "No Access Token")
            return
        }

        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("http://127.0.0.1:8000$url")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true, response.body?.string())
                } else {
                    callback(false, "Request failed: ${response.code}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.localizedMessage)
            }
        })
    }

    // GET запросы
    private fun executeGetRequest(url: String, callback: (Boolean, String?) -> Unit) {
        if (accessToken == null) {
            callback(false, "No Access Token")
            return
        }

        val request = Request.Builder()
            .url("http://127.0.0.1:8000$url")
            .addHeader("Authorization", "Bearer $accessToken")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback(true, response.body?.string())
                } else {
                    callback(false, "Request failed: ${response.code}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.localizedMessage)
            }
        })
    }

    // DELETE запросы
    private fun executeDeleteRequest(url: String, callback: (Boolean) -> Unit) {
        if (accessToken == null) {
            callback(false)
            return
        }

        val request = Request.Builder()
            .url("http://127.0.0.1:8000$url")
            .addHeader("Authorization", "Bearer $accessToken")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful)
            }

            override fun onFailure(call: Call, e: IOException) {
                callback(false)
            }
        })
    }
}
