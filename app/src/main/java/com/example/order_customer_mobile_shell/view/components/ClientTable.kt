package com.example.order_customer_mobile_shell.view.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.order_customer_mobile_shell.data.ClientRequest


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
            // Шапка таблицы
            Row(
                modifier = Modifier
                    .background(Color(0xFFdbe5f0))
            ) {
                TableCell("Имя", header = true)
                TableCell("Отчество", header = true)
                TableCell("Фамилия", header = true)
                TableCell("Email", header = true)
                TableCell("Телефон", header = true)
            }

            // Строки данных
            clients.forEach { client ->
                Row {
                    TableCell(client.first_name)
                    TableCell(client.middle_name ?: "N/A")
                    TableCell(client.last_name)
                    TableCell(client.email)
                    TableCell(client.mobile_phone ?: "N/A")
                }
            }
        }
    }
}

@Composable
fun TableCell(content: String, header: Boolean = false) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(50.dp)
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = if (header) Color.Gray else Color(0xFFDADADA) // Только горизонтальные границы
                ),
                shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp), // Оставляем внутренний отступ
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = content,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace,
            color = if (header) Color.Black else Color.DarkGray
        )
    }
}




