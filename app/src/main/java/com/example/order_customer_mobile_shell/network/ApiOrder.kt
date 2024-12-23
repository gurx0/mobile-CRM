

package com.example.order_customer_mobile_shell.network

import com.example.order_customer_mobile_shell.data.OrderRequest
import com.example.order_customer_mobile_shell.data.OrderEditRequest
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ApiOrder(private val authService: AuthService) {

    // Добавление заказа
    fun addOrder(data: OrderRequest, callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
//            if (isValid) {
//                val accessToken = authService.getAccessToken()
//                val params = mapOf(
//                    "client" to data.client.id,
//                    "product" to data.product,
//                    "quantity" to data.quantity,
//                    "price" to data.price,
//                    "total_price" to data.total_price,
//                    "description" to data.description,
//                    "status" to data.status,
//                    "created_at" to data.created_at
//                )
//                 executePostRequest("/api/orders/add/", params, accessToken, callback)
//            } else {
//                callback(false, "Failed to refresh tok en")
//            }
        }
    }

    // Получение одного заказа по ID
    fun getOrderById(id: Int, callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                val json = """{"id":$id}"""
//                executePostRequest("/api/orders/get/$id/", json, accessToken, callback)
            } else {
                callback(false, "Failed to refresh token")
            }
        }
    }

    // Получение списка заказов
    fun getOrders(startId: Int, search: String? = null,  callback: (Boolean, String?) -> Unit) {
        authService.ensureAccessTokenValid { isValid ->
            if (isValid) {
                val accessToken = authService.getAccessToken()
                val params = mutableMapOf("start_id" to startId.toString())
                search?.let { params["search"] = it }
                executePostRequest("/api/orders/get/", params, accessToken, callback)
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
//                executePostRequest("/api/orders/edit/$id/", json, accessToken, callback)
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
