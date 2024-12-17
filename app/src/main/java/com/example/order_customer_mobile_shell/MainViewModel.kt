package com.example.order_customer_mobile_shell

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.order_customer_mobile_shell.data.ClientRequest
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

    // Функция добавления клиента
    fun addClient(clientRequest: ClientRequest) {
        viewModelScope.launch {
            apiClient.addClient(clientRequest) { success, response ->
                if (success) {
                    println("Client added successfully: $response")
                } else {
                    println("Failed to add client: $response")
                }
            }
        }
    }

    // Функция получения клиента по ID
    fun getClientById(id: Int) {
        viewModelScope.launch {
            apiClient.getClient(id) { success, response ->
                if (success) {
                    println("Client fetched successfully: $response")
                    response?.let { dataList.add(it) }
                } else {
                    println("Failed to fetch client: $response")
                }
            }
        }
    }

    // Функция удаления клиента
    fun deleteClient(id: Int) {
        viewModelScope.launch {
            apiClient.deleteClient(id) { success ->
                if (success) {
                    println("Client deleted successfully")
                } else {
                    println("Failed to delete client")
                }
            }
        }
    }
}
