package com.yunusek.qrguard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.yunusek.qrguard.ui.theme.GradientEnd
import com.yunusek.qrguard.ui.theme.GradientMid
import com.yunusek.qrguard.ui.theme.GradientStart

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GradientStart,
                        GradientMid,
                        GradientEnd
                    )
                )
            ),
        content = content
    )
}
