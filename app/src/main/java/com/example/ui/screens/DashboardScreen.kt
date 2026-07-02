package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.StatCard

fun Double.formatCurrency(): String = "৳ ${this.toLong()}"

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()

    val totalSales = orders.sumOf { it.totalAmount }
    val totalAdvance = orders.sumOf { it.advancePaid }
    val totalDue = orders.sumOf { it.dueAmount }

    val workerPayments = transactions
        .filter { it.type == "payment" || it.type == "advance" }
        .sumOf { it.amount }

    val netBalance = totalSales - workerPayments

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("ড্যাশবোর্ড (Dashboard)", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    title = "মোট বিক্রয়",
                    value = totalSales.formatCurrency(),
                    icon = Icons.Default.MonetizationOn,
                    color = Color(0xFF27AE60),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "নেট ব্যালেন্স",
                    value = netBalance.formatCurrency(),
                    icon = Icons.Default.AccountBalanceWallet,
                    color = Color(0xFF2980B9),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    title = "মোট অগ্রিম",
                    value = totalAdvance.formatCurrency(),
                    icon = Icons.Default.AttachMoney,
                    color = Color(0xFFF39C12),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "বাকি টাকা",
                    value = totalDue.formatCurrency(),
                    icon = Icons.Default.MoneyOff,
                    color = Color(0xFFE74C3C),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("আজকের বিশেষ অফার", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("সমস্ত সেলাই কাজে ১০% ছাড় চলছে! শুধুমাত্র আজ", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }

        item {
            Text("সাম্প্রতিক অর্ডার (Recent Orders)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        }

        items(orders.sortedByDescending { it.orderDate }.take(5)) { order ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(order.customerName, fontWeight = FontWeight.Bold)
                        Text(order.itemName, style = MaterialTheme.typography.bodyMedium)
                        Text("ডেলিভারি: ${order.deliveryDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(order.totalAmount.formatCurrency(), fontWeight = FontWeight.Bold)
                        Text(order.status, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}
