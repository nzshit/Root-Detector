package com.rootdetector.ui.comp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RiskGauge(
    sweep: Float,
    score: Int,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
    thickness: Dp = 14.dp
) {
    val color = when {
        score < 20 -> Color(0xFF4CAF50)
        score < 50 -> Color(0xFFFFC107)
        else -> Color(0xFFE94560)
    }

    val pct = (sweep * 100f / 360f).toInt().coerceIn(0, 100)

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val sw = thickness.toPx()
            val as_ = Size(size.toPx() - sw, size.toPx() - sw)
            val tl = Offset(sw / 2f, sw / 2f)
            drawArc(Color.Gray.copy(alpha = 0.2f), -90f, 360f, false, tl, as_, style = Stroke(sw, cap = StrokeCap.Round))
            if (sweep > 0) drawArc(color, -90f, sweep.coerceAtMost(360f), false, tl, as_, style = Stroke(sw, cap = StrokeCap.Round))
        }
        Text("$pct%", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
    }
}
