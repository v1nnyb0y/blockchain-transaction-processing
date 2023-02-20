package com.bknprocessing.node.nodeimpl.miner

import com.bknprocessing.node.dto.Block
import java.util.UUID

interface INodeMiner<T> {

    fun isMiner(amount: Int): Boolean

    fun mineBlock(block: Block<T>): Block<T>?

    fun constructBlock(obj: T, previousHash: String, nodeId: UUID): Block<T>
}
