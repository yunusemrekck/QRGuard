package com.yunusek.qrguard.domain.model

data class QrContent(
    val id: Long = 0,
    val rawValue: String,
    val type: QrContentType,
    val displayValue: String = rawValue,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val securityWarning: SecurityWarning? = null,
    val parsedData: ParsedQrData? = null
)

sealed class ParsedQrData {
    data class WifiData(
        val ssid: String,
        val password: String?,
        val securityType: String
    ) : ParsedQrData()
    
    data class EmailData(
        val address: String,
        val subject: String?,
        val body: String?
    ) : ParsedQrData()
    
    data class PhoneData(
        val number: String
    ) : ParsedQrData()
    
    data class SmsData(
        val number: String,
        val message: String?
    ) : ParsedQrData()
    
    data class GeoData(
        val latitude: Double,
        val longitude: Double,
        val label: String?
    ) : ParsedQrData()
    
    data class ContactData(
        val name: String?,
        val phone: String?,
        val email: String?,
        val organization: String?
    ) : ParsedQrData()
}

enum class SecurityWarning {
    INSECURE_HTTP,
    SUSPICIOUS_URL,
    SHORT_URL
}
