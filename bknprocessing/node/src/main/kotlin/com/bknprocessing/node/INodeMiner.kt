package com.bknprocessing.node

import com.bknprocessing.node.data.Transaction
import com.bknprocessing.node.dto.Block

interface INodeMiner {

    fun constructBlock(tx: Transaction): Block

    fun mineBlock(block: Block): Block?

    fun isMiner(): Boolean

    // todo use smart-contract for that operation
    fun addBlockToChain(block: Block)
    fun removeBlockFromChain()
}
