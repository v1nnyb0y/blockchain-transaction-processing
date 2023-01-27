package com.bknprocessing.backend.service

import com.bknprocessing.backend.models.Block
import com.bknprocessing.backend.models.Node
import com.bknprocessing.backend.models.Transaction
import com.bknprocessing.backend.type.ValidatorAlgorithm
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class PoolService @OptIn(ObsoleteCoroutinesApi::class) constructor(
    val txChannel: ConflatedBroadcastChannel<Transaction>, // TODO swap to StateFlow
    val blockChannel: Channel<Block>,
    val resultChannel: Channel<Pair<Boolean, Block>>,
    val nodesCount: Int,
    val unhealthyNodesCount: Int,
    val validatorAlgorithm: ValidatorAlgorithm
) {

    private val nodes = mutableListOf<Node>()

    init {
        for (idx in 0..nodesCount - unhealthyNodesCount) {
            nodes.add(Node(index = idx, nodesCount = nodesCount, isHealthy = true))
        }
        for (idx in 0..unhealthyNodesCount) {
            nodes.add(Node(index = idx, nodesCount = nodesCount, isHealthy = false))
        }
    }

    suspend fun run(numberOfTransactions: Int) {
        for (i in 0 until nodes.size) {
            awaitSupervisor(
                { doMine(nodes[i]) },
                { doVerify(nodes[i]) },
                { doSendTransactions(numberOfTransactions) }
            )
        }
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private suspend fun doMine(node: Node) {
        if (node.isMiner()) node.runMining(txChannel, blockChannel, resultChannel) // TODO naming args
    }

    private suspend fun doVerify(node: Node) {
        node.runVerifying(blockChannel, resultChannel) // TODO naming args
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private suspend fun doSendTransactions(numberOfTransactions: Int) {
        var i = 1
        while (false && i <= numberOfTransactions) {
            val result = txChannel.trySend(Transaction())
            if (result.isSuccess) {
                i += 1
            }
        }
    }

    private suspend fun awaitSupervisor(vararg units: suspend () -> Unit) =
        supervisorScope {
            units.forEach {
                launch { it() }
            }
        }
}
