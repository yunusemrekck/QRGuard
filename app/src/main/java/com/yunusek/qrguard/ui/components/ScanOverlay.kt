package com.yunusek.qrguard.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import com.yunusek.qrguard.ui.theme.AccentBlue
import com.yunusek.qrguard.ui.theme.AccentCyan
import com.yunusek.qrguard.ui.theme.ScannerOverlay

@Composable
fun ScanOverlay(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val scanLinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanLine"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Tarama çerçevesi boyutları
        val frameSize = minOf(canvasWidth, canvasHeight) * 0.7f
        val frameLeft = (canvasWidth - frameSize) / 2
        val frameTop = (canvasHeight - frameSize) / 2
        val cornerRadius = 24.dp.toPx()

        // Çerçeve path
        val framePath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        offset = Offset(frameLeft, frameTop),
                        size = Size(frameSize, frameSize)
                    ),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            )
        }

        // Karartılmış alan (çerçeve dışı)
        clipPath(framePath, clipOp = ClipOp.Difference) {
            drawRect(color = ScannerOverlay)
        }

        // Köşe çizgileri
        val cornerLength = 40.dp.toPx()
        val strokeWidth = 4.dp.toPx()
        val cornerColor = AccentCyan

        // Sol üst köşe
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft + cornerRadius, frameTop),
            end = Offset(frameLeft + cornerRadius + cornerLength, frameTop),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft, frameTop + cornerRadius),
            end = Offset(frameLeft, frameTop + cornerRadius + cornerLength),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Sağ üst köşe
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft + frameSize - cornerRadius - cornerLength, frameTop),
            end = Offset(frameLeft + frameSize - cornerRadius, frameTop),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft + frameSize, frameTop + cornerRadius),
            end = Offset(frameLeft + frameSize, frameTop + cornerRadius + cornerLength),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Sol alt köşe
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft + cornerRadius, frameTop + frameSize),
            end = Offset(frameLeft + cornerRadius + cornerLength, frameTop + frameSize),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft, frameTop + frameSize - cornerRadius - cornerLength),
            end = Offset(frameLeft, frameTop + frameSize - cornerRadius),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Sağ alt köşe
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft + frameSize - cornerRadius - cornerLength, frameTop + frameSize),
            end = Offset(frameLeft + frameSize - cornerRadius, frameTop + frameSize),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = cornerColor,
            start = Offset(frameLeft + frameSize, frameTop + frameSize - cornerRadius - cornerLength),
            end = Offset(frameLeft + frameSize, frameTop + frameSize - cornerRadius),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // İnce çerçeve kenarlığı
        drawRoundRect(
            color = Color.White.copy(alpha = 0.3f),
            topLeft = Offset(frameLeft, frameTop),
            size = Size(frameSize, frameSize),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
            style = Stroke(width = 1.dp.toPx())
        )

        // Animasyonlu tarama çizgisi
        val scanLineY = frameTop + cornerRadius + (frameSize - 2 * cornerRadius) * scanLinePosition
        val scanLineGradient = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                AccentCyan.copy(alpha = 0.8f),
                AccentBlue,
                AccentCyan.copy(alpha = 0.8f),
                Color.Transparent
            ),
            startX = frameLeft + cornerRadius,
            endX = frameLeft + frameSize - cornerRadius
        )
        
        drawLine(
            brush = scanLineGradient,
            start = Offset(frameLeft + cornerRadius, scanLineY),
            end = Offset(frameLeft + frameSize - cornerRadius, scanLineY),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
