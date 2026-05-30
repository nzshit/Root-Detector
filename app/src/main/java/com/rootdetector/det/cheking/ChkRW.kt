package com.rootdetector.det.cheking

import android.content.Context
import com.rootdetector.det.base.Chker
import com.rootdetector.det.model.ChkRes
import com.rootdetector.det.model.Risk
import com.rootdetector.det.util.Shell

class  ChkRW : Chker {

    private val mounts = listOf("/system", "/vendor", "/product", "/system_ext")

    override suspend fun chk(ctx: Context): ChkRes {
        val found = mutableListOf<String>()

        val mountOut = Shell.exec("mount 2>/dev/null").lowercase()
        for (m in mounts) {
            if (mountOut.contains(" $m ") && mountOut.contains("rw,")) {
                found.add("$m mounted RW (mount)")
            }
        }

        val procMounts = Shell.exec("cat /proc/mounts 2>/dev/null").lowercase()
        for (m in mounts) {
            if (procMounts.contains(" $m ") && procMounts.contains(" rw,")) {
                found.add("$m RW in /proc/mounts")
            }
        }

        for (m in mounts) {
            val test = Shell.exec("touch \"$m/.ro_test\" 2>/dev/null && echo ok || echo fail").trim()
            if (test == "ok") {
                found.add("$m is writable (can create file)")
                Shell.exec("rm -f \"$m/.ro_test\" 2>/dev/null")
            }
        }

        return ChkRes(
            name = "RW",
            found = found.isNotEmpty(),
            risk = if (found.isNotEmpty()) Risk.HIGH else Risk.NONE,
            detail = if (found.isNotEmpty()) found.joinToString(",\n")
            else "All partitions mounted RO (normal)"
        )
    }
}
