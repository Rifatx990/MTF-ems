package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("DELETE FROM orders WHERE id = :id")
    suspend fun deleteOrderById(id: String)
}

@Dao
interface WorkerDao {
    @Query("SELECT * FROM workers ORDER BY name ASC")
    fun getAllWorkers(): Flow<List<WorkerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorker(worker: WorkerEntity)

    @Query("DELETE FROM workers WHERE id = :id")
    suspend fun deleteWorkerById(id: String)
}

@Dao
interface WorkerTransactionDao {
    @Query("SELECT * FROM worker_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<WorkerTransactionEntity>>

    @Query("SELECT * FROM worker_transactions WHERE workerId = :workerId ORDER BY date DESC")
    fun getTransactionsForWorker(workerId: String): Flow<List<WorkerTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WorkerTransactionEntity)

    @Query("DELETE FROM worker_transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: String)
    
    @Query("DELETE FROM worker_transactions WHERE workerId = :workerId")
    suspend fun deleteTransactionsByWorkerId(workerId: String)
}
