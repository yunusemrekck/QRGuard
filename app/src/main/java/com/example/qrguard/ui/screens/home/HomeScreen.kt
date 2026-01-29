package com.example.qrguard.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qrguard.R
import com.example.qrguard.domain.model.QrContentType
import com.example.qrguard.ui.components.CompactActionCard
import com.example.qrguard.ui.components.CreateButton
import com.example.qrguard.ui.components.GradientBackground
import com.example.qrguard.ui.components.QuickAccessChip
import com.example.qrguard.ui.components.QrCodeIcon
import com.example.qrguard.ui.components.RecentScanItem
import com.example.qrguard.ui.components.ResultBottomSheet
import com.example.qrguard.ui.components.ScanButton
import com.example.qrguard.ui.theme.AccentBlue
import com.example.qrguard.ui.theme.AccentGold
import com.example.qrguard.ui.theme.CardBackground
import com.example.qrguard.ui.theme.TextMuted
import com.example.qrguard.ui.theme.TextPrimary
import com.example.qrguard.ui.theme.TextSecondary
import com.example.qrguard.ui.theme.TypeEmailColor
import com.example.qrguard.ui.theme.TypeTextColor
import com.example.qrguard.ui.theme.TypeUrlColor
import com.example.qrguard.ui.theme.TypeWifiColor

@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onCreateClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val recentScans by viewModel.recentScans.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val favoritesCount by viewModel.favoritesCount.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedQrContent by viewModel.selectedQrContent.collectAsState()

    GradientBackground {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Header
            item {
                HomeHeader(onSettingsClick = onSettingsClick)
            }

            // Main Action Buttons (Scan & Create)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ScanButton(
                        onClick = onScanClick,
                        modifier = Modifier.weight(1f)
                    )
                    CreateButton(
                        onClick = onCreateClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Action Cards (Favorites & History)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CompactActionCard(
                        icon = Icons.Default.Star,
                        title = stringResource(R.string.favorites),
                        count = favoritesCount,
                        backgroundColor = CardBackground.copy(alpha = 0.7f),
                        iconColor = AccentGold,
                        onClick = onFavoritesClick,
                        modifier = Modifier.weight(1f)
                    )

                    CompactActionCard(
                        icon = Icons.Default.History,
                        title = stringResource(R.string.history),
                        count = totalCount,
                        backgroundColor = CardBackground.copy(alpha = 0.7f),
                        iconColor = AccentBlue,
                        onClick = onHistoryClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Access Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                QuickAccessSection(
                    selectedFilter = selectedFilter,
                    onFilterSelected = viewModel::onFilterSelected
                )
            }

            // Recent Scans Section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                RecentScansHeader(onSeeAllClick = onHistoryClick)
            }

            // Recent Scans List
            if (recentScans.isEmpty()) {
                item {
                    EmptyRecentScans()
                }
            } else {
                items(
                    items = recentScans.filter { scan ->
                        selectedFilter == null || scan.type == selectedFilter
                    },
                    key = { it.id }
                ) { scan ->
                    RecentScanItem(
                        scan = scan,
                        onClick = { viewModel.onScanItemClick(scan) },
                        onFavoriteClick = { viewModel.toggleFavorite(scan) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    selectedQrContent?.let { qrContent ->
        ResultBottomSheet(
            qrContent = qrContent,
            isVisible = true,
            onDismiss = viewModel::dismissBottomSheet,
            onFavoriteClick = { viewModel.toggleFavorite(qrContent) }
        )
    }
}

@Composable
private fun HomeHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "QR Guard",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.home_subtitle),
                color = TextSecondary,
                fontSize = 14.sp
            )
        }

        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings),
                tint = TextSecondary
            )
        }
    }
}

@Composable
private fun QuickAccessSection(
    selectedFilter: QrContentType?,
    onFilterSelected: (QrContentType?) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.quick_access),
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                QuickAccessChip(
                    icon = QrContentType.WIFI.icon,
                    label = stringResource(R.string.type_wifi),
                    iconTint = TypeWifiColor,
                    onClick = { onFilterSelected(QrContentType.WIFI) },
                    isSelected = selectedFilter == QrContentType.WIFI
                )
            }
            item {
                QuickAccessChip(
                    icon = QrContentType.URL.icon,
                    label = stringResource(R.string.type_url),
                    iconTint = TypeUrlColor,
                    onClick = { onFilterSelected(QrContentType.URL) },
                    isSelected = selectedFilter == QrContentType.URL
                )
            }
            item {
                QuickAccessChip(
                    icon = QrContentType.TEXT.icon,
                    label = stringResource(R.string.type_text),
                    iconTint = TypeTextColor,
                    onClick = { onFilterSelected(QrContentType.TEXT) },
                    isSelected = selectedFilter == QrContentType.TEXT
                )
            }
            item {
                QuickAccessChip(
                    icon = QrContentType.EMAIL.icon,
                    label = stringResource(R.string.type_email),
                    iconTint = TypeEmailColor,
                    onClick = { onFilterSelected(QrContentType.EMAIL) },
                    isSelected = selectedFilter == QrContentType.EMAIL
                )
            }
        }
    }
}

@Composable
private fun RecentScansHeader(onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.recent_scans),
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        TextButton(onClick = onSeeAllClick) {
            Text(
                text = stringResource(R.string.see_all),
                color = AccentBlue,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun EmptyRecentScans() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.il_empty_history),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_recent_scans),
            color = TextMuted,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}