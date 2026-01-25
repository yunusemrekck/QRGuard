package com.example.qrguard.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrguard.QrGuardApp
import com.example.qrguard.data.repository.QrScanRepository
import com.example.qrguard.domain.model.QrContent
import com.example.qrguard.domain.model.QrContentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: QrScanRepository
    
    init {
        val database = (application as QrGuardApp).database
        repository = QrScanRepository(database.qrScanDao())
    }
    
    val recentScans: StateFlow<List<QrContent>> = repository.getRecentScans(5)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val totalCount: StateFlow<Int> = repository.totalCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    
    val favoritesCount: StateFlow<Int> = repository.favoritesCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    
    private val _selectedFilter = MutableStateFlow<QrContentType?>(null)
    val selectedFilter: StateFlow<QrContentType?> = _selectedFilter.asStateFlow()
    
    fun onFilterSelected(type: QrContentType?) {
        _selectedFilter.value = if (_selectedFilter.value == type) null else type
    }
    
    fun toggleFavorite(scan: QrContent) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(scan.id, !scan.isFavorite)
        }
    }
}
