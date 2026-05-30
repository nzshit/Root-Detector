package com.rootdetector.det

import android.content.Context
import com.rootdetector.det.base.Chker
import com.rootdetector.det.cheking.ChkBL
import com.rootdetector.det.cheking.ChkBin
import com.rootdetector.det.cheking.ChkFile
import com.rootdetector.det.cheking.ChkPkg
import com.rootdetector.det.cheking.ChkProc
import com.rootdetector.det.cheking.ChkProp
import com.rootdetector.det.cheking.ChkRW
import com.rootdetector.det.cheking.ChkSel
import com.rootdetector.det.model.ChkRes
import com.rootdetector.det.model.RootType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class RootDet {

    private val layers: List<Chker> = listOf(
        ChkBin(), ChkPkg(), ChkFile(), ChkProp(), ChkSel(), ChkRW(), ChkBL(), ChkProc()
    )

    data class DetRes(
        val results: List<ChkRes>,
        val score: Int,
        val rootType: RootType
    )

    suspend fun scan(ctx: Context): DetRes = withContext(Dispatchers.IO) {
        val deferred = layers.map { async { it.chk(ctx) } }
        val results = deferred.awaitAll()
        val found = results.count { it.found }
        val score = (found * 100) / layers.size
        val rootType = detectType(results)
        DetRes(results, score, rootType)
    }

    private fun detectType(rs: List<ChkRes>): RootType {
        val all = rs.flatMap { it.detail.split(",\n") }.joinToString(" ").lowercase()
        return when {
            all.contains("kernelsu") -> RootType.KERNELSU
            all.contains("apatch") || all.contains("apd") -> RootType.APATCH
            all.contains("magisk") -> RootType.MAGISK
            all.contains("supersu") || all.contains("superuser") -> RootType.SUPERSU
            all.contains("su(") || all.contains("su ") -> RootType.UNKNOWN
            else -> RootType.NONE
        }
    }
}
