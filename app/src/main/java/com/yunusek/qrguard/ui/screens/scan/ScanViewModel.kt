package com.yunusek.qrguard.ui.screens.scan

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunusek.qrguard.QrGuardApp
import com.yunusek.qrguard.data.repository.QrScanRepository
import com.yunusek.qrguard.domain.model.QrContent
import com.yunusek.qrguard.domain.parser.QrParser
import com.yunusek.qrguard.scanner.ScannerState
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    private val _showImagePicker = MutableStateFlow(false)
    val showImagePicker: StateFlow<Boolean> = _showImagePicker.asStateFlow()

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
        _showImagePicker.value = true
    }

    fun onImagePickerDismissed() {
        _showImagePicker.value = false
    }

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    scanQrFromBitmap(bitmap)
                    bitmap.recycle() // Memory cleanup
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        _showImagePicker.value = false
    }

    private suspend fun scanQrFromBitmap(bitmap: Bitmap) {
        try {
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()

            val scanner = BarcodeScanning.getClient(options)
            val inputImage = InputImage.fromBitmap(bitmap, 0)

            // ✅ DÜZELTİLDİ: await() için kotlinx-coroutines-play-services gerekli
            val barcodes = scanner.process(inputImage).await()

            if (barcodes.isNotEmpty()) {
                // ✅ DÜZELTİLDİ: displayValue yerine rawValue kullan
                val firstBarcode = barcodes.first()
                val rawValue = firstBarcode.displayValue ?: firstBarcode.rawValue ?: return

                // ✅ DÜZELTİLDİ: String olarak gönder
                onQrCodeDetected(rawValue)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}