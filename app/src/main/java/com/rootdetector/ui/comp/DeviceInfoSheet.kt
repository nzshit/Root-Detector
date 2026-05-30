package com.rootdetector.ui.comp

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.rootdetector.det.util.Shell

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceInfoSheet(
    rt: RootType,
    score: Int,
    onDismiss: () -> Unit
) {
    val kernel = remember { Shell.exec("uname -r").trim() }
    val bootState = remember { Shell.exec("getprop ro.boot.verifiedbootstate").trim().lowercase() }
    val flashLocked = remember { Shell.exec("getprop ro.boot.flash.locked").trim() }
    val vbmeta = remember { Shell.exec("getprop ro.boot.vbmeta.device_state").trim().lowercase() }

    val blStatus = when {
        bootState == "orange" || vbmeta == "unlocked" || flashLocked == "0" -> "Unlocked"
        bootState == "red" -> "Tampered"
        bootState == "green" || bootState.isNotEmpty() -> "Locked"
        else -> "Unknown"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BigPhoneWidget(rt, score)

            Spacer(Modifier.height(24.dp))

            InfoRow("Model", "${Build.MANUFACTURER} ${Build.MODEL}")
            InfoRow("Device", Build.DEVICE)
            InfoRow("Android", "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            InfoRow("Kernel", kernel.ifEmpty { "unknown" })
            InfoRow("Build", Build.ID)
            InfoRow("Bootloader", blStatus)
            if (Build.VERSION.SDK_INT >= 23) {
                InfoRow("Security patch", Build.VERSION.SECURITY_PATCH)
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))
            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onDismiss) {
                Text("Close", fontSize = 13.sp)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BigPhoneWidget(rt: RootType, score: Int) {
    val isClean = rt == RootType.NONE && score >= 0
    val noScan = score < 0

    val bodyColor by animateColorAsState(
        targetValue = when {
            noScan -> Color.Gray.copy(alpha = 0.3f)
            isClean -> Color(0xFF4CAF50)
            else -> Color(0xFFE94560)
        }, label = "phoneBig"
    )
    val bgColor = bodyColor.copy(alpha = 0.12f)
    val borderColor = bodyColor.copy(alpha = 0.5f)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.width(100.dp).height(160.dp).clip(RoundedCornerShape(10.dp, 10.dp, 6.dp, 6.dp))
        ) {
            Canvas(Modifier.size(100.dp, 160.dp)) {
                val w = size.width
                val h = size.height
                val cr = CornerRadius(12f, 12f)
                drawRoundRect(bgColor, topLeft = Offset.Zero, size = size, cornerRadius = cr)
                drawRoundRect(borderColor, topLeft = Offset.Zero, size = size, cornerRadius = cr, style = Stroke(2f))
                drawCircle(borderColor, radius = 6f, center = Offset(w / 2f, 18f))
                drawRoundRect(bodyColor, topLeft = Offset(w * 0.15f, h * 0.6f), size = Size(w * 0.7f, 4f), cornerRadius = CornerRadius(2f, 2f))
            }
            val status = when {
                rt != RootType.NONE -> "Rooted"
                score < 0 -> ""
                else -> "Clean"
            }
            if (status.isNotEmpty()) {
                Text(status, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = bodyColor,
                    modifier = Modifier.align(Alignment.Center).padding(top = 10.dp))
            }
        }
        Spacer(Modifier.height(4.dp))
        if (score >= 0) {
            Text("maybe", fontSize = 10.sp, color = bodyColor.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
}
