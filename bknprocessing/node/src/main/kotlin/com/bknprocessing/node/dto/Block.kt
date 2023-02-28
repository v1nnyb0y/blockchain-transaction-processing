package com.bknprocessing.node.dto

import java.time.Instant
import java.util.UUID

data class NodeInfoSubBlock(
    val amount: Int,
    val index: Int,
    val id: UUID,
)

data class Block<T>(
    val previousHash: String,
    val nodeInfo: NodeInfoSubBlock?,

    var currentHash: String = "",
    val timestamp: Long = Instant.now().toEpochMilli(),

    val objs: MutableList<T> = mutableListOf(),

    var nonce: Long = 0,
) {

    fun addObj(tx: T) {
        objs.add(tx)
    }
}
