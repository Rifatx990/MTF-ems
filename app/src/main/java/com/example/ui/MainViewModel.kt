package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.OrderEntity
import com.example.data.TailorRepository
import com.example.data.WorkerEntity
import com.example.data.WorkerTransactionEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: TailorRepository) : ViewModel() {
    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val workers: StateFlow<List<WorkerEntity>> = repository.allWorkers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val transactions: StateFlow<List<WorkerTransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    fun insertOrder(order: OrderEntity) {
        viewModelScope.launch { repository.insertOrder(order) }
    }
    
    fun deleteOrder(id: String) {
        viewModelScope.launch { repository.deleteOrder(id) }
    }
    
    fun insertWorker(worker: WorkerEntity) {
        viewModelScope.launch { repository.insertWorker(worker) }
    }
    
    fun deleteWorker(id: String) {
        viewModelScope.launch { repository.deleteWorker(id) }
    }
    
    fun insertTransaction(transaction: WorkerTransactionEntity) {
        viewModelScope.launch { repository.insertTransaction(transaction) }
    }
    
    fun deleteTransaction(id: String) {
        viewModelScope.launch { repository.deleteTransaction(id) }
    }
}

class MainViewModelFactory(private val repository: TailorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
