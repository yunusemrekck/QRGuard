package com.example.qrguard.ui.screens.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrguard.QrGuardApp
import com.example.qrguard.data.repository.QrScanRepository
import com.example.qrguard.domain.model.QrContent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QrScanRepository

    init {
        val database = (application as QrGuardApp).database
        repository = QrScanRepository(database.qrScanDao())
    }

    val favorites: StateFlow<List<QrContent>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedQrContent = MutableStateFlow<QrContent?>(null)
    val selectedQrContent: StateFlow<QrContent?> = _selectedQrContent.asStateFlow()

    fun onScanItemClick(scan: QrContent) {
        _selectedQrContent.value = scan
    }

    fun dismissBottomSheet() {
        _selectedQrContent.value = null
    }

    fun removeFavorite(scan: QrContent) {
        viewModelScope.launch {
            repository.updateFavoriteStatus(scan.id, false)
            if (_selectedQrContent.value?.id == scan.id) {
                _selectedQrContent.value = scan.copy(isFavorite = false)
            }
        }
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
}