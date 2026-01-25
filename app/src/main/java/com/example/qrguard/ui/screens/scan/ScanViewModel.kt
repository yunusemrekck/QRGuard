package com.example.qrguard.ui.screens.scan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrguard.QrGuardApp
import com.example.qrguard.data.repository.QrScanRepository
import com.example.qrguard.domain.model.QrContent
import com.example.qrguard.domain.parser.QrParser
import com.example.qrguard.scanner.ScannerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class ScanViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: QrScanRepository
    
    init {
        val database = (application as QrGuardApp).database
        repository = QrScanRepository(database.qrScanDao())
    }

    private val _scannerState = MutableStateFlow<ScannerState>(ScannerState.Scanning)
    val scannerState: StateFlow<ScannerState> = _scannerState.asStateFlow()

    private val _isTorchEnabled = MutableStateFlow(false)
    val isTorchEnabled: StateFlow<Boolean> = _isTorchEnabled.asStateFlow()
    
    private val _currentScan = MutableStateFlow<QrContent?>(null)
    val currentScan: StateFlow<QrContent?> = _currentScan.asStateFlow()

    private val isLocked = AtomicBoolean(false)
    private var lastScanTime = 0L
    private val debounceInterval = 1500L

    fun onQrCodeDetected(rawValue: String) {
        val currentTime = System.currentTimeMillis()
        
        if (currentTime - lastScanTime < debounceInterval) return
        if (!isLocked.compareAndSet(false, true)) return

        lastScanTime = currentTime
        
        val content = QrParser.parse(rawValue)
        if (content != null) {
            _currentScan.value = content
            _scannerState.value = ScannerState.Result(content)
            
            viewModelScope.launch {
                val savedId = repository.saveScan(content)
                _currentScan.value = content.copy(id = savedId)
            }
        } else {
            isLocked.set(false)
        }
    }

    fun onResultDismissed() {
        _scannerState.value = ScannerState.Scanning
        _currentScan.value = null
        isLocked.set(false)
    }

    fun toggleTorch() {
        _isTorchEnabled.value = !_isTorchEnabled.value
    }
    
    fun toggleFavorite() {
        val current = _currentScan.value ?: return
        viewModelScope.launch {
            val newFavoriteStatus = !current.isFavorite
            repository.updateFavoriteStatus(current.id, newFavoriteStatus)
            _currentScan.value = current.copy(isFavorite = newFavoriteStatus)
            
            val currentState = _scannerState.value
            if (currentState is ScannerState.Result) {
                _scannerState.value = ScannerState.Result(
                    currentState.content.copy(isFavorite = newFavoriteStatus)
                )
            }
        }
    }

    fun onGalleryClick() {
        // Sprint-4: Galeri'den QR okuma
    }
}
