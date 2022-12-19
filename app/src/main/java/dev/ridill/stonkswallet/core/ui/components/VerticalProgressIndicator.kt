package dev.ridill.stonkswallet.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ridill.stonkswallet.core.ui.theme.StonksWalletTheme

@Composable
fun VerticalProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    width: Dp = BarWidth,
    isTtb: Boolean = false,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
    strokeCap: StrokeCap = Stroke.DefaultCap
) {
    Canvas(
        modifier
            .progressSemantics()
            .width(width)
    ) {
        val strokeWidth = size.width
        drawVerticalTrack(trackColor, strokeWidth, strokeCap)
        val startFraction = if (isTtb) 0f else 1f
        val endFraction = if (isTtb) progress else 1 - progress
        drawVerticalIndicator(startFraction, endFraction, indicatorColor, strokeWidth, strokeCap)
    }
}

private fun DrawScope.drawVerticalIndicator(
    startFraction: Float,
    endFraction: Float,
    color: Color,
    strokeWidth: Float,
    strokeCap: StrokeCap
) {
    val width = size.width
    val height = size.height
    val offsetX = width / 2
    val barStart = startFraction * height
    val barEnd = endFraction * height

    drawLine(
        color,
        Offset(offsetX, barStart),
        Offset(offsetX, barEnd),
        strokeWidth,
        strokeCap
    )
}

private fun DrawScope.drawVerticalTrack(
    color: Color,
    strokeWidth: Float,
    strokeCap: StrokeCap
) = drawVerticalIndicator(1f, 0f, color, strokeWidth, strokeCap)

private val BarWidth = 4.dp

@Preview(showBackground = true)
@Composable
private fun PreviewVerticalProgressIndicator() {
    StonksWalletTheme {
        Box(
            Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            VerticalProgressIndicator(
                progress = 0.2f,
                modifier = Modifier
                    .height(400.dp)
            )
        }
    }
}