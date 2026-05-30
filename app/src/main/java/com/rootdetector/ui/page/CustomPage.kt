package com.rootdetector.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rootdetector.ui.theme.Accents

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomPage(
    isDark: Boolean,
    accentIdx: Int,
    gradStartIdx: Int,
    autoScan: Boolean,
    vibrate: Boolean,
    onDarkChange: (Boolean) -> Unit,
    onAccentChange: (Int) -> Unit,
    onGradStartChange: (Int) -> Unit,
    onAutoScanChange: (Boolean) -> Unit,
    onVibrateChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Palette, contentDescription = null,
            modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Text("Customization", fontSize = 18.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LightMode, contentDescription = null,
                tint = if (!isDark) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(20.dp))
            IconButton(onClick = { onDarkChange(!isDark) }) {
                Icon(
                    if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = "Toggle theme",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Icon(Icons.Default.DarkMode, contentDescription = null,
                tint = if (isDark) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(20.dp))
        }

        Spacer(Modifier.height(16.dp))

        val accExpanded = remember { mutableStateOf(false) }

        Row(
            Modifier.clickable { accExpanded.value = !accExpanded.value },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Accent", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            Spacer(Modifier.width(4.dp))
            Icon(
                if (accExpanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (accExpanded.value) "Collapse" else "Expand",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }

        AnimatedVisibility(
            visible = accExpanded.value,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Accents.forEachIndexed { i, c ->
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(c)
                                .clickable { onAccentChange(i) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (i == accentIdx) Box(Modifier.size(12.dp).clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f)))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        val gradExpanded = remember { mutableStateOf(false) }

        Row(
            Modifier.clickable { gradExpanded.value = !gradExpanded.value },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Background Gradient", fontSize = 13.sp, fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            Spacer(Modifier.width(4.dp))
            Icon(
                if (gradExpanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (gradExpanded.value) "Collapse" else "Expand",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }

        AnimatedVisibility(
            visible = gradExpanded.value,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(8.dp))

                Box(
                    Modifier.fillMaxWidth(0.7f).height(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .background(Brush.verticalGradient(listOf(Accents[gradStartIdx].copy(alpha = 0.4f), Accents[gradStartIdx].copy(alpha = 0f))))
                )

                Spacer(Modifier.height(10.dp))

                Text("From", fontSize = 11.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
                Spacer(Modifier.height(4.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Accents.forEachIndexed { i, c ->
                        Box(
                            modifier = Modifier.size(28.dp).clip(CircleShape).background(c)
                                .clickable { onGradStartChange(i) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (i == gradStartIdx) Box(Modifier.size(10.dp).clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f)))
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(0.6f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                Spacer(Modifier.width(4.dp))
                Text("Auto-scan", fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
            Switch(checked = autoScan, onCheckedChange = onAutoScanChange)
        }

        Spacer(Modifier.height(8.dp))

        Row(
            Modifier.fillMaxWidth(0.6f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                Spacer(Modifier.width(4.dp))
                Text("Vibrate", fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
            Switch(checked = vibrate, onCheckedChange = onVibrateChange)
        }
    }
}
