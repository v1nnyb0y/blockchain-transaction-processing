package com.bknprocessing.node.utils // ktlint-disable filename

import com.bknprocessing.node.nodeimpl.Node
import java.util.UUID

fun <T> determineNextIterationMinerIndex(
    nodesMap: Map<UUID, Node<T>.VerifiedBlocksAndAmountInfo>,
): UUID {
    if (nodesMap.size < 2) {
        throw IllegalStateException("Determine index process: Node size is incorrect")
    }
    var leaderValue = -1
    var leaderIndex: UUID? = null

    nodesMap.entries.forEach {
        val blocksCount = if (it.value.blocksCount == 0L) -1L else it.value.blocksCount
        if (leaderValue < it.value.amount / blocksCount) {
            leaderValue = it.value.amount
            leaderIndex = it.key
        }
    }
    return leaderIndex ?: throw IllegalStateException("Some exception in determining miner for new round")
}
