package com.rootdetector.det.cheking

import android.content.Context
import com.rootdetector.det.base.Chker
import com.rootdetector.det.model.ChkRes
import com.rootdetector.det.model.Risk
import com.rootdetector.det.util.FUtils
import com.rootdetector.det.util.Shell

class ChkSel : Chker {

    override suspend fun chk(ctx: Context): ChkRes {
        val found = mutableListOf<String>()

        val enforce = Shell.exec("getenforce").lowercase()
        if (enforce == "permissive") {
            found.add("SELinux: Permissive")
        }

        if (enforce.isEmpty()) {
            found.add("SELinux: unknown (no getenforce)")
        }

        val enforceFile = Shell.exec("cat /sys/fs/selinux/enforce 2>/dev/null").trim()
        if (enforceFile == "0") {
            found.add("SELinux enforce=0 (permissive via sysfs)")
        }

        return ChkRes(
            name = "SELinux",
            found = found.isNotEmpty(),
            risk = if (found.any { it.contains("Permissive") || it.contains("permissive") })
                Risk.HIGH else Risk.LOW,
            detail = if (found.isNotEmpty()) found.joinToString(",\n")
            else "SELinux: Enforcing (normal)"
        )
    }
}
