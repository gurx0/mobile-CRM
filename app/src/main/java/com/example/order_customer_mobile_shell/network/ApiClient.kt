package com.example.order_customer_mobile_shell.network

import com.example.order_customer_mobile_shell.data.ClientRequest
import com.example.order_customer_mobile_shell.data.ClientEditRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ApiClient(private val authService: AuthService) {

    // Добавление клиента
    fun addClient(data: ClientRequest, callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                val json = authService.moshi.adapter(ClientRequest::class.java).toJson(data)
                executePostRequest("/api/clients/add/", json, accessToken, callback)
            } else {
                callback(false, "Failed to refresh token")
            }
        }
    }

    // Получение клиента по ID
    fun getClientById(id: Int, callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                val json = """{"id":$id}"""
                executePostRequest("/api/clients/get/$id/", json, accessToken, callback)
            } else {
                callback(false, "Failed to refresh token")
            }
        }
    }

    // Получение списка клиентов
    fun getClients(startId: Int, callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                val json = """{"start_id":$startId; "search":"1212"}"""
                executePostRequest("/api/clients/get/", json, accessToken, callback)
            } else {
                callback(false, "Failed to refresh token")
            }
        }
    }

    // Редактирование клиента
    fun editClient(id: Int, data: ClientEditRequest, callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                val json = authService.moshi.adapter(ClientEditRequest::class.java).toJson(data)
                executePostRequest("/api/clients/edit/$id/", json, accessToken, callback)
            } else {
                callback(false, "Failed to refresh token")
            }
        }
    }

    // Удаление клиента
    fun deleteClient(id: Int, callback: (Boolean) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                executeDeleteRequest("/api/clients/delete/$id/", accessToken, callback)
            } else {
                callback(false)
            }
        }
    }

    // Вспомогательные методы
    private fun executePostRequest(
        url: String,
        json: String,
        token: String?,
        callback: (Boolean, String?) -> Unit
    ) {
        if (token == null) {
            callback(false, "No access token provided")
            return
        }

        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = okhttp3.Request.Builder()
            .url("http://95.164.3.6:8001$url")
            .addHeader("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        authService.client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    callback(true, response.body?.string())
                } else {
                    callback(false, response.message)
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback(false, e.localizedMessage)
            }
        })
    }

    private fun executeDeleteRequest(url: String, token: String?, callback: (Boolean) -> Unit) {
        if (token == null) {
            callback(false)
            return
        }

        val request = okhttp3.Request.Builder()
            .url("http://95.164.3.6:8001$url")
            .addHeader("Authorization", "Bearer $token")
            .delete()
            .build()

        authService.client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                callback(response.isSuccessful)
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback(false)
            }
        })
    }
}
