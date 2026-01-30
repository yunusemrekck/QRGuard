package com.yunusek.qrguard.domain.model

import androidx.compose.material.icons.Icons
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

enum class QrContentType(
    val labelResId: Int,
    val icon: ImageVector,
    val color: Color
) {
    URL(
        labelResId = R.string.type_url,
        icon = Icons.Default.Link,
        color = Color(0xFF2196F3)
    ),
    TEXT(
        labelResId = R.string.type_text,
        icon = Icons.Default.TextFields,
        color = Color(0xFF9C27B0)
    ),
    WIFI(
        labelResId = R.string.type_wifi,
        icon = Icons.Default.Wifi,
        color = Color(0xFF4CAF50)
    ),
    EMAIL(
        labelResId = R.string.type_email,
        icon = Icons.Default.Email,
        color = Color(0xFFFF9800)
    ),
    PHONE(
        labelResId = R.string.type_phone,
        icon = Icons.Default.Phone,
        color = Color(0xFF00BCD4)
    ),
    SMS(
        labelResId = R.string.type_sms,
        icon = Icons.Default.Sms,
        color = Color(0xFFE91E63)
    ),
    CONTACT(
        labelResId = R.string.type_contact,
        icon = Icons.Default.ContactPhone,
        color = Color(0xFF3F51B5)
    ),
    GEO(
        labelResId = R.string.type_geo,
        icon = Icons.Default.LocationOn,
        color = Color(0xFFF44336)
    )
}
