package com.example.order_customer_mobile_shell.data

data class TokenResponse(val access: String, val refresh: String)

data class LoginRequest(val username: String, val password: String)

data class ProtectedDataRequest(
    val first_name: String,
    val last_name: String,
    val middle_name: String,
    val mobile_phone: String?,
    val email: String
)
