package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

@Composable
fun AccountingScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf(today) }

    val totalSale = orders.filter {
        (fromDate.isEmpty() || it.orderDate >= fromDate) &&
        (toDate.isEmpty() || it.orderDate <= toDate)
    }.sumOf { it.totalAmount }

    val totalWorkerPayment = transactions.filter {
        (it.type == "payment" || it.type == "advance") &&
        (fromDate.isEmpty() || it.date >= fromDate) &&
        (toDate.isEmpty() || it.date <= toDate)
    }.sumOf { it.amount }

    val netProfit = totalSale - totalWorkerPayment

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("অ্যাকাউন্টিং (Accounting)", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DatePickerField(value = fromDate, onValueChange = { fromDate = it }, label = "From Date", modifier = Modifier.weight(1f))
                DatePickerField(value = toDate, onValueChange = { toDate = it }, label = "To Date", modifier = Modifier.weight(1f))
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("মোট বিক্রয়: ${totalSale.formatCurrency()}", fontWeight = FontWeight.Bold)
                    Text("কর্মী বেতন: ${totalWorkerPayment.formatCurrency()}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("নেট লাভ: ${netProfit.formatCurrency()}", style = MaterialTheme.typography.titleMedium, color = if (netProfit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        val html = HtmlGenerator.generateAccountingReportHtml(
                            shopName = "মেহেদী টেইলার্স & ফেব্রিক্স",
                            shopAddress = "ধনাইদ, আশুলিয়া, সাভার, ঢাকা",
                            shopPhone = "01720267213, 01812249596",
                            fromDate = fromDate,
                            toDate = toDate,
                            totalSale = totalSale,
                            totalWorkerPayment = totalWorkerPayment,
                            netProfit = netProfit
                        )
                        PrintHelper.printHtml(context, html, "Accounting_Report")
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Print Accounting Report")
                    }
                }
            }
        }
    }
}
