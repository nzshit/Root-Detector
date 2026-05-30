package com.rootdetector.det.cheking

import android.content.Context
import com.rootdetector.det.base.Chker
import com.rootdetector.det.model.ChkRes
import com.rootdetector.det.model.Risk
import com.rootdetector.det.util.FUtils
import com.rootdetector.det.util.Shell

class ChkProc : Chker {

    override suspend fun chk(ctx: Context): ChkRes {
        val found = mutableListOf<String>()

        val ps = Shell.exec("ps -A 2>/dev/null").lowercase()
        for (d in listOf("magiskd", "ksud", "apd")) {
            if (ps.contains(d)) found.add("process: $d running")
        }

        val mountOut = Shell.exec("cat /proc/mounts 2>/dev/null").lowercase()
        if (mountOut.contains("tmpfs") && (mountOut.contains(" /system ") || mountOut.contains(" /system_root "))) {
            found.add("mount: tmpfs on /system (suspicious)")
        }
        if (mountOut.contains("overlay") && mountOut.contains(" /system ")) {
            found.add("mount: overlay on /system (magisk mount)")
        }
        if (mountOut.contains("magisk")) found.add("mount: magisk tmpfs detected")

        val comm = Shell.exec("cat /proc/1/comm 2>/dev/null").trim().lowercase()
        if (comm.isNotEmpty() && comm != "init") {
            found.add("proc/1/comm=$comm (should be init)")
        }

        val exe = Shell.exec("ls -la /proc/1/exe 2>/dev/null").lowercase()
        if (exe.isNotEmpty() && !exe.contains("/system/bin/init") && !exe.contains("/init")) {
            found.add("proc/1/exe -> $exe (unusual)")
        }

        val mountInfo = Shell.exec("cat /proc/self/mountinfo 2>/dev/null").lowercase()
        for (s in listOf("magisk", "ksu", "apatch", "su", "supersu")) {
            if (mountInfo.contains(s)) found.add("mountinfo: contains '$s'")
        }

        val maps = Shell.exec("cat /proc/self/maps 2>/dev/null").lowercase()
        for (sig in listOf("libzygisk", "libriru", "libxposed_art", "/magisk/", "/ksu/", "/apatch/")) {
            if (maps.contains(sig)) found.add("maps: $sig loaded in process")
        }

        if (FUtils.exists("/sys/kernel/ksu")) found.add("/sys/kernel/ksu/ -> KernelSU sysfs")
        val cmdline = Shell.exec("cat /proc/cmdline 2>/dev/null").lowercase()
        if (cmdline.contains("androidboot.selinux=permissive")) found.add("cmdline: selinux=permissive")
        if (cmdline.contains("kernelsu") || cmdline.contains("ksu ")) found.add("cmdline: KernelSU detected")
        if (cmdline.contains("apatch")) found.add("cmdline: APatch detected")

        val uid0 = Shell.exec("ps -A 2>/dev/null | awk '{print \$1}' | sort -u").trim()
        if (uid0.split("\n").any { it.trim() == "0" }) {
            found.add("processes running as root (uid 0)")
        }

        val ver = Shell.exec("cat /proc/version 2>/dev/null").lowercase()
        if (ver.contains("kernelsu")) found.add("/proc/version: KernelSU")
        if (ver.contains("apatch")) found.add("/proc/version: APatch")
        if (ver.contains("magisk")) found.add("/proc/version: Magisk")

        val environ = Shell.exec("cat /proc/1/environ 2>/dev/null")
        if (environ.isNotEmpty()) found.add("/proc/1/environ readable (security relaxed)")

        val selCtx = Shell.exec("cat /proc/1/attr/current 2>/dev/null").lowercase()
        if (selCtx.isNotEmpty() && (selCtx.contains("magisk") || selCtx.contains("su") || selCtx.contains("unconfined"))) {
            found.add("proc/1/attr/current=$selCtx")
        }

        return ChkRes(
            name = "Runtime",
            found = found.isNotEmpty(),
            risk = if (found.isNotEmpty()) Risk.MID else Risk.NONE,
            detail = if (found.isNotEmpty()) found.joinToString(",\n") else "Runtime environment normal"
        )
    }
}
