package com.rootdetector.det.cheking

import android.content.Context
import com.rootdetector.det.base.Chker
import com.rootdetector.det.model.ChkRes
import com.rootdetector.det.model.Risk
import com.rootdetector.det.util.Shell

class ChkBL : Chker {

    override suspend fun chk(ctx: Context): ChkRes {
        val found = mutableListOf<String>()

        val bst = Shell.exec("getprop ro.boot.verifiedbootstate").trim().lowercase()
        if (bst == "orange") found.add("ro.boot.verifiedbootstate=orange -> BL unlocked")
        if (bst == "red") found.add("ro.boot.verifiedbootstate=red -> tampered")

        val fl = Shell.exec("getprop ro.boot.flash.locked").trim()
        if (fl == "0") found.add("ro.boot.flash.locked=0 -> BL unlocked")

        val vm = Shell.exec("getprop ro.boot.vbmeta.device_state").trim().lowercase()
        if (vm == "unlocked") found.add("ro.boot.vbmeta.device_state=unlocked -> BL unlocked")

        val vm2 = Shell.exec("getprop ro.boot.veritymode").trim().lowercase()
        if (vm2 == "logging" || vm2 == "disabled") found.add("ro.boot.veritymode=$vm2 -> dm-verity weakened")

        val sb = Shell.exec("getprop ro.boot.secureboot").trim()
        if (sb.isNotEmpty()) found.add("ro.boot.secureboot=$sb")

        val wb = Shell.exec("getprop ro.boot.warranty_bit").trim()
        if (wb.isNotEmpty()) found.add("ro.boot.warranty_bit=$wb -> warranty status")

        val knox = Shell.exec("getprop ro.config.knox").trim()
        if (knox == "0") found.add("ro.config.knox=$knox -> Knox tripped (Samsung)")

        // Bootloader spoofing detection: BL claims locked but system acts rooted
        if (bst == "green" || bst == "") {
            val debug = Shell.exec("getprop ro.debuggable").trim()
            val buildType = Shell.exec("getprop ro.build.type").trim()
            if (debug == "1" || buildType == "userdebug" || buildType == "eng") {
                found.add("BL=${bst.ifEmpty { "?" }} but debug=$debug build=$buildType -> possible bootloader spoof")
            }
            val enforce = Shell.exec("getenforce").trim().lowercase()
            if (enforce == "permissive") {
                found.add("BL=${bst.ifEmpty { "?" }} but SELinux=$enforce -> possible bootloader spoof")
            }
        }

        return ChkRes(
            name = "Bootloader",
            found = found.isNotEmpty(),
            risk = if (found.isNotEmpty()) Risk.MID else Risk.NONE,
            detail = if (found.isNotEmpty()) found.joinToString(",\n") else "Bootloader locked (normal)"
        )
    }
}
