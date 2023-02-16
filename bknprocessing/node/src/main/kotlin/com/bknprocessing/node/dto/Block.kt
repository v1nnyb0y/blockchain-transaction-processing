package com.bknprocessing.node.dto

import java.time.Instant
import java.util.UUID

data class Block<T>(
    val previousHash: String,
    val generatedBy: UUID?,

    var currentHash: String = "",
    val timestamp: Long = Instant.now().toEpochMilli(),
    val objs: MutableList<T> = mutableListOf(),

    var nonce: Long = 0,
) {

    fun addObj(tx: T) {
        objs.add(tx)
    }
}
