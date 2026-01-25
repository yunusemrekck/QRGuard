package com.example.qrguard.ui.screens.scan

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.qrguard.R
import com.example.qrguard.scanner.ScannerState
import com.example.qrguard.ui.components.CameraPreview
import com.example.qrguard.ui.components.ResultBottomSheet
import com.example.qrguard.ui.components.ScanOverlay
import com.example.qrguard.ui.theme.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onNavigateBack: () -> Unit,
    viewModel: ScanViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val scannerState by viewModel.scannerState.collectAsState()
    val isTorchEnabled by viewModel.isTorchEnabled.collectAsState()
    val currentScan by viewModel.currentScan.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible && scannerState is ScannerState.Result) {
            viewModel.onResultDismissed()
        }
    }

    LaunchedEffect(scannerState) {
        if (scannerState is ScannerState.Result) {
            sheetState.show()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = GradientStart
    ) {
        when {
            cameraPermissionState.status.isGranted -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        isTorchEnabled = isTorchEnabled,
                        onQrCodeDetected = viewModel::onQrCodeDetected
                    )

                    ScanOverlay(modifier = Modifier.fillMaxSize())

                    // Top bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CardBackground.copy(alpha = 0.7f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Geri",
                                tint = TextPrimary
                            )
                        }
                        
                        Text(
                            text = "QR Tara",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        IconButton(
                            onClick = viewModel::toggleTorch,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isTorchEnabled) AccentBlue
                                    else CardBackground.copy(alpha = 0.7f)
                                )
                        ) {
                            Icon(
                                imageVector = if (isTorchEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = stringResource(R.string.torch),
                                tint = TextPrimary
                            )
                        }
                    }

                    // Bottom bar
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = CardBackground.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.scan_hint),
                                color = TextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        IconButton(
                            onClick = viewModel::onGalleryClick,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(CardBackground.copy(alpha = 0.8f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = stringResource(R.string.gallery),
                                tint = TextPrimary,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Text(
                            text = stringResource(R.string.gallery),
                            color = TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }

                currentScan?.let { scan ->
                    if (scannerState is ScannerState.Result) {
                        ResultBottomSheet(
                            qrContent = scan,
                            sheetState = sheetState,
                            onDismiss = {
                                scope.launch {
                                    sheetState.hide()
                                    viewModel.onResultDismissed()
                                }
                            },
                            onFavoriteClick = viewModel::toggleFavorite
                        )
                    }
                }
            }

            cameraPermissionState.status.shouldShowRationale -> {
                PermissionContent(
                    title = stringResource(R.string.camera_permission_required),
                    description = stringResource(R.string.camera_permission_rationale),
                    buttonText = stringResource(R.string.grant_permission),
                    onButtonClick = { cameraPermissionState.launchPermissionRequest() },
                    onNavigateBack = onNavigateBack
                )
            }

            else -> {
                val isPermanentlyDenied = !cameraPermissionState.status.shouldShowRationale &&
                        !cameraPermissionState.status.isGranted
                        
                PermissionContent(
                    title = stringResource(R.string.camera_permission_required),
                    description = stringResource(R.string.camera_permission_rationale),
                    buttonText = if (isPermanentlyDenied) 
                        stringResource(R.string.open_settings) 
                    else 
                        stringResource(R.string.grant_permission),
                    onButtonClick = {
                        if (isPermanentlyDenied) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        } else {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    },
                    onNavigateBack = onNavigateBack
                )
            }
        }
    }
}

@Composable
private fun PermissionContent(
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "📸", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = description,
            color = TextSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onButtonClick) {
            Text(buttonText)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateBack) {
            Text("Geri Dön", color = TextMuted)
        }
    }
}
