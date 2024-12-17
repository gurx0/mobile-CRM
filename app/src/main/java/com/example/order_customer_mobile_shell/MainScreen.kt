package com.example.order_customer_mobile_shell

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var query by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Состояние для сайдбара
    val scope = rememberCoroutineScope() // Для управления состоянием в корутинах

    // Выезжающий сайдбар
    ModalNavigationDrawer(
        drawerContent = {
            SidebarContent {
                scope.launch { drawerState.close() } // Закрыть сайдбар
            }
        },
        drawerState = drawerState, // Управляемое состояние
        gesturesEnabled = false, // Отключаем свайпы, чтобы открытие было только по кнопке
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
                                // Левая кнопка (открытие сайдбара)
                                IconButton(
                                    onClick = {
                                        scope.launch { drawerState.open() } // Открыть сайдбар
                                    },
                                    modifier = Modifier.padding(start = 10.dp)
                                ) {
                                    Icon(Icons.Default.Dehaze, contentDescription = "Menu")
                                }

                                // Поле ввода
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

                                // Кнопка аккаунта
                                IconButton(
                                    onClick = { /* TODO: Обработать аккаунт */ },
                                    modifier = Modifier.padding(end = 5.dp)
                                ) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = "Account")
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
                        Text(
                            text = "Main Content",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { println("FloatingActionButton clicked!") } // Вывод в консоль
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
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
    MainScreen()
}

