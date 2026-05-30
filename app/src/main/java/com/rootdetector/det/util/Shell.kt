package com.rootdetector.det.util

import java.util.concurrent.TimeUnit

object Shell {
    fun exec(cmd: String, timeout: Long = 3): String {
        return try {
            val pb = ProcessBuilder("/system/bin/sh", "-c", cmd)
            pb.redirectErrorStream(true)
            val p = pb.start()
            val finished = p.waitFor(timeout, TimeUnit.SECONDS)
            if (!finished) p.destroyForcibly()
            p.inputStream.bufferedReader().readText().trim()
        } catch (_: Exception) { "" }
    }

    fun exists(cmd: String): Boolean {
        return try {
            val pb = ProcessBuilder("/system/bin/sh", "-c", cmd)
            pb.redirectErrorStream(true)
            val p = pb.start()
            val finished = p.waitFor(3, TimeUnit.SECONDS)
            if (!finished) { p.destroyForcibly(); return false }
            val ok = p.inputStream.bufferedReader().readLine() != null
            p.destroy()
            ok
        } catch (_: Exception) { false }
    }
}
