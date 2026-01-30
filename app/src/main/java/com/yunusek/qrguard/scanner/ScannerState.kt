package com.yunusek.qrguard.scanner

import com.yunusek.qrguard.domain.model.QrContent

sealed interface ScannerState {
    data object Scanning : ScannerState
    data class Result(val content: QrContent) : ScannerState
}
