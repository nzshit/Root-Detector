package com.rootdetector.det.cheking

import android.content.Context
import android.os.Build
import com.rootdetector.det.base.Chker
import com.rootdetector.det.model.ChkRes
import com.rootdetector.det.model.Risk
import com.rootdetector.det.util.Shell

class ChkProp : Chker {

    private fun getProp(n: String): String = Shell.exec("getprop $n").trim()

    override suspend fun chk(ctx: Context): ChkRes {
        val found = mutableListOf<String>()

        if (Build.TAGS?.contains("test-keys") == true) found.add("build.tags=test-keys")
        if (getProp("ro.debuggable") == "1") found.add("ro.debuggable=1")
        if (getProp("ro.secure") == "0") found.add("ro.secure=0")
        val bt = getProp("ro.build.type")
        if (bt == "eng" || bt == "userdebug") found.add("ro.build.type=$bt")

        val fp = getProp("ro.build.fingerprint").lowercase()
        if (fp.contains("userdebug") || fp.contains("/eng")) found.add("ro.build.fingerprint -> $bt build")
        val desc = getProp("ro.build.description").lowercase()
        if (desc.contains("test-keys")) found.add("ro.build.description contains test-keys")

        val zy = getProp("ro.zygote").lowercase()
        if (zy.contains("zygisk")) found.add("ro.zygote=$zy -> Zygisk enabled")

        val magSdk = getProp("ro.magisk.sdk")
        if (magSdk.isNotEmpty()) found.add("ro.magisk.sdk=$magSdk -> Magisk")
        val magVer = getProp("ro.magisk.version")
        if (magVer.isNotEmpty()) found.add("ro.magisk.version=$magVer -> Magisk")
        val initMagisk = getProp("init.svc.magisk")
        if (initMagisk.isNotEmpty()) found.add("init.svc.magisk=$initMagisk -> Magisk daemon")
        val magHide = getProp("persist.magisk.hide")
        if (magHide.isNotEmpty()) found.add("persist.magisk.hide=$magHide -> Magisk Hide")

        val ksuVer = getProp("ro.kernelsu.version")
        if (ksuVer.isNotEmpty()) found.add("ro.kernelsu.version=$ksuVer -> KernelSU")
        val ksuKlog = getProp("ro.kernelsu.klog")
        if (ksuKlog.isNotEmpty()) found.add("ro.kernelsu.klog=$ksuKlog -> KernelSU")
        val initKsu = getProp("init.svc.ksud")
        if (initKsu.isNotEmpty()) found.add("init.svc.ksud=$initKsu -> KernelSU daemon")
        val ksuEn = getProp("ro.kernelsu.enabled")
        if (ksuEn.isNotEmpty()) found.add("ro.kernelsu.enabled=$ksuEn -> KernelSU")

        val apVer = getProp("ro.apatch.version")
        if (apVer.isNotEmpty()) found.add("ro.apatch.version=$apVer -> APatch")
        val apStat = getProp("ro.apatch.status")
        if (apStat.isNotEmpty()) found.add("ro.apatch.status=$apStat -> APatch")
        val initApd = getProp("init.svc.apd")
        if (initApd.isNotEmpty()) found.add("init.svc.apd=$initApd -> APatch daemon")
        val apAl = getProp("ro.apatch.allowlist")
        if (apAl.isNotEmpty()) found.add("ro.apatch.allowlist=$apAl -> APatch")
        val apSk = getProp("ro.apatch.superkey")
        if (apSk.isNotEmpty()) found.add("ro.apatch.superkey=$apSk -> APatch")
        val apAlPersist = getProp("persist.apatch.allowlist")
        if (apAlPersist.isNotEmpty()) found.add("persist.apatch.allowlist=$apAlPersist -> APatch")

        val rootAcc = getProp("persist.sys.root_access")
        if (rootAcc.isNotEmpty()) found.add("persist.sys.root_access=$rootAcc")
        val safeMode = getProp("persist.sys.safemode")
        if (safeMode.isNotEmpty()) found.add("persist.sys.safemode=$safeMode")

        val adbd = getProp("init.svc.adbd")
        if (adbd.isNotEmpty()) found.add("init.svc.adbd=$adbd")
        val selinuxProp = getProp("ro.build.selinux")
        if (selinuxProp.isNotEmpty()) found.add("ro.build.selinux=$selinuxProp")

        val envMagisk = Shell.exec("echo \$MAGISK_VER")
        if (envMagisk.isNotEmpty() && envMagisk != "\$MAGISK_VER") found.add("\$MAGISK_VER=$envMagisk -> Magisk")
        val envMagiskTmp = Shell.exec("echo \$MAGISKTMP")
        if (envMagiskTmp.isNotEmpty() && envMagiskTmp != "\$MAGISKTMP") found.add("\$MAGISKTMP=$envMagiskTmp -> Magisk tmpfs")
        val envKsu = Shell.exec("echo \$KSU")
        if (envKsu.isNotEmpty() && envKsu != "\$KSU") found.add("\$KSU=$envKsu -> KernelSU")
        val envAp = Shell.exec("echo \$APATCH")
        if (envAp.isNotEmpty() && envAp != "\$APATCH") found.add("\$APATCH=$envAp -> APatch")
        val envApVer = Shell.exec("echo \$APATCH_VER")
        if (envApVer.isNotEmpty() && envApVer != "\$APATCH_VER") found.add("\$APATCH_VER=$envApVer -> APatch")

        return ChkRes(
            name = "Properties",
            found = found.isNotEmpty(),
            risk = if (found.isNotEmpty()) Risk.MID else Risk.NONE,
            detail = if (found.isNotEmpty()) found.joinToString(",\n") else "All system properties normal"
        )
    }
}
