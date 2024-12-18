

package com.example.order_customer_mobile_shell.network

import com.example.order_customer_mobile_shell.data.OrderRequest
import com.example.order_customer_mobile_shell.data.OrderEditRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ApiOrder(private val authService: AuthService) {

    // Добавление заказа
    fun addOrder(orderRequest: OrderRequest, callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                val json = authService.moshi.adapter(OrderRequest::class.java).toJson(orderRequest)
                executePostRequest("/api/orders/add/", json, accessToken, callback)
            } else {
                callback(false, "Failed to refresh token")
            }
        }
    }

    // Получение одного заказа по ID
    fun getOrderById(id: Int, callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                val json = """{"id":$id}"""
                executePostRequest("/api/orders/get/$id/", json, accessToken, callback)
            } else {
                callback(false, "Failed to refresh token")
            }
        }
    }

    // Получение списка заказов
    fun getOrders(startId: Int, callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                val json = """{"start_id":$startId}"""
                executePostRequest("/api/orders/get/", json, accessToken, callback)
            } else {
                callback(false, "Failed to refresh token")
            }
        }
    }

    // Редактирование заказа
    fun editOrder(id: Int, orderEditRequest: OrderEditRequest, callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                val json = authService.moshi.adapter(OrderEditRequest::class.java).toJson(orderEditRequest)
                executePostRequest("/api/orders/edit/$id/", json, accessToken, callback)
            } else {
                callback(false, "Failed to refresh token")
            }
        }
    }

    // Удаление заказа
    fun deleteOrder(id: Int, callback: (Boolean) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                executeDeleteRequest("/api/orders/delete/$id/", accessToken, callback)
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
            .url("http://127.0.0.1:8000$url")
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
            .url("http://127.0.0.1:8000$url")
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
