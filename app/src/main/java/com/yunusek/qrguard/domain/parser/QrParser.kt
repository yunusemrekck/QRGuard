package com.yunusek.qrguard.domain.parser

import com.yunusek.qrguard.domain.model.ParsedQrData
import com.yunusek.qrguard.domain.model.QrContent
import com.yunusek.qrguard.domain.model.QrContentType
import com.yunusek.qrguard.domain.model.SecurityWarning

object QrParser {

    private val URL_REGEX = Regex(
        "^(https?://)" +
        "([\\w\\-]+\\.)+[\\w\\-]+" +
        "(:\\d+)?" +
        "(/[\\w\\-./?%&=+#@]*)?$",
        RegexOption.IGNORE_CASE
    )
    
    private val GEO_REGEX = Regex(
        "^geo:([\\-\\d.]+),([\\-\\d.]+)(?:\\?.*q=([^&]+))?",
        RegexOption.IGNORE_CASE
    )
    
    private val SHORT_URL_DOMAINS = listOf(
        "bit.ly", "tinyurl.com", "t.co", "goo.gl", "ow.ly",
        "is.gd", "buff.ly", "short.link", "rb.gy", "cutt.ly"
    )
    
    private val SUSPICIOUS_PATTERNS = listOf(
        "login", "signin", "account", "password", "verify",
        "secure", "update", "confirm", "banking"
    )

    fun parse(rawValue: String?): QrContent? {
        if (rawValue.isNullOrBlank()) return null
        
        val trimmed = rawValue.trim()
        
        return when {
            isWifi(trimmed) -> parseWifi(trimmed)
            isEmail(trimmed) -> parseEmail(trimmed)
            isPhone(trimmed) -> parsePhone(trimmed)
            isSms(trimmed) -> parseSms(trimmed)
            isGeo(trimmed) -> parseGeo(trimmed)
            isVCard(trimmed) -> parseVCard(trimmed)
            isUrl(trimmed) -> parseUrl(trimmed)
            else -> QrContent(
                rawValue = trimmed,
                type = QrContentType.TEXT,
                displayValue = trimmed
            )
        }
    }
    
    private fun isUrl(value: String): Boolean {
        return value.startsWith("http://", ignoreCase = true) ||
               value.startsWith("https://", ignoreCase = true) ||
               URL_REGEX.matches(value)
    }
    
    private fun isWifi(value: String): Boolean {
        return value.startsWith("WIFI:", ignoreCase = true)
    }
    
    private fun isEmail(value: String): Boolean {
        return value.startsWith("mailto:", ignoreCase = true)
    }
    
    private fun isPhone(value: String): Boolean {
        return value.startsWith("tel:", ignoreCase = true)
    }
    
    private fun isSms(value: String): Boolean {
        return value.startsWith("smsto:", ignoreCase = true) ||
               value.startsWith("sms:", ignoreCase = true)
    }
    
    private fun isGeo(value: String): Boolean {
        return value.startsWith("geo:", ignoreCase = true)
    }
    
    private fun isVCard(value: String): Boolean {
        return value.startsWith("BEGIN:VCARD", ignoreCase = true)
    }
    
    private fun parseUrl(value: String): QrContent {
        val warning = analyzeUrlSecurity(value)
        return QrContent(
            rawValue = value,
            type = QrContentType.URL,
            displayValue = value.removePrefix("https://").removePrefix("http://").take(50),
            securityWarning = warning
        )
    }
    
    private fun analyzeUrlSecurity(url: String): SecurityWarning? {
        val lowerUrl = url.lowercase()
        
        if (lowerUrl.startsWith("http://")) {
            return SecurityWarning.INSECURE_HTTP
        }
        
        SHORT_URL_DOMAINS.forEach { domain ->
            if (lowerUrl.contains(domain)) {
                return SecurityWarning.SHORT_URL
            }
        }
        
        SUSPICIOUS_PATTERNS.forEach { pattern ->
            if (lowerUrl.contains(pattern) && !lowerUrl.contains("google.com") && 
                !lowerUrl.contains("microsoft.com") && !lowerUrl.contains("apple.com")) {
                val domain = extractDomain(url)
                if (domain != null && !isKnownSafeDomain(domain)) {
                    return SecurityWarning.SUSPICIOUS_URL
                }
            }
        }
        
        return null
    }
    
    private fun extractDomain(url: String): String? {
        return try {
            val withoutProtocol = url.removePrefix("https://").removePrefix("http://")
            withoutProtocol.split("/").firstOrNull()?.split(":")?.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
    
    private fun isKnownSafeDomain(domain: String): Boolean {
        val safeDomains = listOf(
            "google.com", "microsoft.com", "apple.com", "amazon.com",
            "facebook.com", "twitter.com", "instagram.com", "linkedin.com",
            "github.com", "stackoverflow.com", "wikipedia.org"
        )
        return safeDomains.any { domain.endsWith(it) }
    }
    
    private fun parseWifi(value: String): QrContent {
        val parts = value.removePrefix("WIFI:").removeSuffix(";").split(";")
        var ssid = ""
        var password: String? = null
        var security = "WPA"
        
        parts.forEach { part ->
            when {
                part.startsWith("S:") -> ssid = part.removePrefix("S:")
                part.startsWith("P:") -> password = part.removePrefix("P:")
                part.startsWith("T:") -> security = part.removePrefix("T:")
            }
        }
        
        return QrContent(
            rawValue = value,
            type = QrContentType.WIFI,
            displayValue = ssid.ifEmpty { "Wi-Fi Ağı" },
            parsedData = ParsedQrData.WifiData(
                ssid = ssid,
                password = password,
                securityType = security
            )
        )
    }
    
    private fun parseEmail(value: String): QrContent {
        val withoutPrefix = value.removePrefix("mailto:")
        val parts = withoutPrefix.split("?")
        val address = parts[0]
        var subject: String? = null
        var body: String? = null
        
        if (parts.size > 1) {
            val params = parts[1].split("&")
            params.forEach { param ->
                when {
                    param.startsWith("subject=") -> subject = param.removePrefix("subject=")
                    param.startsWith("body=") -> body = param.removePrefix("body=")
                }
            }
        }
        
        return QrContent(
            rawValue = value,
            type = QrContentType.EMAIL,
            displayValue = address,
            parsedData = ParsedQrData.EmailData(
                address = address,
                subject = subject,
                body = body
            )
        )
    }
    
    private fun parsePhone(value: String): QrContent {
        val number = value.removePrefix("tel:")
        return QrContent(
            rawValue = value,
            type = QrContentType.PHONE,
            displayValue = number,
            parsedData = ParsedQrData.PhoneData(number = number)
        )
    }
    
    private fun parseSms(value: String): QrContent {
        val withoutPrefix = value.removePrefix("smsto:").removePrefix("sms:")
        val parts = withoutPrefix.split(":", limit = 2)
        val number = parts[0]
        val message = if (parts.size > 1) parts[1] else null
        
        return QrContent(
            rawValue = value,
            type = QrContentType.SMS,
            displayValue = number,
            parsedData = ParsedQrData.SmsData(
                number = number,
                message = message
            )
        )
    }
    
    private fun parseGeo(value: String): QrContent {
        val match = GEO_REGEX.find(value)
        val lat = match?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0
        val lng = match?.groupValues?.get(2)?.toDoubleOrNull() ?: 0.0
        val label = match?.groupValues?.get(3)
        
        return QrContent(
            rawValue = value,
            type = QrContentType.GEO,
            displayValue = label ?: "$lat, $lng",
            parsedData = ParsedQrData.GeoData(
                latitude = lat,
                longitude = lng,
                label = label
            )
        )
    }
    
    private fun parseVCard(value: String): QrContent {
        var name: String? = null
        var phone: String? = null
        var email: String? = null
        var org: String? = null
        
        value.lines().forEach { line ->
            when {
                line.startsWith("FN:") -> name = line.removePrefix("FN:")
                line.startsWith("N:") && name == null -> {
                    val nameParts = line.removePrefix("N:").split(";")
                    name = "${nameParts.getOrNull(1) ?: ""} ${nameParts.getOrNull(0) ?: ""}".trim()
                }
                line.startsWith("TEL") -> phone = line.substringAfter(":")
                line.startsWith("EMAIL") -> email = line.substringAfter(":")
                line.startsWith("ORG:") -> org = line.removePrefix("ORG:")
            }
        }
        
        return QrContent(
            rawValue = value,
            type = QrContentType.CONTACT,
            displayValue = name ?: "Kişi",
            parsedData = ParsedQrData.ContactData(
                name = name,
                phone = phone,
                email = email,
                organization = org
            )
        )
    }
}
