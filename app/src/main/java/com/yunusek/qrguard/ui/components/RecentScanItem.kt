package com.yunusek.qrguard.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunusek.qrguard.R
import com.yunusek.qrguard.domain.model.QrContent
import com.yunusek.qrguard.ui.theme.CardBackground
import com.yunusek.qrguard.ui.theme.FavoriteActive
import com.yunusek.qrguard.ui.theme.FavoriteInactive
import com.yunusek.qrguard.ui.theme.TextMuted
import com.yunusek.qrguard.ui.theme.TextPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

@Composable
fun RecentScanItem(
    scan: QrContent,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBackground.copy(alpha = 0.6f))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // İkon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(scan.type.color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = scan.type.icon,
                contentDescription = null,
                tint = scan.type.color,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // İçerik
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = scan.displayValue,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tür etiketi
                Text(
                    text = scan.type.name,
                    color = scan.type.color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "•",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                // Zaman
                Text(
                    text = formatRelativeTime(context, scan.timestamp),
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Favori butonu
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (scan.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (scan.isFavorite) FavoriteActive else FavoriteInactive,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun ScanHistoryItem(
    scan: QrContent,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // İkon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(scan.type.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = scan.type.icon,
                contentDescription = null,
                tint = scan.type.color,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = scan.displayValue,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatDateTime(context, scan.timestamp),
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (scan.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (scan.isFavorite) FavoriteActive else FavoriteInactive,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun formatRelativeTime(context: Context, timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> {
            context.getString(R.string.just_now)
        }
        diff < TimeUnit.HOURS.toMillis(1) -> {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
            context.getString(R.string.minutes_ago, minutes)
        }
        diff < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            context.getString(R.string.hours_ago, hours)
        }
        diff < TimeUnit.DAYS.toMillis(2) -> {
            context.getString(R.string.yesterday)
        }
        diff < TimeUnit.DAYS.toMillis(7) -> {
            val days = TimeUnit.MILLISECONDS.toDays(diff)
            context.getString(R.string.days_ago, days)
        }
        else -> {
            // System locale kullanarak format
            val locale = context.resources.configuration.locales[0]
            val sdf = SimpleDateFormat("dd MMM", locale)
            sdf.format(Date(timestamp))
        }
    }
}

private fun formatDateTime(context: Context, timestamp: Long): String {
    // System locale kullanarak format
    val locale = context.resources.configuration.locales[0]
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", locale)
    return sdf.format(Date(timestamp))
}