package com.rootdetector.ui.page

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rootdetector.det.RootDet
import com.rootdetector.det.model.RootType
import com.rootdetector.ui.comp.RiskGauge
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class St { IDLE, SCANNING, DONE }

@Composable
fun ScanPage(autoScan: Boolean = false, vibrate: Boolean = true, onResult: (RootType, Int) -> Unit = { _, _ -> }) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var st by remember { mutableStateOf(St.IDLE) }
    var log by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }
    var rt by remember { mutableStateOf(RootType.NONE) }
    var gs by remember { mutableStateOf(0) }
    var sw by remember { mutableStateOf(0f) }
    var scanJob by remember { mutableStateOf<Job?>(null) }
    var autoScanned by remember { mutableStateOf(false) }

    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= 31) {
            val vm = ctx.getSystemService(VibratorManager::class.java)
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    val saveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let {
            ctx.contentResolver.openOutputStream(it)?.use { os ->
                val hdr = "RootDetector Report\n${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}\n${"=".repeat(30)}\n\n"
                os.write((hdr + log).toByteArray())
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { scanJob?.cancel() }
    }

    LaunchedEffect(Unit) {
        if (autoScan && !autoScanned && st == St.IDLE) {
            autoScanned = true
            st = St.SCANNING; gs = 0; sw = 0f; log = ""
            val r = RootDet().scan(ctx)
            score = r.score; rt = r.rootType
            val lns = buildLog(r)
            gs = score
            lns.add("")
            lns.add(if (rt != RootType.NONE) "→ Root: ${rt.name} (${score}%)" else "→ Clean (${score}%)")
            val steps = 40
            for (i in 1..steps) {
                sw = 360f * i / steps
                delay(37)
            }
            sw = 360f
            val total = lns.size
            for (i in 1..total) {
                log = lns.take(i).joinToString("\n")
                delay((1500 / total).toLong().coerceAtLeast(50))
            }
            log = lns.joinToString("\n")
            delay(600)
            onResult(rt, score)
            st = St.DONE
            if (vibrate && vibrator != null) {
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(200)
                }
            }
        }
    }

    val snake = rememberInfiniteTransition(label = "snk")
    val sPhase by snake.animateFloat(0f, 1f, infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart), label = "sp")

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RiskGauge(sweep = sw, score = gs)

        Spacer(Modifier.height(4.dp))

        Text(
            when { st == St.IDLE -> "RootDetector"; st == St.DONE && rt != RootType.NONE -> "Root: ${rt.name}"; st == St.DONE -> "Clean"; else -> "" },
            fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        Spacer(Modifier.height(20.dp))

        val btnCol = if (st == St.DONE) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
        val r = 16.dp

        Box(
            modifier = Modifier
                .width(200.dp).height(52.dp)
                .clip(RoundedCornerShape(r))
                .background(btnCol, RoundedCornerShape(r))
                .drawWithContent {
                    drawContent()
                    if (st == St.SCANNING) {
                        val sw2 = 2.dp.toPx()
                        val cr = r.toPx()
                        val p = Path().apply {
                            addRoundRect(RoundRect(Rect(Offset(sw2 / 2, sw2 / 2), Size(size.width - sw2, size.height - sw2)), CornerRadius(cr)))
                        }
                        drawPath(p, Color.White.copy(0.8f), style = Stroke(sw2, cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 500.dp.toPx()), sPhase * 500.dp.toPx())))
                    }
                }
                .clickable(enabled = st != St.SCANNING) {
                    when (st) {
                        St.IDLE -> {
                            autoScanned = true
                            st = St.SCANNING; gs = 0; sw = 0f; log = ""
                            scanJob = scope.launch {
                                val r = RootDet().scan(ctx)
                                score = r.score; rt = r.rootType
                                val lns = buildLog(r)
                                gs = score
                                lns.add("")
                                lns.add(if (rt != RootType.NONE) "→ Root: ${rt.name} (${score}%)" else "→ Clean (${score}%)")
                                val steps = 40
                                for (i in 1..steps) {
                                    sw = 360f * i / steps
                                    delay(37)
                                }
                                sw = 360f
                                val total = lns.size
                                for (i in 1..total) {
                                    log = lns.take(i).joinToString("\n")
                                    delay((1500 / total).toLong().coerceAtLeast(50))
                                }
                                 log = lns.joinToString("\n")
                                 delay(600)
                                 onResult(rt, score)
                                 st = St.DONE
                                 if (vibrate && vibrator != null) {
                                    if (Build.VERSION.SDK_INT >= 26) {
                                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                                    } else {
                                        vibrator.vibrate(200)
                                    }
                                }
                            }
                        }
                        St.DONE -> { st = St.IDLE; log = ""; gs = 0; sw = 0f; score = 0; rt = RootType.NONE; autoScanned = false }
                        else -> {}
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                when (st) { St.IDLE -> "SCAN"; St.SCANNING -> "▸ SCANNING"; St.DONE -> "AGAIN"; else -> "" },
                fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary
            )
        }

        if (st == St.DONE) {
            Spacer(Modifier.height(6.dp))
            TextButton(onClick = {
                val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                saveLauncher.launch("RootDetector_$ts.txt")
            }) {
                Text("Save Log", fontSize = 12.sp)
            }
        }

        if (log.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            val sc = rememberScrollState()
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)).padding(12.dp).verticalScroll(sc)
            ) {
                Text(log, fontSize = 12.sp, fontFamily = FontFamily.Monospace,
                    color = if (rt != RootType.NONE) Color(0xFFE94560) else Color(0xFF4CAF50), lineHeight = 18.sp)
            }
        }
    }
}

private fun buildLog(r: com.rootdetector.det.RootDet.DetRes): MutableList<String> {
    val lns = mutableListOf<String>()
    r.results.forEach { c ->
        val il = StringBuilder()
        var first = true
        c.detail.split(",").filter { it.isNotBlank() }.forEach { l ->
            if (first) { il.append("  ${l.trim()}"); first = false }
            else { il.append("\n  ${l.trim()}") }
        }
        lns.add("$ ${c.name}")
        if (c.found) lns.add(il.toString())
        else lns.add("  clean")
    }
    return lns
}
