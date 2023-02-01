package com.bknprocessing.backend.models

interface INode {

    fun mineBlock(block: Block, ignoreLog: Boolean = false): Block

    suspend fun verifyBlock(block: Block): Boolean

    fun isMiner(): Boolean

    fun isChainValid(): Boolean

    suspend fun constructBlock(tx: Transaction): Block
}
