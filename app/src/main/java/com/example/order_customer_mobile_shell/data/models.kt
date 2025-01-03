package com.example.order_customer_mobile_shell.data

import java.time.Period

data class TokenResponse(
    val access: String,
    val refresh: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class ClientRequest(
    val first_name: String,
    val last_name: String,
    val middle_name: String?,
    val mobile_phone: String?,
    val email: String
)

data class OrderRequest(
    val client: ClientDetails,
    val product: String,
    val quantity: Int,
    val price: String,
    val total_price: String,
    val description: String,
    val status: String,
    val created_at: String
)

data class ClientDetails(
    val id: Int,
    val full_name: String,
    val email: String,
    val mobile_phone: String
)


data class OrderEditRequest(
    val product: String?,
    val quantity: Int?,
    val price: String?,
    val status: String?
)

//data class ClientEditRequest(
//    val first_name: String?,
//    val last_name: String?
//)

data class Report(
    val period: Period,
    val new_orders_count: String,
    val new_clients_count: String,
    val total_orders_sum: String,
    val avvg_order_sum: String,
    val complited_orders_count: String
)

