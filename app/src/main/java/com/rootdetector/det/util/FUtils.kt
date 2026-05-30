package com.rootdetector.det.util

object FUtils {
    fun exists(path: String): Boolean {
        val out = Shell.exec("test -e \"$path\" && echo 1 || echo 0")
        return out.trim() == "1"
    }

    fun canRead(path: String): Boolean {
        val out = Shell.exec("test -r \"$path\" && echo 1 || echo 0")
        return out.trim() == "1"
    }

    fun isDir(path: String): Boolean {
        val out = Shell.exec("test -d \"$path\" && echo 1 || echo 0")
        return out.trim() == "1"
    }
}
