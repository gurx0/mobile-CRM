package com.example.order_customer_mobile_shell.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.order_customer_mobile_shell.data.ClientDetails
import com.example.order_customer_mobile_shell.data.ClientRequest
import com.example.order_customer_mobile_shell.data.OrderRequest
import com.example.order_customer_mobile_shell.data.getCurrentDateTime
import com.example.order_customer_mobile_shell.data.toString

@Composable
fun AddClientDialog(onDismiss: () -> Unit, onAddClient: (ClientRequest) -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var middleName by remember { mutableStateOf("") }
    var mobilePhone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Новый клиент") },
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
    var clientId by remember { mutableStateOf(0) }
    var clientFullName by remember { mutableStateOf("") }
    var clientEmail by remember { mutableStateOf("") }
    var clientMobilePhone by remember { mutableStateOf("") }
    var product by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(0) }
    var price by remember { mutableStateOf("") }
    var totalPrice by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val date = getCurrentDateTime()


    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add New Order") },
        text = {
            Column {
//                // Client details
                OutlinedTextField(value = clientId.toString(), onValueChange = { clientId = it.toIntOrNull() ?: 0 }, label = { Text("Client ID") })
//                OutlinedTextField(value = clientFullName, onValueChange = { clientFullName = it }, label = { Text("Client Full Name") })
//                OutlinedTextField(value = clientEmail, onValueChange = { clientEmail = it }, label = { Text("Client Email") })
//                OutlinedTextField(value = clientMobilePhone, onValueChange = { clientMobilePhone = it }, label = { Text("Client Mobile Phone") })

                // Order details
                OutlinedTextField(value = product, onValueChange = { product = it }, label = { Text("Product") })
                OutlinedTextField(value = quantity.toString(), onValueChange = { quantity = it.toIntOrNull() ?: 0 }, label = { Text("Quantity") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
//                OutlinedTextField(value = totalPrice, onValueChange = { totalPrice = it }, label = { Text("Total Price") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            }
        },
        confirmButton = {
            Button(onClick = {
                val clientDetails = ClientDetails(
                    id = clientId,
                    full_name = clientFullName,
                    email = clientEmail,
                    mobile_phone = clientMobilePhone
                )
                val newOrder = OrderRequest(
                    client = clientDetails,
                    product = product,
                    quantity = quantity,
                    price = price,
                    total_price = totalPrice,
                    description = description,
                    status = "Pending",
                    created_at = date.toString("yyyy/MM/dd HH:mm")
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

