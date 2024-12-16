package com.example.order_customer_mobile_shell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order System") },
                actions = {
                    // Кнопка фильтров
                    IconButton(onClick = { /*todo Обработать нажатие фильтров */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filters")
                    }

                    // Кнопка входа в аккаунт
                    IconButton(onClick = { /*todo Обработать вход в аккаунт */ }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Account")
                    }
                },
                navigationIcon = {
                    // Кнопка навигации (например, "Назад")
                    IconButton(onClick = { /*todo Обработать нажатие навигации */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EA),
                    titleContentColor = Color.White
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {

                // Поле ввода
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },

                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.DarkGray, CircleShape)
                        .padding(8.dp)
                )

                //получение данных
                Button(
                    onClick = { viewModel.fetchData() },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Fetch Data")
                }

                // список данных
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                        .border(width = 1.dp, color = Color.DarkGray)
                ) {
//                    items(viewModel.dataList) { item ->
//                        Text(text = item.result, modifier = Modifier.padding(8.dp))
//                    } //todo добавить в бд
                }
            }
        }
    )
}

@Preview
@Composable
fun PreviewMainScreen() {
    MainScreen()
}
