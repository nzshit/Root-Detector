package com.rootdetector.det.cheking

import android.content.Context
import com.rootdetector.det.base.Chker
import com.rootdetector.det.model.ChkRes
import com.rootdetector.det.model.Risk
import com.rootdetector.det.model.RootType
import com.rootdetector.det.util.FUtils
import com.rootdetector.det.util.Shell

class ChkBin : Chker {

    private val suPaths = listOf(
        "/system/bin/su", "/system/xbin/su", "/sbin/su", "/su/bin/su",
        "/data/local/su", "/data/local/xbin/su", "/data/local/bin/su",
        "/system/sd/xbin/su", "/system/bin/failsafe/su"
    )
    private val daemonPaths = mapOf(
        "/data/adb/ksu/bin/ksud" to RootType.KERNELSU,
        "/data/adb/apd" to RootType.APATCH,
        "/data/adb/ap/bin/apd" to RootType.APATCH,
    )
    private val bins = mapOf(
        "magiskinit" to RootType.MAGISK, "magiskpolicy" to RootType.MAGISK,
        "magiskboot" to RootType.MAGISK, "magisk" to RootType.MAGISK,
        "ksud" to RootType.KERNELSU, "apd" to RootType.APATCH,
        "busybox" to RootType.UNKNOWN
    )

    override suspend fun chk(ctx: Context): ChkRes {
        val found = mutableListOf<String>()
        val pathDirs = Shell.exec("echo \$PATH 2>/dev/null").split(":").filter { it.isNotBlank() }

        for (p in suPaths) {
            if (FUtils.exists(p)) { found.add("su($p)"); break }
        }
        for (d in pathDirs) {
            val sp = "$d/su"
            if (FUtils.exists(sp) && sp !in suPaths) { found.add("su($sp)"); break }
        }

        for ((dp, _) in daemonPaths) {
            if (FUtils.exists(dp)) found.add("${dp.substringAfterLast("/")}($dp)")
        }

        for ((bin, _) in bins) {
            for (d in pathDirs) {
                val bp = "$d/$bin"
                if (FUtils.exists(bp)) { found.add("$bin($bp)"); break }
            }
        }

        var sh = Shell.exec("which su")
        if (sh.isNotEmpty() && !sh.contains("not found")) found.add("which su -> $sh")
        sh = Shell.exec("su --version")
        if (sh.isNotEmpty() && !sh.contains("not found")) found.add("su --version -> $sh")
        sh = Shell.exec("magisk -v")
        if (sh.isNotEmpty() && !sh.contains("not found")) found.add("magisk -v -> $sh -> Magisk")
        sh = Shell.exec("magisk -c")
        if (sh.isNotEmpty() && !sh.contains("not found")) found.add("magisk -c -> $sh -> Magisk")
        sh = Shell.exec("magisk --path")
        if (sh.isNotEmpty() && !sh.contains("not found")) found.add("magisk --path -> $sh -> Magisk")
        sh = Shell.exec("magisk --denylist status")
        if (sh.isNotEmpty() && !sh.contains("not found") && !sh.contains("not implemented")) found.add("magisk --denylist -> $sh -> Magisk")
        sh = Shell.exec("magisk -s")
        if (sh.isNotEmpty() && !sh.contains("not found")) found.add("magisk -s -> $sh -> Magisk")
        sh = Shell.exec("ksud --version")
        if (sh.isNotEmpty() && !sh.contains("not found")) found.add("ksud --version -> $sh -> KernelSU")
        sh = Shell.exec("ksud module list 2>/dev/null")
        if (sh.isNotEmpty() && !sh.contains("not found") && sh != "[]") found.add("ksud module list -> modules installed -> KernelSU")
        sh = Shell.exec("apd --version")
        if (sh.isNotEmpty() && !sh.contains("not found")) found.add("apd --version -> $sh -> APatch")
        sh = Shell.exec("apd --help 2>/dev/null | head -3")
        if (sh.isNotEmpty() && !sh.contains("not found") && sh != "Usage:" && !sh.contains("command not found")) found.add("apd -> $sh -> APatch")

        sh = Shell.exec("busybox 2>&1 | head -1")
        if (sh.isNotEmpty() && !sh.contains("not found")) found.add("busybox -> $sh")

        return ChkRes(
            name = "Binaries",
            found = found.isNotEmpty(),
            risk = if (found.isNotEmpty()) Risk.HIGH else Risk.NONE,
            detail = if (found.isNotEmpty()) found.joinToString(",\n") else "No root binaries found"
        )
    }
}
