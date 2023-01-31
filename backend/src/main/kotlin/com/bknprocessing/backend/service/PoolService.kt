package com.bknprocessing.backend.service

import com.bknprocessing.backend.models.Block
import com.bknprocessing.backend.models.Node
import com.bknprocessing.backend.models.Transaction
import com.bknprocessing.backend.type.ValidatorAlgorithm
import com.bknprocessing.backend.utils.logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger

class PoolService(
    val nodesCount: Int,
    val unhealthyNodesCount: Int,
    val validatorAlgorithm: ValidatorAlgorithm
) {

    private val log: Logger by logger()

    val nodes = mutableListOf<Node>()

    private val transChannel = Channel<Transaction>(capacity = 1)

    @OptIn(ObsoleteCoroutinesApi::class)
    private val blockChannel = BroadcastChannel<Block>(capacity = nodesCount * nodesCount)
    private val resultChannel = Channel<Pair<Boolean, Block>>(capacity = UNLIMITED)

    init {
        for (idx in 0..nodesCount - unhealthyNodesCount) {
            nodes.add(Node(index = idx, nodesCount = nodesCount, isHealthy = true))
        }
        for (idx in 0..unhealthyNodesCount) {
            nodes.add(Node(index = idx, nodesCount = nodesCount, isHealthy = false))
        }
    }

    /*suspend fun run(numberOfTransactions: Int) {
        for (i in 0 until nodes.size) {
            awaitSupervisor(
                { doMine(nodes[i]) },
                { doVerify(nodes[i]) },
                { doSendTransactions(numberOfTransactions) }
            )
        }
    }*/

    @OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
    suspend fun run(numberOfTransactions: Int) = coroutineScope {
        for (i in 0 until nodes.size) {
            launch { doMine(nodes[i]) }
            launch { doVerify(nodes[i]) }
        }

        launch {
            doSendTransactions(numberOfTransactions)
            while (!transChannel.isEmpty ||
                !resultChannel.isEmpty ||
                !blockChannel.openSubscription().isEmpty
            ) {
                log.info(
                    "trans-channel = ${transChannel.isEmpty}, " +
                        "result-channel = ${resultChannel.isEmpty}, " +
                        "block-channel = ${blockChannel.openSubscription().isEmpty}"
                )
                delay(250)
            }

            for (i in 0 until nodes.size) {
                nodes[i].isFinished = true
            }
        }
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private suspend fun doMine(node: Node) {
        if (node.isMiner()) node.runMining(
            forTransChannel = transChannel,
            forVerifyChannel = blockChannel,
            forVerificationResultChannel = resultChannel
        )
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private suspend fun doVerify(node: Node) {
        node.runVerifying(
            forVerifyChannel = blockChannel.openSubscription(),
            forResultChannel = resultChannel
        )
    }

    private fun doSendTransactions(numberOfTransactions: Int) {
        var i = 1
        while (i <= numberOfTransactions) {
            val result = transChannel.trySend(Transaction())
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
