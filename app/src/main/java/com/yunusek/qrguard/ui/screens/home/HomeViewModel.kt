package com.yunusek.qrguard.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunusek.qrguard.QrGuardApp
import com.yunusek.qrguard.data.repository.QrScanRepository
import com.yunusek.qrguard.domain.model.QrContent
import com.yunusek.qrguard.domain.model.QrContentType
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

    private val _selectedQrContent = MutableStateFlow<QrContent?>(null)
    val selectedQrContent: StateFlow<QrContent?> = _selectedQrContent.asStateFlow()

    fun onFilterSelected(type: QrContentType?) {
        _selectedFilter.value = if (_selectedFilter.value == type) null else type
    }

    fun onScanItemClick(scan: QrContent) {
        _selectedQrContent.value = scan
    }

    fun dismissBottomSheet() {
        _selectedQrContent.value = null
    }

    fun toggleFavorite(scan: QrContent) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(scan.id, !scan.isFavorite)
            if (_selectedQrContent.value?.id == scan.id) {
                _selectedQrContent.value = scan.copy(isFavorite = !scan.isFavorite)
            }
        }
    }
}