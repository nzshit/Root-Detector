package com.rootdetector.det.cheking

import android.content.Context
import android.content.pm.PackageManager
import com.rootdetector.det.base.Chker
import com.rootdetector.det.model.ChkRes
import com.rootdetector.det.model.Risk
import com.rootdetector.det.model.RootType

class ChkPkg : Chker {

    private val pkgs = mapOf(
        "com.topjohnwu.magisk" to RootType.MAGISK,
        "io.github.huskydg.magisk" to RootType.MAGISK,
        "me.weishu.kernelsu" to RootType.KERNELSU,
        "me.bmax.apatch" to RootType.APATCH,
        "eu.chainfire.supersu" to RootType.SUPERSU,
        "com.noshufou.android.su" to RootType.SUPERSU,
        "com.thirdparty.superuser" to RootType.SUPERSU,
        "com.koushikdutta.superuser" to RootType.SUPERSU,
        "com.mgyun.shua.su" to RootType.UNKNOWN,
        "com.yellowes.su" to RootType.UNKNOWN,
        "com.rummy.su" to RootType.UNKNOWN,
        "com.dimonvideo.luckypatcher" to RootType.UNKNOWN,
        "com.chelpus.lackypatch" to RootType.UNKNOWN,
        "de.robv.android.xposed.installer" to RootType.UNKNOWN,
        "com.devadvance.rootcloak" to RootType.UNKNOWN,
        "com.amphoras.hidemyroot" to RootType.UNKNOWN,
        "com.saurik.substrate" to RootType.UNKNOWN,
        "com.formicro.yackboard" to RootType.UNKNOWN,
        "com.kimcy92.noteroot" to RootType.UNKNOWN
    )

    override suspend fun chk(ctx: Context): ChkRes {
        val pm = ctx.packageManager
        val found = mutableListOf<Pair<String, RootType>>()

        for ((pkg, rt) in pkgs) {
            try {
                pm.getPackageInfo(pkg, 0)
                found.add(pkg to rt)
            } catch (_: PackageManager.NameNotFoundException) { }
        }

        return ChkRes(
            name = "Packages",
            found = found.isNotEmpty(),
            risk = if (found.isNotEmpty()) Risk.HIGH else Risk.NONE,
            detail = if (found.isNotEmpty())
                found.joinToString(",\n") { "${it.first} → ${it.second.name}" }
            else
                "No root management apps found"
        )
    }
}
