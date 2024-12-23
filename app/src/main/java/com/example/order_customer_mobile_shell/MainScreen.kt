package com.example.order_customer_mobile_shell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.order_customer_mobile_shell.data.ClientRequest
import com.example.order_customer_mobile_shell.data.JsonParser.parseClients
import com.example.order_customer_mobile_shell.data.JsonParser.parseOrders
import com.example.order_customer_mobile_shell.data.OrderRequest
import com.example.order_customer_mobile_shell.network.ApiClient
import com.example.order_customer_mobile_shell.network.ApiOrder
import com.example.order_customer_mobile_shell.network.AuthService
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign



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
                                onClick = { currentTable = "CLIENTS" }) {
                                Icon(Icons.Default.Person, contentDescription = "ClientsTable")
                            }
                            IconButton(
                                onClick = { currentTable = "ORDERS" }) {
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
                                            apiClient.getClients(0, query) { success, response ->
                                                if (success && response != null) {
                                                    clientData = try {
                                                        parseClients(response)
                                                    } catch (e: Exception){
                                                        emptyList()
                                                    }
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
                                            apiOrder.getOrders(0) { success, response ->
                                                if (success && response != null) {
                                                    orderData = try {
                                                        parseOrders(response)
                                                    } catch (e: Exception){
                                                        emptyList()
                                                    }
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
                                apiClient.addClient(newClient) { success, _ ->
                                    if (success) {
                                        showAddDialog = false
                                        apiClient.getClients(0, query) { success, response ->
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
                                    if (success) {
                                        showAddDialog = false
                                        apiOrder.getOrders(0) { success, response ->
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



@Composable
fun ClientTable(clients: List<ClientRequest>) {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(horizontalScrollState)
            .verticalScroll(verticalScrollState)
            .padding(16.dp)
    ) {
        Column {
            // Заголовок таблицы
            Row(
                modifier = Modifier
                    .background(Color.LightGray)
                    .padding(8.dp)
            ) {
                Text("First Name", modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center)
                Text("Last Name", modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center)
                Text("Email", modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center)
                Text("Phone", modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center)
            }

            // Строки данных
            clients.forEach { client ->
                Row(
                    modifier = Modifier
                        .border(1.dp, Color.Gray)
                        .padding(8.dp)
                ) {
                    Text(client.first_name, modifier = Modifier.padding(8.dp))
                    Text(client.last_name, modifier = Modifier.padding(8.dp))
                    Text(client.email, modifier = Modifier.padding(8.dp))
                    Text(client.mobile_phone ?: "N/A", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
fun OrderTable(orders: List<OrderRequest>) {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(horizontalScrollState)
            .verticalScroll(verticalScrollState)
            .padding(16.dp)
    ) {
        Column {
            // Заголовок таблицы
            Row(
                modifier = Modifier
                    .background(Color.LightGray)
                    .padding(8.dp)
            ) {
                Text("Client", modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center)
                Text("Product", modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center)
                Text("Quantity", modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center)
                Text("Total Price", modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center)
            }

            // Строки данных
            orders.forEach { order ->
                Row(
                    modifier = Modifier
                        .border(1.dp, Color.Gray)
                        .padding(8.dp)
                ) {
                    Text(order.client.toString(), modifier = Modifier.padding(8.dp))
                    Text(order.product, modifier = Modifier.padding(8.dp))
                    Text(order.quantity.toString(), modifier = Modifier.padding(8.dp))
                    Text(order.totalPrice, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}


@Composable
fun AddClientDialog(onDismiss: () -> Unit, onAddClient: (ClientRequest) -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var mobilePhone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add New Client") },
        text = {
            Column {
                OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") })
                OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") })
                OutlinedTextField(value = middleName, onValueChange = { middleName = it }, label = { Text("Middle Name") })
                OutlinedTextField(value = mobilePhone, onValueChange = { mobilePhone = it }, label = { Text("Mobile Phone") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val newClient = ClientRequest(
                    first_name = firstName,
                    last_name = lastName,
                    middle_name = middleName,
                    mobile_phone = mobilePhone,
                    email = email
                )
                onAddClient(newClient)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddOrderDialog(onDismiss: () -> Unit, onAddOrder: (OrderRequest) -> Unit) {
    var client by remember { mutableStateOf(0) }
    var product by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(0) }
    var price by remember { mutableStateOf("") }
    var totalPrice by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add New Order") },
        text = {
            Column {
                OutlinedTextField(value = client.toString(), onValueChange = { client = it.toIntOrNull() ?: 0 }, label = { Text("Client ID") })
                OutlinedTextField(value = product, onValueChange = { product = it }, label = { Text("Product") })
                OutlinedTextField(value = quantity.toString(), onValueChange = { quantity = it.toIntOrNull() ?: 0 }, label = { Text("Quantity") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
                OutlinedTextField(value = totalPrice, onValueChange = { totalPrice = it }, label = { Text("Total Price") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val newOrder = OrderRequest(
                    client = client,
                    product = product,
                    quantity = quantity,
                    price = price,
                    totalPrice = totalPrice,
                    description = description
                )
                onAddOrder(newOrder)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
