package com.example.qrguard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun QrCodeIcon(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    color: Color = Color.White,
    strokeWidth: Float = 3f
) {
    Canvas(modifier = modifier.size(size)) {
        val canvasSize = this.size.minDimension
        val cellSize = canvasSize / 7
        val cornerRadius = CornerRadius(cellSize / 4, cellSize / 4)
        
        // Sol üst köşe deseni
        drawRoundRect(
            color = color,
            topLeft = Offset(0f, 0f),
            size = Size(cellSize * 3, cellSize * 3),
            cornerRadius = cornerRadius,
            style = Stroke(width = strokeWidth)
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(cellSize * 0.75f, cellSize * 0.75f),
            size = Size(cellSize * 1.5f, cellSize * 1.5f),
            cornerRadius = CornerRadius(cellSize / 6, cellSize / 6)
        )
        
        // Sağ üst köşe deseni
        drawRoundRect(
            color = color,
            topLeft = Offset(cellSize * 4, 0f),
            size = Size(cellSize * 3, cellSize * 3),
            cornerRadius = cornerRadius,
            style = Stroke(width = strokeWidth)
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(cellSize * 4.75f, cellSize * 0.75f),
            size = Size(cellSize * 1.5f, cellSize * 1.5f),
            cornerRadius = CornerRadius(cellSize / 6, cellSize / 6)
        )
        
        // Sol alt köşe deseni
        drawRoundRect(
            color = color,
            topLeft = Offset(0f, cellSize * 4),
            size = Size(cellSize * 3, cellSize * 3),
            cornerRadius = cornerRadius,
            style = Stroke(width = strokeWidth)
        )
        drawRoundRect(
            color = color,
            topLeft = Offset(cellSize * 0.75f, cellSize * 4.75f),
            size = Size(cellSize * 1.5f, cellSize * 1.5f),
            cornerRadius = CornerRadius(cellSize / 6, cellSize / 6)
        )
        
        // Orta kısım - QR data noktaları
        val dotSize = cellSize * 0.6f
        val dots = listOf(
            Offset(cellSize * 3.5f, cellSize * 0.5f),
            Offset(cellSize * 3.5f, cellSize * 1.5f),
            Offset(cellSize * 3.5f, cellSize * 2.5f),
            Offset(cellSize * 0.5f, cellSize * 3.5f),
            Offset(cellSize * 1.5f, cellSize * 3.5f),
            Offset(cellSize * 2.5f, cellSize * 3.5f),
            Offset(cellSize * 3.5f, cellSize * 3.5f),
            Offset(cellSize * 4.5f, cellSize * 3.5f),
            Offset(cellSize * 5.5f, cellSize * 3.5f),
            Offset(cellSize * 6.5f, cellSize * 3.5f),
            Offset(cellSize * 3.5f, cellSize * 4.5f),
            Offset(cellSize * 3.5f, cellSize * 5.5f),
            Offset(cellSize * 3.5f, cellSize * 6.5f),
            Offset(cellSize * 4.5f, cellSize * 4.5f),
            Offset(cellSize * 5.5f, cellSize * 5.5f),
            Offset(cellSize * 6.5f, cellSize * 4.5f),
            Offset(cellSize * 4.5f, cellSize * 6.5f),
            Offset(cellSize * 6.5f, cellSize * 6.5f),
        )
        
        dots.forEach { offset ->
            drawCircle(
                color = color,
                radius = dotSize / 2,
                center = offset
            )
        }
    }
}
