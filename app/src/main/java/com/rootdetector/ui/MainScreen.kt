package com.rootdetector.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.rootdetector.det.model.RootType
import com.rootdetector.ui.comp.DeviceInfoSheet
import com.rootdetector.ui.page.CreditPage
import com.rootdetector.ui.page.CustomPage
import com.rootdetector.ui.page.ScanPage
import com.rootdetector.ui.theme.Accents
import com.rootdetector.ui.theme.RootDetTheme

@Composable
fun MainScreen() {
    var isDark by remember { mutableStateOf(false) }
    var accentIdx by remember { mutableStateOf(0) }
    var gradStartIdx by remember { mutableStateOf(0) }
    var autoScan by remember { mutableStateOf(false) }
    var vibrate by remember { mutableStateOf(true) }
    var lastRt by remember { mutableStateOf(RootType.NONE) }
    var lastScore by remember { mutableIntStateOf(-1) }
    var showDeviceSheet by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { 3 }, initialPage = 1)

    RootDetTheme(darkTheme = isDark, accent = Accents[accentIdx]) {
        Column(
            modifier = Modifier
                .fillMaxSize().padding(top = 32.dp)
                .background(MaterialTheme.colorScheme.background)
                .background(
                    Brush.verticalGradient(
                        0.0f to Accents[gradStartIdx].copy(alpha = 0.25f),
                        1.0f to Accents[gradStartIdx].copy(alpha = 0f)
                    )
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                ) {
                    IconButton(onClick = { showDeviceSheet = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp))
                    }
                }
            }

            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                when (page) {
                    0 -> CustomPage(
                        isDark = isDark, accentIdx = accentIdx,
                        gradStartIdx = gradStartIdx,
                        autoScan = autoScan, vibrate = vibrate,
                        onDarkChange = { isDark = it },
                        onAccentChange = { accentIdx = it },
                        onGradStartChange = { gradStartIdx = it },
                        onAutoScanChange = { autoScan = it },
                        onVibrateChange = { vibrate = it }
                    )
                    1 -> ScanPage(
                        autoScan = autoScan, vibrate = vibrate,
                        onResult = { rt, sc -> lastRt = rt; lastScore = sc }
                    )
                    2 -> CreditPage()
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (pagerState.currentPage == i) 10.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == i) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                            )
                    )
                }
            }
        }

        if (showDeviceSheet) {
            DeviceInfoSheet(
                rt = lastRt,
                score = lastScore,
                onDismiss = { showDeviceSheet = false }
            )
        }
    }
}
