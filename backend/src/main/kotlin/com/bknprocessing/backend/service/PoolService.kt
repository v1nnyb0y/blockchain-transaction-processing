package com.bknprocessing.backend.service

import com.bknprocessing.backend.models.Node
import com.bknprocessing.backend.models.Block
import com.bknprocessing.backend.models.Transaction
import com.bknprocessing.backend.type.ValidatorAlgorithm
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PoolService {

    private val nodes = mutableListOf<Node>()

    fun createExpPool(instancesCount: Int, unhealthyNodesCount: Int, validatorAlgo: ValidatorAlgorithm) {
        createListNode(unhealthyNodesCount, false, instancesCount)
        createListNode(instancesCount - unhealthyNodesCount, true, instancesCount)
    }

    fun startExpPool(numberOfTransactions: Int) = runBlocking {
        val transChannel = Channel<Transaction>(1)
        val blockChannel = Channel<Block>()
        val resultChannel = Channel<Pair<Boolean, Block>>()

        for (i in 0 until nodes.size) {
            launch {
                if (nodes[i].isMiner()) nodes[i].runMining(transChannel, blockChannel, resultChannel)
            }
            launch {
                nodes[i].runVerifying(blockChannel, resultChannel)
            }
        }

        launch {
            var i = 1
            while (i <= numberOfTransactions) {
                val result = transChannel.trySend(Transaction())
                if (result.isSuccess) {
                    i += 1
                }
            }
        }
    }

    private fun createListNode(count: Int, isHealthy: Boolean, numberOfInstances: Int) {
        for (i in 1..count) {
            nodes.add(Node(isHealthy = isHealthy, numberOfNodes = numberOfInstances))
        }
    }
}
