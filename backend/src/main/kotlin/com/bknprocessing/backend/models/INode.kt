package com.bknprocessing.backend.models

interface INode {
    val index: Int
    val isHealthy: Boolean

    fun mineBlock(block: Block): Block?

    fun verifyBlock(block: Block): Boolean

    fun isMiner(): Boolean

    fun constructBlock(tx: Transaction): Block

    // todo use smart-contract for that operation
    fun addBlockToChain(block: Block)
    fun removeBlockFromChain()
}
