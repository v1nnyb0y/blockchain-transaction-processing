package com.bknprocessing.backend.pool

import com.bknprocessing.backend.blockchain.Node
import com.bknprocessing.backend.blockchain.block.Block
import com.bknprocessing.backend.controllers.models.ValidatorAlgorithm
import com.bknprocessing.backend.entity.Transaction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class NodePoolService {

    val nodeList = mutableListOf<Node>()

    fun createExpPool(numberOfInstances: Int, numberOfUnhealthyNodes: Int, validatorAlgo: ValidatorAlgorithm) {
        createListNode(numberOfUnhealthyNodes, false, numberOfInstances)
        createListNode(numberOfInstances - numberOfUnhealthyNodes, true, numberOfInstances)
    }

    fun startExpPool(numberOfTransactions: Int) = runBlocking {
        val transChannel = Channel<Transaction>(1)
        val blockChannel = Channel<Block>()
        val resultChannel = Channel<Pair<Boolean, Block>>()

        for (i in 0 until nodeList.size) {
            launch {
                if (nodeList[i].isMiner()) nodeList[i].runMining(transChannel, blockChannel, resultChannel)
            }
            launch {
                nodeList[i].runVerifying(blockChannel, resultChannel)
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
            nodeList.add(Node(isHealthy = isHealthy, numberOfNodes = numberOfInstances))
        }
    }
}
