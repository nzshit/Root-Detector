package com.rootdetector.det.cheking

import android.content.Context
import com.rootdetector.det.base.Chker
import com.rootdetector.det.model.ChkRes
import com.rootdetector.det.model.Risk
import com.rootdetector.det.model.RootType
import com.rootdetector.det.util.FUtils
import com.rootdetector.det.util.Shell

class ChkFile : Chker {
// пока так
    private val paths = listOf(
        "/system/app/Superuser.apk" to RootType.SUPERSU,
        "/system/app/SuperSU/SuperSU.apk" to RootType.SUPERSU,
        "/data/data/com.topjohnwu.magisk" to RootType.MAGISK,
        "/data/adb/magisk" to RootType.MAGISK,
        "/data/adb/magisk.db" to RootType.MAGISK,
        "/data/adb/magisk.img" to RootType.MAGISK,
        "/data/adb/magiskboot" to RootType.MAGISK,
        "/data/adb/magisk/util_functions.sh" to RootType.MAGISK,
        "/data/adb/ksu" to RootType.KERNELSU,
        "/data/adb/ksu.db" to RootType.KERNELSU,
        "/data/adb/ksu/.allowlist" to RootType.KERNELSU,
        "/data/adb/ksu/allowlist" to RootType.KERNELSU,
        "/data/adb/ksu/modules" to RootType.KERNELSU,
        "/dev/ksu" to RootType.KERNELSU,
        "/data/adb/ap" to RootType.APATCH,
        "/data/adb/apatch" to RootType.APATCH,
        "/data/adb/ap/su_path" to RootType.APATCH,
        "/data/adb/ap/modules" to RootType.APATCH,
        "/data/adb/ap/allowlist" to RootType.APATCH,
        "/data/adb/ap/superkey" to RootType.APATCH,
        "/data/data/me.bmax.apatch" to RootType.APATCH,
        "/data/adb/modules" to RootType.UNKNOWN,
        "/data/adb/modules_update" to RootType.UNKNOWN,
        "/data/adb/service.d" to RootType.UNKNOWN,
        "/data/adb/post-fs-data.d" to RootType.UNKNOWN,
        "/data/local/tmp" to RootType.UNKNOWN,
        "/data/local/bin" to RootType.UNKNOWN,
        "/data/adb/riru" to RootType.UNKNOWN,
        "/data/adb/riru64" to RootType.UNKNOWN,
        "/system/lib/libxposed_art.so" to RootType.UNKNOWN,
        "/system/lib64/libxposed_art.so" to RootType.UNKNOWN,
        "/system/framework/XposedBridge.jar" to RootType.UNKNOWN,
        "/cache/recovery/command" to RootType.UNKNOWN
    )

    override suspend fun chk(ctx: Context): ChkRes {
        val found = mutableListOf<String>()
        val mods = mutableListOf<String>()

        for ((p, rt) in paths) {
            if (FUtils.exists(p)) found.add("${p} -> ${rt.name}")
        }

        if (FUtils.exists("/sdcard/TWRP")) found.add("/sdcard/TWRP/ -> TWRP recovery")
        if (FUtils.exists("/data/media/0/TWRP")) found.add("/data/media/0/TWRP/ -> TWRP recovery")
        if (FUtils.exists("/data/adb/modules/hosts/system/etc/hosts")) found.add("/data/adb/modules/hosts/ -> systemless hosts module")

        val bks = Shell.exec("ls -d /data/magisk_backup_* 2>/dev/null || true")
        if (bks.isNotEmpty() && !bks.contains("No such") && !bks.contains("/*")) {
            bks.split("\n").filter { it.isNotBlank() }.forEach { found.add("$it -> Magisk boot backup") }
        }

        val modList = Shell.exec("ls /data/adb/modules/ 2>/dev/null || true")
        if (modList.isNotEmpty() && !modList.contains("No such") && !modList.contains("Permission denied")) {
            val dirs = modList.split("\n").filter { it.isNotBlank() }.take(20)
            for (d in dirs) {
                val mpLine = Shell.exec("cat \"/data/adb/modules/$d/module.prop\" 2>/dev/null")
                if (mpLine.isNotEmpty()) {
                    val name = mpLine.split("\n").firstOrNull { it.startsWith("name=") }
                        ?.substringAfter("name=")?.trim() ?: d
                    val zygTag = if (FUtils.isDir("/data/adb/modules/$d/zygisk")) " [zygisk]" else ""
                    val snWords = listOf("safetynet", "playintegrity", "safetynet-fix", "playintegrityfix")
                    val snFlag = if (snWords.any { d.contains(it, true) || name.contains(it, true) }) " [SafetyNet]" else ""
                    mods.add("module: $name ($d)$zygTag$snFlag")
                } else {
                    mods.add("module dir: $d")
                }
            }
        }

        val all = found.toMutableList()
        if (mods.isNotEmpty()) { all.add(""); all.addAll(mods) }

        return ChkRes(
            name = "Files",
            found = found.isNotEmpty() || mods.isNotEmpty(),
            risk = if (found.isNotEmpty()) Risk.MID else if (mods.isNotEmpty()) Risk.LOW else Risk.NONE,
            detail = if (all.isNotEmpty()) all.joinToString(",\n") else "No root-related files found"
        )
    }
}
