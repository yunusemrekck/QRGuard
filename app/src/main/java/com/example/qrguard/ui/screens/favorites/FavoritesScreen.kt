package com.example.qrguard.ui.screens.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.qrguard.ui.components.GradientBackground
import com.example.qrguard.ui.components.ResultBottomSheet
import com.example.qrguard.ui.components.ScanHistoryItem
import com.example.qrguard.ui.theme.*

@Composable
fun FavoritesScreen(
    onNavigateBack: () -> Unit,
    viewModel: FavoritesViewModel = viewModel()
) {
    val favorites by viewModel.favorites.collectAsState()
    val selectedQrContent by viewModel.selectedQrContent.collectAsState()

    GradientBackground {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = onNavigateBack, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.ArrowBack, "Geri", tint = TextPrimary)
                }
                Text(
                    text = stringResource(R.string.favorites_title),
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (favorites.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.il_empty_favorites),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(stringResource(R.string.favorites_empty), color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.favorites_empty_desc), color = TextMuted, fontSize = 14.sp, textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = favorites, key = { it.id }) { scan ->
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
}