package com.example.qrguard.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qrguard.BuildConfig
import com.example.qrguard.R
import com.example.qrguard.data.preferences.AppLanguage
import com.example.qrguard.data.preferences.AppTheme
import com.example.qrguard.ui.components.GradientBackground
import com.example.qrguard.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()
    val theme by viewModel.theme.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val autoCopyEnabled by viewModel.autoCopyEnabled.collectAsState()
    
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    GradientBackground {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.back), tint = TextPrimary)
                    }
                    Text(
                        text = stringResource(R.string.settings_title),
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // General Section
            item {
                SettingsSectionHeader(title = stringResource(R.string.settings_general))
            }
            
            item {
                SettingsCard {
                    // Language
                    SettingsItem(
                        icon = Icons.Outlined.Language,
                        title = stringResource(R.string.settings_language),
                        subtitle = getLanguageDisplayName(language),
                        onClick = { showLanguageDialog = true }
                    )
                    
                    SettingsDivider()
                    
                    // Theme
                    SettingsItem(
                        icon = Icons.Outlined.Palette,
                        title = stringResource(R.string.settings_theme),
                        subtitle = getThemeDisplayName(theme),
                        onClick = { showThemeDialog = true }
                    )
                }
            }
            
            // Scan Settings Section
            item {
                SettingsSectionHeader(title = stringResource(R.string.settings_appearance))
            }
            
            item {
                SettingsCard {
                    // Vibration
                    SettingsSwitchItem(
                        icon = Icons.Outlined.Vibration,
                        title = stringResource(R.string.settings_vibration),
                        checked = vibrationEnabled,
                        onCheckedChange = viewModel::setVibrationEnabled
                    )
                    
                    SettingsDivider()
                    
                    // Sound
                    SettingsSwitchItem(
                        icon = Icons.Outlined.VolumeUp,
                        title = stringResource(R.string.settings_sound),
                        checked = soundEnabled,
                        onCheckedChange = viewModel::setSoundEnabled
                    )
                    
                    SettingsDivider()
                    
                    // Auto Copy
                    SettingsSwitchItem(
                        icon = Icons.Outlined.ContentCopy,
                        title = stringResource(R.string.settings_auto_copy),
                        checked = autoCopyEnabled,
                        onCheckedChange = viewModel::setAutoCopyEnabled
                    )
                }
            }
            
            // About Section
            item {
                SettingsSectionHeader(title = stringResource(R.string.settings_about))
            }
            
            item {
                SettingsCard {
                    // About App
                    SettingsItem(
                        icon = Icons.Outlined.Info,
                        title = stringResource(R.string.about_title),
                        subtitle = stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                        onClick = { showAboutDialog = true }
                    )
                    
                    SettingsDivider()
                    
                    // Rate App
                    SettingsItem(
                        icon = Icons.Outlined.Star,
                        title = stringResource(R.string.about_rate),
                        onClick = {
                            // Play Store link
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Play Store not installed
                            }
                        }
                    )
                    
                    SettingsDivider()
                    
                    // Share App
                    SettingsItem(
                        icon = Icons.Outlined.Share,
                        title = stringResource(R.string.about_share),
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "QR Guard - https://play.google.com/store/apps/details?id=${context.packageName}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, null))
                        }
                    )
                }
            }
            
            // Version Footer
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.about_copyright),
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    // Language Dialog
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = language,
            onLanguageSelected = {
                viewModel.setLanguage(it)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }
    
    // Theme Dialog
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = theme,
            onThemeSelected = {
                viewModel.setTheme(it)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }
    
    // About Dialog
    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = AccentBlue,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp), content = content)
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimary, fontSize = 15.sp)
            subtitle?.let {
                Text(text = it, color = TextMuted, fontSize = 13.sp)
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AccentBlue,
                checkedTrackColor = AccentBlue.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        color = TextMuted.copy(alpha = 0.2f)
    )
}

@Composable
private fun LanguageSelectionDialog(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_language), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                LanguageOption(
                    title = stringResource(R.string.settings_language_system),
                    isSelected = currentLanguage == AppLanguage.SYSTEM,
                    onClick = { onLanguageSelected(AppLanguage.SYSTEM) }
                )
                LanguageOption(
                    title = stringResource(R.string.settings_language_turkish),
                    isSelected = currentLanguage == AppLanguage.TURKISH,
                    onClick = { onLanguageSelected(AppLanguage.TURKISH) }
                )
                LanguageOption(
                    title = stringResource(R.string.settings_language_english),
                    isSelected = currentLanguage == AppLanguage.ENGLISH,
                    onClick = { onLanguageSelected(AppLanguage.ENGLISH) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
private fun LanguageOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = AccentBlue)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title)
    }
}

@Composable
private fun ThemeSelectionDialog(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_theme), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                ThemeOption(
                    title = stringResource(R.string.settings_theme_system),
                    isSelected = currentTheme == AppTheme.SYSTEM,
                    onClick = { onThemeSelected(AppTheme.SYSTEM) }
                )
                ThemeOption(
                    title = stringResource(R.string.settings_theme_light),
                    isSelected = currentTheme == AppTheme.LIGHT,
                    onClick = { onThemeSelected(AppTheme.LIGHT) }
                )
                ThemeOption(
                    title = stringResource(R.string.settings_theme_dark),
                    isSelected = currentTheme == AppTheme.DARK,
                    onClick = { onThemeSelected(AppTheme.DARK) }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
private fun ThemeOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = AccentBlue)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title)
    }
}

@Composable
private fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AccentBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.about_app_name),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                    color = TextMuted,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.about_description),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${stringResource(R.string.about_developer)}: ${stringResource(R.string.about_developer_name)}",
                    color = TextMuted,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.about_copyright),
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
private fun getLanguageDisplayName(language: AppLanguage): String {
    return when (language) {
        AppLanguage.SYSTEM -> stringResource(R.string.settings_language_system)
        AppLanguage.TURKISH -> stringResource(R.string.settings_language_turkish)
        AppLanguage.ENGLISH -> stringResource(R.string.settings_language_english)
    }
}

@Composable
private fun getThemeDisplayName(theme: AppTheme): String {
    return when (theme) {
        AppTheme.SYSTEM -> stringResource(R.string.settings_theme_system)
        AppTheme.LIGHT -> stringResource(R.string.settings_theme_light)
        AppTheme.DARK -> stringResource(R.string.settings_theme_dark)
    }
}
