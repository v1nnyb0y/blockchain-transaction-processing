package com.bknprocessing.backend.models

import java.util.UUID

interface INode {
    val index: Int
    val id: UUID
    var amount: Int
    val isHealthy: Boolean

    fun constructBlock(tx: Transaction): Block

    fun mineBlock(block: Block): Block?

    fun verifyBlock(block: Block): Boolean

    fun isMiner(): Boolean

    // todo use smart-contract for that operation
    fun addBlockToChain(block: Block)

    fun removeBlockFromChain()

    fun countBlocksCreatedByNodeInChain(nodeId: UUID): Long
}
