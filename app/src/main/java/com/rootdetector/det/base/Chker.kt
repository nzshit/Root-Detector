package com.rootdetector.det.base

import android.content.Context
import com.rootdetector.det.model.ChkRes

interface Chker {
    suspend fun chk(ctx: Context): ChkRes
}
