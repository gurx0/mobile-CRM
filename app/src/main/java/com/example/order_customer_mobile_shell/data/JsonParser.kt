package com.example.order_customer_mobile_shell.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonParser {

    // Парсинг списка клиентов из JSON
    fun parseClients(json: String): List<ClientRequest> {
        val gson = Gson()
        val responseType = object : TypeToken<Map<String, List<ClientRequest>>>() {}.type
        val response: Map<String, List<ClientRequest>> = gson.fromJson(json, responseType)
        return response["clients"] ?: emptyList()
    }

    // Парсинг списка заказов из JSON
    fun parseOrders(json: String): List<OrderRequest> {
        val gson = Gson()
        val responseType = object : TypeToken<Map<String, List<OrderRequest>>>() {}.type
        val response: Map<String, List<OrderRequest>> = gson.fromJson(json, responseType)
        return response["orders"] ?: emptyList()
    }
}
