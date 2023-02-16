package com.bknprocessing.node.nodeimpl.miner

import com.bknprocessing.node.dto.Block
import com.bknprocessing.node.nodeimpl.Node
import java.util.UUID

class NodeMinerImpl<T>(
    val calculateHash: (Block<T>) -> String,
    val isMined: (Block<T>) -> Boolean,
) : INodeMiner<T> {

    private fun Block<T>.calculateAndSetCurrentHash() = apply {
        currentHash = calculateHash(this)
    }
    private fun Block<T>.nonceIncrement() = copy(nonce = nonce + 1)

    override fun isMiner(amount: Int): Boolean = amount > (Node.MAX_MONEY / 20)

    override fun mineBlock(block: Block<T>): Block<T>? {
        if (isMined(block)) return null

        var minedBlock = block.copy()
        while (!isMined(minedBlock)) {
            minedBlock = minedBlock
                .nonceIncrement()
                .calculateAndSetCurrentHash()
        }

        return minedBlock
    }

    override fun constructBlock(obj: T, previousHash: String, nodeId: UUID): Block<T> {
        return Block<T>(
            previousHash = previousHash,
            generatedBy = nodeId,
        )
            .apply { addObj(obj) }
            .calculateAndSetCurrentHash()
    }
}
