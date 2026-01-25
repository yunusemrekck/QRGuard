package com.example.qrguard.scanner

import com.example.qrguard.domain.model.QrContent

sealed interface ScannerState {
    data object Scanning : ScannerState
    data class Result(val content: QrContent) : ScannerState
}
