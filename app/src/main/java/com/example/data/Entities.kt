package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "orders")
@Serializable
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val orderDate: String,
    val deliveryDate: String,
    val customerName: String,
    val phone: String,
    val address: String,
    val itemName: String,
    val quantity: Int,
    val measurements: String,
    val notes: String,
    val totalAmount: Double,
    val advancePaid: Double,
    val dueAmount: Double,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)

@Entity(tableName = "workers")
@Serializable
data class WorkerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val address: String,
    val workType: String,
    val ratePerWork: Double,
    val notes: String,
    val createdAt: String,
    val updatedAt: String
)

@Entity(tableName = "worker_transactions")
@Serializable
data class WorkerTransactionEntity(
    @PrimaryKey val id: String,
    val date: String,
    val workerId: String,
    val workerName: String,
    val type: String,
    val amount: Double,
    val workQuantity: Int,
    val workRate: Double,
    val description: String,
    val createdAt: String
)
