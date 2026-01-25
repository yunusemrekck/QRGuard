package com.example.qrguard.ui.screens.create

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qrguard.R
import com.example.qrguard.generator.QrType
import com.example.qrguard.generator.WifiSecurityType
import com.example.qrguard.ui.components.GradientBackground
import com.example.qrguard.ui.theme.AccentBlue
import com.example.qrguard.ui.theme.CardBackground
import com.example.qrguard.ui.theme.QrBackgroundOptions
import com.example.qrguard.ui.theme.QrColorOptions
import com.example.qrguard.ui.theme.TextMuted
import com.example.qrguard.ui.theme.TextPrimary
import com.example.qrguard.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateViewModel = viewModel()
) {
    val context = LocalContext.current
    val selectedType by viewModel.selectedType.collectAsState()
    val qrBitmap by viewModel.qrBitmap.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val foregroundColor by viewModel.foregroundColor.collectAsState()
    val backgroundColor by viewModel.backgroundColor.collectAsState()
    
    LaunchedEffect(saveSuccess) {
        saveSuccess?.let { success ->
            val message = if (success) R.string.qr_saved else R.string.qr_save_failed
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearSaveStatus()
        }
    }

    GradientBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Geri",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.create_title),
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Type Selection
            item {
                Text(
                    text = stringResource(R.string.select_qr_type),
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QrType.entries.forEach { type ->
                        QrTypeChip(
                            type = type,
                            isSelected = selectedType == type,
                            onClick = { viewModel.selectType(type) }
                        )
                    }
                }
            }
            
            // Form
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        QrForm(
                            type = selectedType,
                            viewModel = viewModel
                        )
                    }
                }
            }
            
            // Color Customization
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.customize),
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                
                ColorSelectionSection(
                    title = stringResource(R.string.qr_color),
                    colors = QrColorOptions,
                    selectedColor = foregroundColor,
                    onColorSelected = viewModel::updateForegroundColor
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                ColorSelectionSection(
                    title = stringResource(R.string.background_color),
                    colors = QrBackgroundOptions,
                    selectedColor = backgroundColor,
                    onColorSelected = viewModel::updateBackgroundColor
                )
            }
            
            // Generate Button
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.generateQr() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    enabled = viewModel.isFormValid() && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.generate_qr),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            
            // QR Preview
            if (qrBitmap != null) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    QrPreviewSection(
                        bitmap = qrBitmap!!,
                        onSave = { viewModel.saveToGallery(context) },
                        onShare = { viewModel.shareQr(context) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QrTypeChip(
    type: QrType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) type.color.copy(alpha = 0.2f)
                else CardBackground.copy(alpha = 0.5f)
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) type.color else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = type.icon,
            contentDescription = null,
            tint = if (isSelected) type.color else TextMuted,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = stringResource(type.labelResId),
            color = if (isSelected) TextPrimary else TextSecondary,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QrForm(
    type: QrType,
    viewModel: CreateViewModel
) {
    when (type) {
        QrType.TEXT -> {
            val text by viewModel.textInput.collectAsState()
            QrTextField(
                value = text,
                onValueChange = viewModel::updateTextInput,
                label = stringResource(R.string.label_text),
                placeholder = stringResource(R.string.placeholder_text),
                maxLines = 5
            )
        }
        QrType.URL -> {
            val url by viewModel.urlInput.collectAsState()
            QrTextField(
                value = url,
                onValueChange = viewModel::updateUrlInput,
                label = stringResource(R.string.label_url),
                placeholder = stringResource(R.string.placeholder_url),
                keyboardType = KeyboardType.Uri
            )
        }
        QrType.WIFI -> {
            val ssid by viewModel.wifiSsid.collectAsState()
            val password by viewModel.wifiPassword.collectAsState()
            val security by viewModel.wifiSecurity.collectAsState()
            
            QrTextField(
                value = ssid,
                onValueChange = viewModel::updateWifiSsid,
                label = stringResource(R.string.label_wifi_ssid),
                placeholder = stringResource(R.string.placeholder_wifi_ssid)
            )
            Spacer(modifier = Modifier.height(12.dp))
            QrTextField(
                value = password,
                onValueChange = viewModel::updateWifiPassword,
                label = stringResource(R.string.label_wifi_password),
                placeholder = stringResource(R.string.placeholder_wifi_password),
                keyboardType = KeyboardType.Password
            )
            Spacer(modifier = Modifier.height(12.dp))
            WifiSecurityDropdown(
                selected = security,
                onSelected = viewModel::updateWifiSecurity
            )
        }
        QrType.EMAIL -> {
            val email by viewModel.emailAddress.collectAsState()
            val subject by viewModel.emailSubject.collectAsState()
            val body by viewModel.emailBody.collectAsState()
            
            QrTextField(
                value = email,
                onValueChange = viewModel::updateEmailAddress,
                label = stringResource(R.string.label_email_address),
                placeholder = stringResource(R.string.placeholder_email),
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(12.dp))
            QrTextField(
                value = subject,
                onValueChange = viewModel::updateEmailSubject,
                label = stringResource(R.string.label_email_subject),
                placeholder = "Konu (opsiyonel)"
            )
            Spacer(modifier = Modifier.height(12.dp))
            QrTextField(
                value = body,
                onValueChange = viewModel::updateEmailBody,
                label = stringResource(R.string.label_email_body),
                placeholder = "Mesaj (opsiyonel)",
                maxLines = 3
            )
        }
        QrType.PHONE -> {
            val phone by viewModel.phoneNumber.collectAsState()
            QrTextField(
                value = phone,
                onValueChange = viewModel::updatePhoneNumber,
                label = stringResource(R.string.label_phone_number),
                placeholder = stringResource(R.string.placeholder_phone),
                keyboardType = KeyboardType.Phone
            )
        }
        QrType.SMS -> {
            val number by viewModel.smsNumber.collectAsState()
            val message by viewModel.smsMessage.collectAsState()
            
            QrTextField(
                value = number,
                onValueChange = viewModel::updateSmsNumber,
                label = stringResource(R.string.label_sms_number),
                placeholder = stringResource(R.string.placeholder_phone),
                keyboardType = KeyboardType.Phone
            )
            Spacer(modifier = Modifier.height(12.dp))
            QrTextField(
                value = message,
                onValueChange = viewModel::updateSmsMessage,
                label = stringResource(R.string.label_sms_message),
                placeholder = "Mesaj (opsiyonel)",
                maxLines = 3
            )
        }
        QrType.CONTACT -> {
            val name by viewModel.contactName.collectAsState()
            val phone by viewModel.contactPhone.collectAsState()
            val email by viewModel.contactEmail.collectAsState()
            val company by viewModel.contactCompany.collectAsState()
            
            QrTextField(
                value = name,
                onValueChange = viewModel::updateContactName,
                label = stringResource(R.string.label_contact_name),
                placeholder = stringResource(R.string.placeholder_name)
            )
            Spacer(modifier = Modifier.height(12.dp))
            QrTextField(
                value = phone,
                onValueChange = viewModel::updateContactPhone,
                label = stringResource(R.string.label_contact_phone),
                placeholder = stringResource(R.string.placeholder_phone),
                keyboardType = KeyboardType.Phone
            )
            Spacer(modifier = Modifier.height(12.dp))
            QrTextField(
                value = email,
                onValueChange = viewModel::updateContactEmail,
                label = stringResource(R.string.label_contact_email),
                placeholder = stringResource(R.string.placeholder_email),
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(12.dp))
            QrTextField(
                value = company,
                onValueChange = viewModel::updateContactCompany,
                label = stringResource(R.string.label_contact_company),
                placeholder = "Şirket (opsiyonel)"
            )
        }
        QrType.LOCATION -> {
            val lat by viewModel.geoLatitude.collectAsState()
            val lng by viewModel.geoLongitude.collectAsState()
            val label by viewModel.geoLabel.collectAsState()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QrTextField(
                    value = lat,
                    onValueChange = viewModel::updateGeoLatitude,
                    label = stringResource(R.string.label_geo_latitude),
                    placeholder = "41.0082",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
                QrTextField(
                    value = lng,
                    onValueChange = viewModel::updateGeoLongitude,
                    label = stringResource(R.string.label_geo_longitude),
                    placeholder = "28.9784",
                    keyboardType = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            QrTextField(
                value = label,
                onValueChange = viewModel::updateGeoLabel,
                label = stringResource(R.string.label_geo_label),
                placeholder = "İstanbul (opsiyonel)"
            )
        }
        QrType.EVENT -> {
            Text(
                text = "Etkinlik özelliği yakında eklenecek",
                color = TextMuted,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun QrTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = TextMuted) },
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBlue,
            unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
            focusedLabelColor = AccentBlue,
            unfocusedLabelColor = TextSecondary,
            cursorColor = AccentBlue,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        maxLines = maxLines,
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WifiSecurityDropdown(
    selected: WifiSecurityType,
    onSelected: (WifiSecurityType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = when (selected) {
                WifiSecurityType.WPA -> stringResource(R.string.wifi_security_wpa)
                WifiSecurityType.WEP -> stringResource(R.string.wifi_security_wep)
                WifiSecurityType.NONE -> stringResource(R.string.wifi_security_none)
            },
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_wifi_security)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            WifiSecurityType.entries.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Text(
                            when (type) {
                                WifiSecurityType.WPA -> stringResource(R.string.wifi_security_wpa)
                                WifiSecurityType.WEP -> stringResource(R.string.wifi_security_wep)
                                WifiSecurityType.NONE -> stringResource(R.string.wifi_security_none)
                            }
                        )
                    },
                    onClick = {
                        onSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ColorSelectionSection(
    title: String,
    colors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = title,
            color = TextMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(colors) { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (selectedColor == color) 3.dp else 1.dp,
                            color = if (selectedColor == color) AccentBlue else TextMuted.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(color) },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedColor == color) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (color == Color.White || color == Color(0xFFFFF8E1)) Color.Black else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QrPreviewSection(
    bitmap: Bitmap,
    onSave: () -> Unit,
    onShare: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.qr_preview),
            color = TextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier
                    .size(220.dp)
                    .padding(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilledTonalButton(
                onClick = onSave,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = CardBackground
                )
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.save))
            }
            
            Button(
                onClick = onShare,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue
                )
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.share))
            }
        }
    }
}
