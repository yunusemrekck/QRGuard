package com.yunusek.qrguard.generator

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object QrGenerator {
    
    suspend fun generateQrCode(
        content: String,
        size: Int = 512,
        foregroundColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE,
        errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.M
    ): Bitmap? = withContext(Dispatchers.Default) {
        try {
            val hints = hashMapOf<EncodeHintType, Any>(
                EncodeHintType.ERROR_CORRECTION to errorCorrectionLevel,
                EncodeHintType.MARGIN to 1,
                EncodeHintType.CHARACTER_SET to "UTF-8"
            )
            
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)
            
            for (y in 0 until height) {
                for (x in 0 until width) {
                    pixels[y * width + x] = if (bitMatrix[x, y]) foregroundColor else backgroundColor
                }
            }
            
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun generateTextQrContent(text: String): String = text
    
    fun generateUrlQrContent(url: String): String {
        return if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            "https://$url"
        }
    }
    
    fun generateWifiQrContent(
        ssid: String,
        password: String?,
        securityType: WifiSecurityType
    ): String {
        val security = when (securityType) {
            WifiSecurityType.WPA -> "WPA"
            WifiSecurityType.WEP -> "WEP"
            WifiSecurityType.NONE -> "nopass"
        }
        
        return buildString {
            append("WIFI:")
            append("T:$security;")
            append("S:${escapeWifiString(ssid)};")
            if (!password.isNullOrEmpty() && securityType != WifiSecurityType.NONE) {
                append("P:${escapeWifiString(password)};")
            }
            append(";")
        }
    }
    
    fun generateEmailQrContent(
        email: String,
        subject: String? = null,
        body: String? = null
    ): String {
        return buildString {
            append("mailto:$email")
            val params = mutableListOf<String>()
            subject?.let { params.add("subject=${it.encodeUrl()}") }
            body?.let { params.add("body=${it.encodeUrl()}") }
            if (params.isNotEmpty()) {
                append("?${params.joinToString("&")}")
            }
        }
    }
    
    fun generatePhoneQrContent(phoneNumber: String): String {
        val cleanNumber = phoneNumber.replace(Regex("[^+\\d]"), "")
        return "tel:$cleanNumber"
    }
    
    fun generateSmsQrContent(phoneNumber: String, message: String? = null): String {
        val cleanNumber = phoneNumber.replace(Regex("[^+\\d]"), "")
        return if (message.isNullOrEmpty()) {
            "smsto:$cleanNumber"
        } else {
            "smsto:$cleanNumber:$message"
        }
    }
    
    fun generateVCardQrContent(
        name: String,
        phone: String? = null,
        email: String? = null,
        company: String? = null,
        title: String? = null,
        address: String? = null,
        website: String? = null,
        note: String? = null
    ): String {
        return buildString {
            appendLine("BEGIN:VCARD")
            appendLine("VERSION:3.0")
            
            // İsim parsing
            val nameParts = name.trim().split(" ", limit = 2)
            val firstName = nameParts.getOrNull(0) ?: ""
            val lastName = nameParts.getOrNull(1) ?: ""
            appendLine("N:$lastName;$firstName;;;")
            appendLine("FN:$name")
            
            phone?.let { appendLine("TEL:$it") }
            email?.let { appendLine("EMAIL:$it") }
            company?.let { appendLine("ORG:$it") }
            title?.let { appendLine("TITLE:$it") }
            address?.let { appendLine("ADR:;;$it;;;;") }
            website?.let { appendLine("URL:$it") }
            note?.let { appendLine("NOTE:$it") }
            
            appendLine("END:VCARD")
        }
    }
    
    fun generateGeoQrContent(
        latitude: Double,
        longitude: Double,
        label: String? = null
    ): String {
        return if (label.isNullOrEmpty()) {
            "geo:$latitude,$longitude"
        } else {
            "geo:$latitude,$longitude?q=$latitude,$longitude(${label.encodeUrl()})"
        }
    }
    
    fun generateEventQrContent(
        title: String,
        location: String? = null,
        startDateTime: String, // Format: yyyyMMddTHHmmss
        endDateTime: String? = null,
        description: String? = null
    ): String {
        return buildString {
            appendLine("BEGIN:VEVENT")
            appendLine("SUMMARY:$title")
            appendLine("DTSTART:$startDateTime")
            endDateTime?.let { appendLine("DTEND:$it") }
            location?.let { appendLine("LOCATION:$it") }
            description?.let { appendLine("DESCRIPTION:$it") }
            appendLine("END:VEVENT")
        }
    }
    
    private fun escapeWifiString(input: String): String {
        return input
            .replace("\\", "\\\\")
            .replace(";", "\\;")
            .replace(",", "\\,")
            .replace("\"", "\\\"")
            .replace(":", "\\:")
    }
    
    private fun String.encodeUrl(): String {
        return java.net.URLEncoder.encode(this, "UTF-8")
    }
}

enum class WifiSecurityType {
    WPA, WEP, NONE
}
