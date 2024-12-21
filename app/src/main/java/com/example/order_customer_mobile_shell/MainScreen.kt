package com.example.order_customer_mobile_shell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import com.example.order_customer_mobile_shell.data.ClientRequest
import com.example.order_customer_mobile_shell.network.ApiClient
import com.example.order_customer_mobile_shell.network.AuthService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(authService: AuthService, apiClient: ApiClient = ApiClient(authService)) {
    var query by remember { mutableStateOf("") }
    var newClientFirstName by remember { mutableStateOf("") }
    var newClientLastName by remember { mutableStateOf("") }
    var newClientMiddleName by remember { mutableStateOf("") }
    var newClientMobilePhone by remember { mutableStateOf("") }
    var newClientEmail by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) } // Управление видимостью окна

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var clientData by remember { mutableStateOf(listOf<String>()) }

    ModalNavigationDrawer(
        drawerContent = {
            SidebarContent {
                scope.launch { drawerState.close() }
            }
        },
        drawerState = drawerState,
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
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
                                    onClick = {
                                        scope.launch { drawerState.open() }
                                    },
                                    modifier = Modifier.padding(start = 10.dp)
                                ) {
                                    Icon(Icons.Default.Dehaze, contentDescription = "Menu")
                                }

                                BasicTextField(
                                    value = query,
                                    onValueChange = { query = it },
                                    textStyle = TextStyle(fontSize = 16.sp, color = Color.White),
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 8.dp)
                                        .alpha(0.8f)
                                )

                                IconButton(
                                    onClick = { showAddDialog = true }, // Открыть окно добавления
                                    modifier = Modifier.padding(end = 5.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Client")
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF6200EA),
                            titleContentColor = Color.White,
                            actionIconContentColor = Color.White,
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
                        // Содержимое таблицы
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Здесь отображаются клиенты
                            clientData.forEach { data ->
                                Text(text = data, modifier = Modifier.padding(8.dp))
                            }
                        }

                        // Диалог добавления клиента
                        if (showAddDialog) {
                            AlertDialog(
                                onDismissRequest = { showAddDialog = false },
                                title = { Text("Add New Client") },
                                text = {
                                    Column {
                                        OutlinedTextField(
                                            value = newClientFirstName,
                                            onValueChange = { newClientFirstName = it },
                                            label = { Text("First Name") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = newClientLastName,
                                            onValueChange = { newClientLastName = it },
                                            label = { Text("Last Name") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = newClientMiddleName,
                                            onValueChange = { newClientMiddleName = it },
                                            label = { Text("Middle Name") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = newClientMobilePhone,
                                            onValueChange = { newClientMobilePhone = it },
                                            label = { Text("Mobile Phone") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        OutlinedTextField(
                                            value = newClientEmail,
                                            onValueChange = { newClientEmail = it },
                                            label = { Text("Email") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(
                                        onClick = {
                                            val newClient = ClientRequest(
                                                first_name = newClientFirstName,
                                                last_name = newClientLastName,
                                                middle_name = newClientMiddleName,
                                                mobile_phone = newClientMobilePhone,
                                                email = newClientEmail
                                            )
                                            apiClient.addClient(newClient) { success, _ ->
                                                if (success) {
                                                    // Обновление данных после добавления
                                                    newClientFirstName = ""
                                                    newClientLastName = ""
                                                    newClientMiddleName = ""
                                                    newClientMobilePhone = ""
                                                    newClientEmail = ""
                                                    apiClient.getClients(0, query) { _, response ->
                                                        clientData = response?.split("\n") ?: listOf()
                                                    }
                                                }
                                                showAddDialog = false // Закрыть окно
                                            }
                                        }
                                    ) {
                                        Text("Add")
                                    }
                                },
                                dismissButton = {
                                    Button(onClick = { showAddDialog = false }) {
                                        Text("Cancel")
                                    }
                                }
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                // Запрос на сервер для получения данных
                                authService.authenticate("admin", "admin") { authSuccess ->
                                    if (authSuccess) {
                                        apiClient.getClients(0, query) { success, response ->
                                            if (success && response != null) {
                                                clientData = response.split("\n") // Пример преобразования
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh Data")
                        }
                    }
                }
            )
        }
    )
}


@Composable
fun SidebarContent(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Иконка "Настройки"
        IconButton(onClick = { println("Settings clicked!") }) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Иконка "Информация"
        IconButton(onClick = { println("Info clicked!") }) {
            Icon(Icons.Default.Info, contentDescription = "Info")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка для закрытия
        Button(onClick = onClose, modifier = Modifier.padding(top = 16.dp)) {
            Text("Close Sidebar")
        }
    }
}

@Preview
@Composable
fun PreviewMainScreen() {
    val authService = AuthService()
    MainScreen(authService)
}
