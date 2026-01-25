package com.example.qrguard.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qrguard.R
import com.example.qrguard.domain.model.ParsedQrData
import com.example.qrguard.domain.model.QrContent
import com.example.qrguard.domain.model.QrContentType
import com.example.qrguard.domain.model.SecurityWarning
import com.example.qrguard.ui.theme.AccentBlue
import com.example.qrguard.ui.theme.CardBackground
import com.example.qrguard.ui.theme.FavoriteActive
import com.example.qrguard.ui.theme.FavoriteInactive
import com.example.qrguard.ui.theme.GradientStart
import com.example.qrguard.ui.theme.TextMuted
import com.example.qrguard.ui.theme.TextPrimary
import com.example.qrguard.ui.theme.TextSecondary
import com.example.qrguard.ui.theme.WarningOrange
import com.example.qrguard.ui.theme.WarningRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultBottomSheet(
    qrContent: QrContent,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = GradientStart,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Başlık ve Favori
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.scan_result),
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (qrContent.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = if (qrContent.isFavorite) FavoriteActive else FavoriteInactive,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tür göstergesi
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(qrContent.type.color.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = qrContent.type.icon,
                    contentDescription = null,
                    tint = qrContent.type.color,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = stringResource(qrContent.type.labelResId),
                    color = qrContent.type.color,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Güvenlik Uyarısı
            qrContent.securityWarning?.let { warning ->
                SecurityWarningBanner(warning = warning)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Ham içerik
            Surface(
                color = CardBackground,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = qrContent.rawValue,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        maxLines = 6,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Parsed data varsa göster
                    qrContent.parsedData?.let { data ->
                        Spacer(modifier = Modifier.height(12.dp))
                        ParsedDataSection(data = data)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Aksiyon butonları
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { copyToClipboard(context, qrContent.rawValue) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    )
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.copy), fontSize = 13.sp)
                }

                FilledTonalButton(
                    onClick = { shareContent(context, qrContent.rawValue) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = CardBackground,
                        contentColor = TextPrimary
                    )
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.share), fontSize = 13.sp)
                }
            }

            // Türe özel aksiyon butonu
            Spacer(modifier = Modifier.height(8.dp))
            TypeSpecificActionButton(
                qrContent = qrContent,
                context = context
            )
        }
    }
}

@Composable
private fun SecurityWarningBanner(warning: SecurityWarning) {
    val (text, color) = when (warning) {
        SecurityWarning.INSECURE_HTTP -> stringResource(R.string.warning_insecure_url) to WarningOrange
        SecurityWarning.SUSPICIOUS_URL -> stringResource(R.string.warning_suspicious_url) to WarningRed
        SecurityWarning.SHORT_URL -> stringResource(R.string.warning_short_url) to WarningOrange
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ParsedDataSection(data: ParsedQrData) {
    when (data) {
        is ParsedQrData.WifiData -> {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ParsedDataRow("Ağ Adı", data.ssid)
                data.password?.let { ParsedDataRow("Şifre", it) }
                ParsedDataRow("Güvenlik", data.securityType)
            }
        }
        is ParsedQrData.EmailData -> {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ParsedDataRow("E-posta", data.address)
                data.subject?.let { ParsedDataRow("Konu", it) }
            }
        }
        is ParsedQrData.PhoneData -> {
            ParsedDataRow("Telefon", data.number)
        }
        is ParsedQrData.SmsData -> {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ParsedDataRow("Telefon", data.number)
                data.message?.let { ParsedDataRow("Mesaj", it) }
            }
        }
        is ParsedQrData.GeoData -> {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                data.label?.let { ParsedDataRow("Konum", it) }
                ParsedDataRow("Koordinat", "${data.latitude}, ${data.longitude}")
            }
        }
        is ParsedQrData.ContactData -> {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                data.name?.let { ParsedDataRow("Ad", it) }
                data.phone?.let { ParsedDataRow("Telefon", it) }
                data.email?.let { ParsedDataRow("E-posta", it) }
                data.organization?.let { ParsedDataRow("Kurum", it) }
            }
        }
    }
}

@Composable
private fun ParsedDataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TypeSpecificActionButton(
    qrContent: QrContent,
    context: Context
) {
    when (qrContent.type) {
        QrContentType.URL -> {
            Button(
                onClick = { openUrl(context, qrContent.rawValue) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue
                )
            ) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.open_url))
            }
        }
        QrContentType.WIFI -> {
            val wifiData = qrContent.parsedData as? ParsedQrData.WifiData
            Button(
                onClick = { /* TODO: Wi-Fi bağlantısı */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = qrContent.type.color
                )
            ) {
                Icon(Icons.Default.Wifi, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.connect_wifi))
            }
        }
        QrContentType.PHONE -> {
            val phoneData = qrContent.parsedData as? ParsedQrData.PhoneData
            Button(
                onClick = { 
                    phoneData?.let { dialPhone(context, it.number) }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = qrContent.type.color
                )
            ) {
                Icon(qrContent.type.icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.call))
            }
        }
        QrContentType.EMAIL -> {
            val emailData = qrContent.parsedData as? ParsedQrData.EmailData
            Button(
                onClick = { 
                    emailData?.let { sendEmail(context, it.address, it.subject, it.body) }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = qrContent.type.color
                )
            ) {
                Icon(qrContent.type.icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.send_email))
            }
        }
        QrContentType.SMS -> {
            val smsData = qrContent.parsedData as? ParsedQrData.SmsData
            Button(
                onClick = { 
                    smsData?.let { sendSms(context, it.number, it.message) }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = qrContent.type.color
                )
            ) {
                Icon(qrContent.type.icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.send_sms))
            }
        }
        QrContentType.GEO -> {
            val geoData = qrContent.parsedData as? ParsedQrData.GeoData
            Button(
                onClick = { 
                    geoData?.let { openMap(context, it.latitude, it.longitude, it.label) }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = qrContent.type.color
                )
            ) {
                Icon(qrContent.type.icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.open_map))
            }
        }
        else -> { /* No specific action for TEXT and CONTACT */ }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("QR Content", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
}

private fun shareContent(context: Context, text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

private fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "URL açılamadı", Toast.LENGTH_SHORT).show()
    }
}

private fun dialPhone(context: Context, number: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Arama yapılamadı", Toast.LENGTH_SHORT).show()
    }
}

private fun sendEmail(context: Context, address: String, subject: String?, body: String?) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
            body?.let { putExtra(Intent.EXTRA_TEXT, it) }
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "E-posta gönderilemedi", Toast.LENGTH_SHORT).show()
    }
}

private fun sendSms(context: Context, number: String, message: String?) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$number")
            message?.let { putExtra("sms_body", it) }
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "SMS gönderilemedi", Toast.LENGTH_SHORT).show()
    }
}

private fun openMap(context: Context, lat: Double, lng: Double, label: String?) {
    try {
        val uri = if (label != null) {
            Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
        } else {
            Uri.parse("geo:$lat,$lng?q=$lat,$lng")
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Harita açılamadı", Toast.LENGTH_SHORT).show()
    }
}
