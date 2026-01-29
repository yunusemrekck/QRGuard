package com.example.qrguard.ui.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrguard.QrGuardApp
import com.example.qrguard.data.repository.QrScanRepository
import com.example.qrguard.domain.model.QrContent
import com.example.qrguard.domain.model.QrContentType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QrScanRepository

    init {
        val database = (application as QrGuardApp).database
        repository = QrScanRepository(database.qrScanDao())
    }

    private val _selectedFilter = MutableStateFlow<QrContentType?>(null)
    val selectedFilter: StateFlow<QrContentType?> = _selectedFilter.asStateFlow()

    private val _showClearDialog = MutableStateFlow(false)
    val showClearDialog: StateFlow<Boolean> = _showClearDialog.asStateFlow()

    private val _selectedQrContent = MutableStateFlow<QrContent?>(null)
    val selectedQrContent: StateFlow<QrContent?> = _selectedQrContent.asStateFlow()

    val filteredScans: StateFlow<List<QrContent>> = combine(
        repository.allScans,
        _selectedFilter
    ) { scans, filter ->
        if (filter == null) scans
        else scans.filter { it.type == filter }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

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

    fun deleteScan(scan: QrContent) {
        viewModelScope.launch {
            repository.deleteScan(scan.id)
            if (_selectedQrContent.value?.id == scan.id) {
                _selectedQrContent.value = null
            }
        }
    }

    fun showClearHistoryDialog() { _showClearDialog.value = true }
    fun dismissClearHistoryDialog() { _showClearDialog.value = false }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.deleteAllHistory()
            _showClearDialog.value = false
            _selectedQrContent.value = null
        }
    }
}