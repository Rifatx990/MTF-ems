package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.OrderEntity
import com.example.ui.MainViewModel
import com.example.ui.components.DatePickerField
import com.example.utils.HtmlGenerator
import com.example.utils.PrintHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(viewModel: MainViewModel) {
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    var showAddForm by remember { mutableStateOf(false) }
    var editingOrder by remember { mutableStateOf<OrderEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("All") }

    if (showAddForm || editingOrder != null) {
        AddOrderForm(
            initialOrder = editingOrder,
            onDismiss = { 
                showAddForm = false
                editingOrder = null
            },
            onSave = { order ->
                viewModel.insertOrder(order)
                showAddForm = false
                editingOrder = null
            }
        )
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showAddForm = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Order")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("অর্ডার ব্যবস্থাপনা (Orders)", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search orders...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.fillMaxWidth()
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = statusFilter,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status Filter") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("All", "Pending", "Cutting", "Sewing", "Ready", "Delivered").forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    statusFilter = status
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                val filteredOrders = orders.filter {
                    val matchesSearch = it.customerName.contains(searchQuery, ignoreCase = true) ||
                                        it.phone.contains(searchQuery) ||
                                        it.orderId.contains(searchQuery, ignoreCase = true)
                    val matchesStatus = statusFilter == "All" || it.status == statusFilter
                    matchesSearch && matchesStatus
                }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredOrders) { order ->
                        OrderCard(
                            order = order, 
                            onEdit = { editingOrder = it },
                            onDelete = { viewModel.deleteOrder(it.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderEntity, onEdit: (OrderEntity) -> Unit, onDelete: (OrderEntity) -> Unit) {
    val context = LocalContext.current
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(order.orderId, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(order.status, style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("গ্রাহক (Customer): ${order.customerName}")
            Text("ফোন (Phone): ${order.phone}")
            Text("আইটেম (Item): ${order.itemName} (x${order.quantity})")
            Text("ডেলিভারি (Delivery): ${order.deliveryDate}")
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("মোট (Total): ${order.totalAmount.formatCurrency()}")
                    Text("বাকি (Due): ${order.dueAmount.formatCurrency()}", color = MaterialTheme.colorScheme.error)
                }
                Row {
                    IconButton(onClick = {
                        val html = HtmlGenerator.generateInvoiceHtml(
                            order = order,
                            shopName = "মেহেদী টেইলার্স & ফেব্রিক্স",
                            shopAddress = "ধনাইদ, আশুলিয়া, সাভার, ঢাকা",
                            shopPhone = "01720267213, 01812249596"
                        )
                        PrintHelper.printHtml(context, html, "Invoice_${order.orderId}")
                    }) {
                        Icon(Icons.Default.Print, contentDescription = "Print Invoice", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { onEdit(order) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { onDelete(order) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderForm(initialOrder: OrderEntity? = null, onDismiss: () -> Unit, onSave: (OrderEntity) -> Unit) {
    var orderId by remember { mutableStateOf(initialOrder?.orderId ?: "ORD-${System.currentTimeMillis() % 100000}") }
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var orderDate by remember { mutableStateOf(initialOrder?.orderDate ?: sdf.format(Date())) }
    var deliveryDate by remember { mutableStateOf(initialOrder?.deliveryDate ?: sdf.format(Date(System.currentTimeMillis() + 86400000L))) } // tomorrow

    var customerName by remember { mutableStateOf(initialOrder?.customerName ?: "") }
    var phone by remember { mutableStateOf(initialOrder?.phone ?: "") }
    var address by remember { mutableStateOf(initialOrder?.address ?: "") }
    var itemName by remember { mutableStateOf(initialOrder?.itemName ?: "") }
    var quantity by remember { mutableStateOf(initialOrder?.quantity?.toString() ?: "1") }
    var measurements by remember { mutableStateOf(initialOrder?.measurements ?: "") }
    var notes by remember { mutableStateOf(initialOrder?.notes ?: "") }
    var totalAmount by remember { mutableStateOf(initialOrder?.totalAmount?.takeIf { it > 0 }?.toString() ?: "") }
    var advancePaid by remember { mutableStateOf(initialOrder?.advancePaid?.toString() ?: "0") }
    var status by remember { mutableStateOf(initialOrder?.status ?: "Pending") }

    val dueAmount = (totalAmount.toDoubleOrNull() ?: 0.0) - (advancePaid.toDoubleOrNull() ?: 0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (initialOrder != null) "অর্ডার সম্পাদন (Edit Order)" else "নতুন অর্ডার (New Order)") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(value = orderId, onValueChange = { orderId = it }, label = { Text("Order ID") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = customerName, onValueChange = { customerName = it }, label = { Text("Customer Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = { Text("Item Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = measurements, onValueChange = { measurements = it }, label = { Text("Measurements") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            DatePickerField(value = orderDate, onValueChange = { orderDate = it }, label = "Order Date", modifier = Modifier.fillMaxWidth())
            DatePickerField(value = deliveryDate, onValueChange = { deliveryDate = it }, label = "Delivery Date", modifier = Modifier.fillMaxWidth())
            
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = status,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("Pending", "Cutting", "Sewing", "Ready", "Delivered").forEach { stat ->
                        DropdownMenuItem(
                            text = { Text(stat) },
                            onClick = {
                                status = stat
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(value = totalAmount, onValueChange = { totalAmount = it }, label = { Text("Total Amount") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = advancePaid, onValueChange = { advancePaid = it }, label = { Text("Advance Paid") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            
            Text("বাকি (Due Amount): ${dueAmount.formatCurrency()}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))

            Button(
                onClick = {
                    val order = OrderEntity(
                        id = initialOrder?.id ?: UUID.randomUUID().toString(),
                        orderId = orderId,
                        orderDate = orderDate,
                        deliveryDate = deliveryDate,
                        customerName = customerName,
                        phone = phone,
                        address = address,
                        itemName = itemName,
                        quantity = quantity.toIntOrNull() ?: 1,
                        measurements = measurements,
                        notes = notes,
                        totalAmount = totalAmount.toDoubleOrNull() ?: 0.0,
                        advancePaid = advancePaid.toDoubleOrNull() ?: 0.0,
                        dueAmount = dueAmount,
                        status = status,
                        createdAt = initialOrder?.createdAt ?: System.currentTimeMillis().toString(),
                        updatedAt = System.currentTimeMillis().toString()
                    )
                    onSave(order)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (initialOrder != null) "Update Order" else "Save Order")
            }
        }
    }
}
