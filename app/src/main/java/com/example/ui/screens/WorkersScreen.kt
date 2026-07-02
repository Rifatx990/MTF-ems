package com.example.ui.screens

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
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.WorkerEntity
import com.example.data.WorkerTransactionEntity
import com.example.ui.MainViewModel
import com.example.ui.components.DatePickerField
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkersScreen(viewModel: MainViewModel) {
    val workers by viewModel.workers.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()

    var showAddWorker by remember { mutableStateOf(false) }
    var showAddTransaction by remember { mutableStateOf(false) }

    if (showAddWorker) {
        AddWorkerForm(
            onDismiss = { showAddWorker = false },
            onSave = { worker ->
                viewModel.insertWorker(worker)
                showAddWorker = false
            }
        )
    } else if (showAddTransaction) {
        AddTransactionForm(
            workers = workers,
            onDismiss = { showAddTransaction = false },
            onSave = { transaction ->
                viewModel.insertTransaction(transaction)
                showAddTransaction = false
            }
        )
    } else {
        Scaffold(
            floatingActionButton = {
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FloatingActionButton(onClick = { showAddTransaction = true }, containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                        Icon(Icons.Default.Receipt, contentDescription = "Add Transaction")
                    }
                    FloatingActionButton(onClick = { showAddWorker = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Worker")
                    }
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("কর্মী ব্যবস্থাপনা (Workers)", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }

                items(workers) { worker ->
                    WorkerCard(
                        worker = worker,
                        transactions = transactions.filter { it.workerId == worker.id },
                        onDelete = { viewModel.deleteWorker(it.id) }
                    )
                }

                item {
                    Text("লেনদেন (Transactions)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
                }

                items(transactions) { transaction ->
                    TransactionCard(
                        transaction = transaction,
                        onDelete = { viewModel.deleteTransaction(it.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun WorkerCard(worker: WorkerEntity, transactions: List<WorkerTransactionEntity>, onDelete: (WorkerEntity) -> Unit) {
    var totalWork = 0
    var totalSalary = 0.0
    var advancePaid = 0.0

    transactions.forEach { trans ->
        if (trans.type == "work_add") {
            val quantity = trans.workQuantity
            val rate = if (trans.workRate > 0) trans.workRate else worker.ratePerWork
            totalWork += quantity
            totalSalary += quantity * rate
        } else if (trans.type == "advance" || trans.type == "payment") {
            advancePaid += trans.amount
        }
    }
    val dueAmount = totalSalary - advancePaid

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(worker.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(worker.workType, style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("ফোন (Phone): ${worker.phone}")
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("মোট কাজ: $totalWork")
                    Text("বেতন: ${totalSalary.formatCurrency()}")
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("অগ্রিম: ${advancePaid.formatCurrency()}")
                    Text("বাকি: ${dueAmount.formatCurrency()}", color = if (dueAmount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { onDelete(worker) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: WorkerTransactionEntity, onDelete: (WorkerTransactionEntity) -> Unit) {
    val typeText = when (transaction.type) {
        "payment" -> "বেতন প্রদান"
        "advance" -> "অগ্রিম"
        "work_add" -> "কাজ যোগ"
        else -> "অন্যান্য"
    }
    
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.workerName, fontWeight = FontWeight.Bold)
                Text(typeText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                Text(transaction.date, style = MaterialTheme.typography.bodySmall)
                if (transaction.description.isNotBlank()) {
                    Text(transaction.description, style = MaterialTheme.typography.bodySmall)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(transaction.amount.formatCurrency(), fontWeight = FontWeight.Bold)
                IconButton(onClick = { onDelete(transaction) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWorkerForm(onDismiss: () -> Unit, onSave: (WorkerEntity) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var workType by remember { mutableStateOf("সেলাই") }
    var ratePerWork by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("নতুন কর্মী (New Worker)") },
                navigationIcon = { IconButton(onClick = onDismiss) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
            
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = workType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Work Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("কাটিং", "সেলাই", "এমব্রয়ডারি", "অন্যান্য").forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                workType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(value = ratePerWork, onValueChange = { ratePerWork = it }, label = { Text("Rate per Work") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

            Button(
                onClick = {
                    onSave(
                        WorkerEntity(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            phone = phone,
                            address = address,
                            workType = workType,
                            ratePerWork = ratePerWork.toDoubleOrNull() ?: 0.0,
                            notes = notes,
                            createdAt = System.currentTimeMillis().toString(),
                            updatedAt = System.currentTimeMillis().toString()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Save Worker")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionForm(workers: List<WorkerEntity>, onDismiss: () -> Unit, onSave: (WorkerTransactionEntity) -> Unit) {
    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    var date by remember { mutableStateOf(sdf.format(Date())) }
    var selectedWorkerId by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("payment") }
    var amount by remember { mutableStateOf("0") }
    var workQuantity by remember { mutableStateOf("0") }
    var workRate by remember { mutableStateOf("0") }
    var description by remember { mutableStateOf("") }

    val selectedWorker = workers.find { it.id == selectedWorkerId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("নতুন লেনদেন (New Transaction)") },
                navigationIcon = { IconButton(onClick = onDismiss) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DatePickerField(value = date, onValueChange = { date = it }, label = "Date", modifier = Modifier.fillMaxWidth())

            var expandedWorker by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedWorker,
                onExpandedChange = { expandedWorker = !expandedWorker }
            ) {
                OutlinedTextField(
                    value = selectedWorker?.name ?: "Select Worker",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Worker") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedWorker) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expandedWorker, onDismissRequest = { expandedWorker = false }) {
                    workers.forEach { worker ->
                        DropdownMenuItem(
                            text = { Text(worker.name) },
                            onClick = {
                                selectedWorkerId = worker.id
                                expandedWorker = false
                            }
                        )
                    }
                }
            }

            var expandedType by remember { mutableStateOf(false) }
            val types = mapOf("payment" to "বেতন প্রদান", "advance" to "অগ্রিম প্রদান", "work_add" to "কাজ যোগ", "other" to "অন্যান্য")
            ExposedDropdownMenuBox(
                expanded = expandedType,
                onExpandedChange = { expandedType = !expandedType }
            ) {
                OutlinedTextField(
                    value = types[type] ?: type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Transaction Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                    types.forEach { (key, value) ->
                        DropdownMenuItem(
                            text = { Text(value) },
                            onClick = {
                                type = key
                                expandedType = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            
            if (type == "work_add") {
                OutlinedTextField(value = workQuantity, onValueChange = { workQuantity = it }, label = { Text("Work Quantity") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = workRate, onValueChange = { workRate = it }, label = { Text("Work Rate") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }

            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

            Button(
                onClick = {
                    if (selectedWorker != null) {
                        onSave(
                            WorkerTransactionEntity(
                                id = UUID.randomUUID().toString(),
                                date = date,
                                workerId = selectedWorker.id,
                                workerName = selectedWorker.name,
                                type = type,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                workQuantity = workQuantity.toIntOrNull() ?: 0,
                                workRate = workRate.toDoubleOrNull() ?: 0.0,
                                description = description,
                                createdAt = System.currentTimeMillis().toString()
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                enabled = selectedWorkerId.isNotEmpty()
            ) {
                Text("Save Transaction")
            }
        }
    }
}
