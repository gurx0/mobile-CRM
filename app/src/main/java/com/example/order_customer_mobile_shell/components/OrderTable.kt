package com.example.order_customer_mobile_shell.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.order_customer_mobile_shell.data.OrderRequest

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
            ) {
                OrderTableCell("Client")
                OrderTableCell("Product")
                OrderTableCell("Quantity")
                OrderTableCell("Total Price")
                OrderTableCell("Status")
                OrderTableCell("Description")
                OrderTableCell("Created At")
            }

            // Строки данных
            orders.forEach { order ->
                Row(
                    modifier = Modifier
                        .border(1.dp, Color.Gray)
                ) {
                    OrderTableCell(order.client.full_name)
                    OrderTableCell(order.product)
                    OrderTableCell(order.quantity.toString())
                    OrderTableCell(order.total_price)
                    OrderTableCell(order.status)
                    OrderTableCell(order.description)
                    OrderTableCell(order.created_at)
                }
            }
        }
    }
}

@Composable
fun OrderTableCell(content: String) {
    Box(
        modifier = Modifier
            .width(150.dp) // Фиксированная ширина ячейки
            .height(50.dp) // Фиксированная высота ячейки
            .border(1.dp, Color.Gray)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(content, textAlign = TextAlign.Center)
    }
}