package com.rootdetector.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreditPage() {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Code, contentDescription = null,
            modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))

        Text("RootDetector", fontSize = 22.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground)
        Text("v1.0", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))

        Spacer(Modifier.height(32.dp))

        CreditItem(Icons.Default.Code, "GitHub")
        Text("github.com/nzshit", fontSize = 13.sp, textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary, lineHeight = 18.sp,
            modifier = Modifier.clickable { uriHandler.openUri("https://github.com/nzshit") })

        Spacer(Modifier.height(16.dp))

        CreditItem(Icons.AutoMirrored.Filled.Send, "Telegram")
        Text("t.me/+O5Zbw_y3tFE2YmYy", fontSize = 13.sp, textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary, lineHeight = 18.sp,
            modifier = Modifier.clickable { uriHandler.openUri("https://t.me/+O5Zbw_y3tFE2YmYy") })

        Spacer(Modifier.height(16.dp))

        CreditItem(Icons.Default.Person, "Author")
        Text("nzshit", fontSize = 13.sp, textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))

        Spacer(Modifier.height(32.dp))
        Text("Open source root detection tool",
            fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            textAlign = TextAlign.Center)
    }
}

@Composable
private fun CreditItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        Spacer(Modifier.padding(4.dp))
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
    }
}
