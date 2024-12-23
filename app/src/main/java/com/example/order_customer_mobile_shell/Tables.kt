package com.example.order_customer_mobile_shell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.order_customer_mobile_shell.data.ClientRequest
import com.example.order_customer_mobile_shell.data.OrderRequest

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
            Row(
                modifier = Modifier
                    .background(Color.LightGray)
                    .padding(8.dp)
            ) {
                Text("First Name", modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center)
                Text("Middle Name", modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center)
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
                    Text(client.middle_name ?:"N/A", modifier = Modifier.padding(8.dp))
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