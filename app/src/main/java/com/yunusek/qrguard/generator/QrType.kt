package com.yunusek.qrguard.generator

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.yunusek.qrguard.R

enum class QrType(
    @StringRes val labelResId: Int,
    val icon: ImageVector,
    val color: Color,
    val description: String
) {
    TEXT(
        labelResId = R.string.type_text,
        icon = Icons.Default.TextFields,
        color = Color(0xFF9C27B0),
        description = "Serbest metin"
    ),
    URL(
        labelResId = R.string.type_url,
        icon = Icons.Default.Link,
        color = Color(0xFF2196F3),
        description = "Web adresi"
    ),
    WIFI(
        labelResId = R.string.type_wifi,
        icon = Icons.Default.Wifi,
        color = Color(0xFF4CAF50),
        description = "Wi-Fi ağ bilgileri"
    ),
    EMAIL(
        labelResId = R.string.type_email,
        icon = Icons.Default.Email,
        color = Color(0xFFFF9800),
        description = "E-posta adresi"
    ),
    PHONE(
        labelResId = R.string.type_phone,
        icon = Icons.Default.Phone,
        color = Color(0xFF00BCD4),
        description = "Telefon numarası"
    ),
    SMS(
        labelResId = R.string.type_sms,
        icon = Icons.Default.Sms,
        color = Color(0xFFE91E63),
        description = "SMS mesajı"
    ),
    CONTACT(
        labelResId = R.string.type_contact,
        icon = Icons.Default.ContactPhone,
        color = Color(0xFF3F51B5),
        description = "Kişi kartı (vCard)"
    ),
    LOCATION(
        labelResId = R.string.type_geo,
        icon = Icons.Default.LocationOn,
        color = Color(0xFFF44336),
        description = "Konum koordinatları"
    ),
    EVENT(
        labelResId = R.string.type_event,
        icon = Icons.Default.CalendarMonth,
        color = Color(0xFF795548),
        description = "Takvim etkinliği"
    )
}
