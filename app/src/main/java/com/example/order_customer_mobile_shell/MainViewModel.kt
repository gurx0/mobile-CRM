package com.example.order_customer_mobile_shell

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.order_customer_mobile_shell.data.ClientRequest
import com.example.order_customer_mobile_shell.network.ApiClient
import com.example.order_customer_mobile_shell.network.AuthService
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val apiClient = ApiClient(authService = AuthService())

    
}