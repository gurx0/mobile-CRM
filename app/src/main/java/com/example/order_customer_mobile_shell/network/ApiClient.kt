package com.example.order_customer_mobile_shell.network

import com.example.order_customer_mobile_shell.data.ClientRequest
import com.example.order_customer_mobile_shell.data.OrderEditRequest
import okhttp3.FormBody
import java.io.IOException

class ApiClient(private val authService: AuthService) {

    // Добавление клиента
    fun addClient(data: ClientRequest, callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                val params = mapOf(
                    "first_name" to data.first_name,
                    "last_name" to data.last_name,
                    "middle_name" to (data.middle_name ?: ""),
                    "mobile_phone" to (data.mobile_phone ?: ""),
                    "email" to data.email
                )
                executePostRequest("/api/clients/add/", params, accessToken, callback)
            } else {
                callback(false, "Failed to refresh token")
            }
        }
    }

    // Получение клиента по ID с фильтром
    fun getClientById(id: Int, search: String? = null, callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                val params = mutableMapOf("id" to id.toString())
                search?.let { params["search"] = it }
                executePostRequest("/api/clients/get/$id/", params, accessToken, callback)
            } else {
                callback(false, "Failed to refresh token")
            }
        }
    }

    // Получение списка клиентов с фильтром
    fun getClients(startId: Int, search: String? = null, callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                val params = mutableMapOf("start_id" to startId.toString())
                search?.let { params["search"] = it }
                executePostRequest("/api/clients/get/", params, accessToken, callback)
            } else {
                callback(false, "Failed to refresh token")
            }
        }
    }

    // Редактирование клиента
    fun editClient(id: Int, data: ClientRequest, callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                val params = mutableMapOf<String, String>()
                data.first_name.let { params["first_name"] = it }
                data.middle_name?.let { params["middle_name"] = it }
                data.last_name.let { params["last_name"] = it }
                data.email.let { params["email"] = it }
                data.mobile_phone?.let { params["mobile_phone"] = it }

                executePostRequest("/api/clients/edit/$id/", params, accessToken, callback)
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
        params: Map<String, String>,
        token: String?,
        callback: (Boolean, String?) -> Unit
    ) {
        if (token == null) {
            callback(false, "No access token provided")
            return
        }

        val formBodyBuilder = FormBody.Builder()
        params.forEach { (key, value) -> formBodyBuilder.add(key, value) }

        val request = okhttp3.Request.Builder()
            .url("http://95.164.3.6:8001$url")
            .addHeader("Authorization", "Bearer $token")
            .post(formBodyBuilder.build())
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
