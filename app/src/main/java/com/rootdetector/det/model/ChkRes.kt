package com.rootdetector.det.model

data class ChkRes(
    val name: String,
    val found: Boolean,
    val risk: Risk,
    val detail: String = ""
)
