package com.example.order_customer_mobile_shell

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import com.example.order_customer_mobile_shell.components.AddClientDialog
import com.example.order_customer_mobile_shell.components.AddOrderDialog
import com.example.order_customer_mobile_shell.components.ClientTable
import com.example.order_customer_mobile_shell.components.OrderTable
import com.example.order_customer_mobile_shell.data.ClientRequest
import com.example.order_customer_mobile_shell.data.JsonParser.parseClients
import com.example.order_customer_mobile_shell.data.JsonParser.parseOrders
import com.example.order_customer_mobile_shell.data.OrderRequest
import com.example.order_customer_mobile_shell.network.ApiClient
import com.example.order_customer_mobile_shell.network.ApiOrder
import com.example.order_customer_mobile_shell.network.AuthService
import kotlinx.coroutines.launch




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authService: AuthService,
    apiClient: ApiClient = ApiClient(authService),
    apiOrder: ApiOrder = ApiOrder(authService)
) {
    var query by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var currentTable by remember { mutableStateOf("CLIENTS") }
    var clientData by remember { mutableStateOf(listOf<ClientRequest>()) }
    var orderData by remember { mutableStateOf(listOf<OrderRequest>()) }

    val scope = rememberCoroutineScope()
    var start_id by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color.White.copy(alpha = 0f),
                                    shape = RoundedCornerShape(30.dp)
                                )
                                .padding(horizontal = 0.dp, vertical = 4.dp)
                                .padding(end = 15.dp)
                                .border(2.dp, Color.White, RoundedCornerShape(30.dp)),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { showAddDialog = true },
                                modifier = Modifier.padding(end = 5.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }

                            BasicTextField(
                                value = query,
                                onValueChange = { query = it },
                                textStyle = TextStyle(fontSize = 16.sp, color = Color.White),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            )

                            IconButton(
                                onClick = {
                                    currentTable = "CLIENTS";
                                    start_id = 0 ;
                                    Log.d("table", "start_id: $start_id") }) {
                                Icon(Icons.Default.Person, contentDescription = "ClientsTable")
                            }
                            IconButton(
                                onClick = {
                                    currentTable = "ORDERS";
                                    start_id = 0 ;
                                    Log.d("table", "start_id: $start_id")}) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "OrdersTable")
                            }
                        }


                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EA),
                    titleContentColor = Color.White
                )
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White)
            ) {
                when (currentTable) {
                    "CLIENTS" -> ClientTable(clientData)
                    "ORDERS" -> OrderTable(orderData)
                }

                FloatingActionButton(
                    onClick = {
                        when (currentTable) {
                            "CLIENTS" -> {
                                scope.launch {
                                    authService.authenticate("sanya", "mobile-api123") { authSuccess ->
                                        if (authSuccess) {
                                            apiClient.getClients(start_id, query) { success, response ->
                                                if (success && response != null) {
                                                    clientData = try {
                                                        parseClients(response)
                                                    } catch (e: Exception){
                                                        emptyList()
                                                    }
                                                    start_id += 50
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            "ORDERS" -> {
                                scope.launch {
                                    authService.authenticate("sanya", "mobile-api123") { authSuccess ->
                                        if (authSuccess) {
                                            apiOrder.getOrders(start_id, query) { success, response ->
                                                if (success && response != null) {
                                                    orderData = try {
                                                        parseOrders(response)
                                                    } catch (e: Exception){
                                                        emptyList()
                                                    }
                                                    start_id += 50
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh Data")
                }

                if (showAddDialog) {
                    when (currentTable) {
                        "CLIENTS" -> AddClientDialog(
                            onDismiss = { showAddDialog = false },
                            onAddClient = { newClient ->
                                Log.d("Main Screen", "attempting add client: $newClient")
                                apiClient.addClient(newClient) { success, _ ->
                                    Log.d("Main Screen", "client added successfully")
                                    if (success) {
                                        Log.d("Main", " $success")
                                        showAddDialog = false

                                        Log.d("Main", "window is closed - $showAddDialog")
                                        apiClient.getClients(start_id, query) { success, response ->
                                            if (success && response != null) {
                                                clientData = parseClients(response)
                                            }
                                        }
                                    }
                                }
                            }
                        )
                        "ORDERS" -> AddOrderDialog(
                            onDismiss = { showAddDialog = false },
                            onAddOrder = { newOrder ->
                                apiOrder.addOrder(newOrder) { success, _ ->
                                    Log.d("api order", "add request")
                                    if (success) {
                                        Log.d("api order", "add successfull")
                                        showAddDialog = false
                                        apiOrder.getOrders(start_id, query) { success, response ->
                                            if (success && response != null) {
                                                orderData = parseOrders(response)
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}

