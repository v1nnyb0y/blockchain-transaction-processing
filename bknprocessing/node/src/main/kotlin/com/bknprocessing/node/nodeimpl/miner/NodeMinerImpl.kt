package com.bknprocessing.node.nodeimpl.miner

import com.bknprocessing.node.dto.Block
import com.bknprocessing.node.dto.NodeInfoSubBlock
import com.bknprocessing.node.nodeimpl.Node
import java.time.Instant
import java.util.UUID

open class NodeMinerImpl<T>(
    val calculateHash: (Block<T>) -> String,
    val isMined: (Block<T>) -> Boolean,
) : INodeMiner<T> {

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

    override fun constructBlock(obj: T, previousHash: String, nodeId: UUID, nodeIndex: Int, amount: Int) =
        Block<T>(
            previousHash = previousHash,
            nodeInfo = NodeInfoSubBlock(amount = amount, index = nodeIndex, id = nodeId),
            processingTime = Instant.now().toEpochMilli(),
        ).apply { addObj(obj) }.calculateAndSetCurrentHash()

    protected fun Block<T>.calculateAndSetCurrentHash() = apply {
        currentHash = calculateHash(this)
    }

    protected fun Block<T>.nonceIncrement() = copy(nonce = nonce + 1) // TODO copy change to apply
    // protected fun Block<T>.nonceIncrement() = apply { nonce += 1 }
}
