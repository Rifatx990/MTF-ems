package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.DatePickerField
import com.example.utils.HtmlGenerator
import com.example.utils.PrintHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val workers by viewModel.workers.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    var reportFromDate by remember { mutableStateOf("") }
    var reportToDate by remember { mutableStateOf(today) }

    var workerReportFrom by remember { mutableStateOf("") }
    var workerReportTo by remember { mutableStateOf(today) }
    var selectedWorkerId by remember { mutableStateOf("") }

    val filteredOrders = orders.filter {
        (reportFromDate.isEmpty() || it.orderDate >= reportFromDate) &&
        (reportToDate.isEmpty() || it.orderDate <= reportToDate)
    }

    val totalSale = filteredOrders.sumOf { it.totalAmount }
    val totalAdvance = filteredOrders.sumOf { it.advancePaid }
    val totalDue = filteredOrders.sumOf { it.dueAmount }

    val selectedWorker = workers.find { it.id == selectedWorkerId }
    val filteredTransactions = transactions.filter {
        it.workerId == selectedWorkerId &&
        (workerReportFrom.isEmpty() || it.date >= workerReportFrom) &&
        (workerReportTo.isEmpty() || it.date <= workerReportTo)
    }

    var totalWork = 0
    var totalSalary = 0.0
    var advancePaid = 0.0

    filteredTransactions.forEach { trans ->
        if (trans.type == "work_add") {
            val quantity = trans.workQuantity
            val rate = if (trans.workRate > 0) trans.workRate else (selectedWorker?.ratePerWork ?: 0.0)
            totalWork += quantity
            totalSalary += quantity * rate
        } else if (trans.type == "advance" || trans.type == "payment") {
            advancePaid += trans.amount
        }
    }
    val dueAmount = totalSalary - advancePaid

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("বিক্রয় রিপোর্ট (Sales Report)", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DatePickerField(value = reportFromDate, onValueChange = { reportFromDate = it }, label = "From Date", modifier = Modifier.weight(1f))
                DatePickerField(value = reportToDate, onValueChange = { reportToDate = it }, label = "To Date", modifier = Modifier.weight(1f))
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("মোট বিক্রয়: ${totalSale.formatCurrency()}", fontWeight = FontWeight.Bold)
                    Text("মোট অগ্রিম: ${totalAdvance.formatCurrency()}")
                    Text("বাকি টাকা: ${totalDue.formatCurrency()}", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        val html = HtmlGenerator.generateSalesReportHtml(
                            shopName = "মেহেদী টেইলার্স & ফেব্রিক্স",
                            shopAddress = "ধনাইদ, আশুলিয়া, সাভার, ঢাকা",
                            shopPhone = "01720267213, 01812249596",
                            fromDate = reportFromDate,
                            toDate = reportToDate,
                            totalSale = totalSale,
                            totalAdvance = totalAdvance,
                            totalDue = totalDue
                        )
                        PrintHelper.printHtml(context, html, "Sales_Report")
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Print Sales Report")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("কর্মী রিপোর্ট (Worker Report)", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        item {
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
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DatePickerField(value = workerReportFrom, onValueChange = { workerReportFrom = it }, label = "From Date", modifier = Modifier.weight(1f))
                DatePickerField(value = workerReportTo, onValueChange = { workerReportTo = it }, label = "To Date", modifier = Modifier.weight(1f))
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("মোট কাজ: $totalWork", fontWeight = FontWeight.Bold)
                    Text("মোট বেতন: ${totalSalary.formatCurrency()}")
                    Text("অগ্রিম: ${advancePaid.formatCurrency()}")
                    Text("বাকি: ${dueAmount.formatCurrency()}", color = if (dueAmount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        val html = HtmlGenerator.generateWorkerReportHtml(
                            shopName = "মেহেদী টেইলার্স & ফেব্রিক্স",
                            shopAddress = "ধনাইদ, আশুলিয়া, সাভার, ঢাকা",
                            shopPhone = "01720267213, 01812249596",
                            workerName = selectedWorker?.name ?: "Unknown",
                            fromDate = workerReportFrom,
                            toDate = workerReportTo,
                            totalWork = totalWork,
                            totalSalary = totalSalary,
                            advancePaid = advancePaid,
                            dueAmount = dueAmount
                        )
                        PrintHelper.printHtml(context, html, "Worker_Report")
                    }, modifier = Modifier.fillMaxWidth(), enabled = selectedWorker != null) {
                        Text("Print Worker Report")
                    }
                }
            }
        }
    }
}
