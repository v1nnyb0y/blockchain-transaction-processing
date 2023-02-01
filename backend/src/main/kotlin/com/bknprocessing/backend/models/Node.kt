package com.bknprocessing.backend.models

import com.bknprocessing.backend.utils.logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import org.slf4j.Logger
import java.util.UUID
import kotlin.random.Random

class Node(
    private val index: Int,
    private val nodesCount: Int,
    private val isHealthy: Boolean
) : INode {

    private val log: Logger by logger()

    private val id: UUID = UUID.randomUUID()

    private var amount = Random.nextInt(MIN_MONEY, MAX_MONEY)

    private var chain: MutableList<Block> = mutableListOf()
    private var lastAddedIntoChainBlockHash: String = ""

    // Experimental properties for testing bellow
    var countOfHandledTrans: Int = 0
    var isFinished: Boolean = false

    init {
        var genericBlock = Block(previousHash = lastAddedIntoChainBlockHash)
        genericBlock = mineBlock(genericBlock, false)
        chain.add(genericBlock)
        lastAddedIntoChainBlockHash = genericBlock.currentHash
    }

    suspend fun runMining(
        forTransChannel: ReceiveChannel<Transaction>,
        forVerifyChannel: SendChannel<Block>,
        forVerificationResultChannel: Channel<Pair<Boolean, Block>>
    ) {
        while (!isFinished) {
            delay(100)
            val trans = forTransChannel.tryReceive().getOrNull() ?: continue
            countOfHandledTrans += 1

            val healthyStr = if (isHealthy) "healthy" else "unhealthy"
            log.info("Node with INDEX: $index ($healthyStr) is working (Mining)")
            val verifyingBlock = constructBlock(trans)

            forVerifyChannel.send(verifyingBlock)
            var countOfFinishedNodes = 0
            var countOfSuccessNodes = 0
            // while (countOfSuccessNodes / (nodesCount - 1) - 0.8 < EPSILON) {
            while (true) {
                delay(100)
                if (countOfFinishedNodes == nodesCount - 1) break
                val result = forVerificationResultChannel.tryReceive().getOrNull() ?: continue

                if (result.second.currentHash == verifyingBlock.currentHash) {
                    countOfFinishedNodes += 1

                    if (result.first) {
                        countOfSuccessNodes += 1
                    }
                } else {
                    forVerificationResultChannel.send(result)
                }
            }

            if (countOfSuccessNodes / (nodesCount - 1) - 0.8 >= EPSILON) {
                log.info("Node with INDEX: $index is verified transaction with ID: ${trans.transId}")
            } else {
                log.info("Node with INDEX: $index isn't verified transaction with ID: ${trans.transId}")
            }
        }
    }

    suspend fun runVerifying(
        forVerifyChannel: ReceiveChannel<Block>,
        forResultChannel: SendChannel<Pair<Boolean, Block>>
    ) {
        while (!isFinished) {
            delay(100)
            val block = forVerifyChannel.tryReceive().getOrNull() ?: continue

            val healthyStr = if (isHealthy) "healthy" else "unhealthy"
            log.info("Node with INDEX: $index ($healthyStr) is working (Verifying)")
            forResultChannel.send(Pair(verifyBlock(block), block))
        }
    }

    override fun isMiner(): Boolean = amount > (MAX_MONEY / 20)

    override suspend fun verifyBlock(block: Block): Boolean {
        val minedBlock = if (isMined(block)) block else mineBlock(block)
        chain.add(minedBlock)

        if (!isChainValid()) {
            chain.removeLast()
            return false
        }

        lastAddedIntoChainBlockHash = minedBlock.currentHash
        amount += 1
        return true
    }

    override suspend fun constructBlock(tx: Transaction) =
        Block(previousHash = lastAddedIntoChainBlockHash).apply { addTransaction(tx) }

    override fun mineBlock(block: Block, ignoreLog: Boolean): Block {
        if (!ignoreLog) log.info("Mining: ${block.currentHash}")

        var minedBlock = block.copy()
        while (!isMined(minedBlock)) {
            minedBlock = minedBlock.copy(nonce = minedBlock.nonce + 1)
        }

        if (!ignoreLog) log.info("Mined : ${minedBlock.currentHash}")

        return minedBlock
    }

    override fun isChainValid(): Boolean {
        when {
            chain.isEmpty() -> return true
            chain.size == 1 -> return chain[0].currentHash == chain[0].calculateBlockHash()
            else -> {
                for (i in 1 until chain.size) {
                    val previousBlock = chain[i - 1]
                    val currentBlock = chain[i]

                    when {
                        currentBlock.currentHash != currentBlock.calculateBlockHash() -> return false
                        currentBlock.previousHash != previousBlock.calculateBlockHash() -> return false
                        !(isMined(previousBlock) && isMined(currentBlock)) -> return false
                    }
                }
                return true
            }
        }
    }

    private fun isMined(block: Block): Boolean {
        return block.currentHash.startsWith(validPrefix)
    }

    companion object {
        private val MONEY_INT: Int = 4
        private val MAX_MONEY: Int = 10000
        private val MIN_MONEY: Int = 100

        private val EPSILON = 0.0000000001

        private val difficulty = 2
        private val validPrefix = "0".repeat(difficulty)
    }
}
