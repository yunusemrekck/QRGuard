package com.example.qrguard.ui.screens.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.qrguard.ui.components.GradientBackground
import com.example.qrguard.ui.components.QuickAccessChip
import com.example.qrguard.ui.components.ResultBottomSheet
import com.example.qrguard.ui.components.ScanHistoryItem
import com.example.qrguard.ui.theme.*

@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = viewModel()
) {
    val filteredScans by viewModel.filteredScans.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val showClearDialog by viewModel.showClearDialog.collectAsState()
    val selectedQrContent by viewModel.selectedQrContent.collectAsState()

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.ArrowBack, "Geri", tint = TextPrimary)
                    }
                    Text(
                        text = stringResource(R.string.history_title),
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (filteredScans.isNotEmpty()) {
                    IconButton(onClick = viewModel::showClearHistoryDialog, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.DeleteSweep, stringResource(R.string.clear_history), tint = WarningRed)
                    }
                }
            }

            // Filters
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(QrContentType.entries) { type ->
                    QuickAccessChip(
                        icon = type.icon,
                        label = stringResource(type.labelResId),
                        iconTint = type.color,
                        onClick = { viewModel.onFilterSelected(type) },
                        isSelected = selectedFilter == type
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredScans.isEmpty()) {
                EmptyContent(
                    emoji = "📋",
                    title = stringResource(R.string.history_empty),
                    description = stringResource(R.string.history_empty_desc)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = filteredScans, key = { it.id }) { scan ->
                        ScanHistoryItem(
                            scan = scan,
                            onClick = { viewModel.onScanItemClick(scan) },
                            onFavoriteClick = { viewModel.toggleFavorite(scan) },
                            onDeleteClick = { viewModel.deleteScan(scan) }
                        )
                    }
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

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissClearHistoryDialog,
            title = { Text(stringResource(R.string.clear_history), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.clear_history_confirm)) },
            confirmButton = {
                TextButton(onClick = viewModel::clearAllHistory) {
                    Text(stringResource(R.string.confirm), color = WarningRed)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissClearHistoryDialog) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun EmptyContent(emoji: String, title: String, description: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.il_empty_history),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = description, color = TextMuted, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}