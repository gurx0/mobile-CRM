package com.example.order_customer_mobile_shell

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.order_customer_mobile_shell.data.ProtectedDataRequest
import com.example.order_customer_mobile_shell.network.ApiClient
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var dataList = mutableStateListOf<String>() // Хранит данные из защищенного эндпоинта
        private set

    private val apiClient = ApiClient()

    // Функция аутентификации
    fun authenticate(username: String, password: String) {
        viewModelScope.launch {
            apiClient.authenticate(username, password) { success ->
                if (success) {
                    println("Authentication successful")
                } else {
                    println("Authentication failed")
                }
            }
        }
    }

    // Функция получения данных с сервера
    fun fetchData() {
        viewModelScope.launch {
            val requestData = ProtectedDataRequest(
                first_name = "Имя",
                last_name = "Фамилия",
                middle_name = "Отчество",
                mobile_phone = "88005553535",
                email = "example@email.com"
            )
            apiClient.makeProtectedRequest(requestData) { success, response ->
                if (success) {
                    println("Data fetched successfully: $response")
                    dataList.clear()
                    response?.let { dataList.add(it) }
                } else {
                    println("Failed to fetch data: $response")
                }
            }
        }
    }
}
