package com.example.data

import kotlinx.coroutines.flow.Flow

class TailorRepository(private val database: AppDatabase) {
    val allOrders: Flow<List<OrderEntity>> = database.orderDao().getAllOrders()
    val allWorkers: Flow<List<WorkerEntity>> = database.workerDao().getAllWorkers()
    val allTransactions: Flow<List<WorkerTransactionEntity>> = database.workerTransactionDao().getAllTransactions()

    suspend fun insertOrder(order: OrderEntity) = database.orderDao().insertOrder(order)
    suspend fun deleteOrder(id: String) = database.orderDao().deleteOrderById(id)

    suspend fun insertWorker(worker: WorkerEntity) = database.workerDao().insertWorker(worker)
    suspend fun deleteWorker(id: String) {
        database.workerTransactionDao().deleteTransactionsByWorkerId(id)
        database.workerDao().deleteWorkerById(id)
    }

    suspend fun insertTransaction(transaction: WorkerTransactionEntity) = database.workerTransactionDao().insertTransaction(transaction)
    suspend fun deleteTransaction(id: String) = database.workerTransactionDao().deleteTransactionById(id)
    
    fun getTransactionsForWorker(workerId: String) = database.workerTransactionDao().getTransactionsForWorker(workerId)
}
