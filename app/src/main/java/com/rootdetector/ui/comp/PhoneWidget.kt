package com.rootdetector.ui.comp

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rootdetector.det.model.RootType

@Composable
fun PhoneWidget(rt: RootType, score: Int, modifier: Modifier = Modifier) {
    val isClean = rt == RootType.NONE && score >= 0
    val noScan = score < 0

    val bodyColor by animateColorAsState(
        targetValue = when {
            noScan -> Color.Gray.copy(alpha = 0.3f)
            isClean -> Color(0xFF4CAF50)
            else -> Color(0xFFE94560)
        }, label = "phoneColor"
    )
    val bgColor = bodyColor.copy(alpha = 0.12f)
    val borderColor = bodyColor.copy(alpha = 0.5f)

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.width(54.dp).height(88.dp).clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
        ) {
            Canvas(Modifier.size(54.dp, 88.dp)) {
                val w = size.width
                val h = size.height
                val cr = CornerRadius(8f, 8f)
                drawRoundRect(bgColor, topLeft = Offset.Zero, size = size, cornerRadius = cr)
                drawRoundRect(borderColor, topLeft = Offset.Zero, size = size, cornerRadius = cr, style = Stroke(1.5f))
                drawCircle(borderColor, radius = 4f, center = Offset(w / 2f, 12f))
                drawRoundRect(bodyColor, topLeft = Offset(w * 0.15f, h * 0.58f), size = Size(w * 0.7f, 3f), cornerRadius = CornerRadius(1.5f, 1.5f))
            }
            val txt = when {
                rt != RootType.NONE -> "Rooted"
                score < 0 -> ""
                else -> "Clear"
            }
            if (txt.isNotEmpty()) {
                Text(txt, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = bodyColor,
                    modifier = Modifier.align(Alignment.Center).padding(top = 6.dp))
            }
        }
        Spacer(Modifier.height(2.dp))
        if (score >= 0) {
            Text("maybe", fontSize = 7.sp, color = bodyColor.copy(alpha = 0.5f))
        }
    }
}
