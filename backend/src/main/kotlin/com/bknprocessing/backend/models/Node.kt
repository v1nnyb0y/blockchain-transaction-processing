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
) {

    private val log: Logger by logger()

    private val id: UUID = UUID.randomUUID()
    private var money = Random.nextInt(MIN_MONEY, MAX_MONEY)

    private var lastHash: String = ""
    private var blocks: MutableList<Block> = mutableListOf()

    var isFinished: Boolean = false

    // Properties for testing
    var countOfHandledTrans: Int = 0

    companion object {
        private const val MONEY_INT: Int = 4
        private const val MAX_MONEY: Int = 10000
        private const val MIN_MONEY: Int = 100

        private const val EPSILON = 0.0000000001

        private const val difficulty = 2
        private val validPrefix = "0".repeat(difficulty)
    }

    init {
        var genericBlock = Block(previousHash = lastHash, isHealthy = isHealthy)
        genericBlock = mine(genericBlock, false)
        blocks.add(genericBlock)
        lastHash = genericBlock.hash
    }

    fun isMiner() = money > (MAX_MONEY / 20)

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

                if (result.second.hash == verifyingBlock.hash) {
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

    private fun verifyBlock(block: Block): Boolean {
        val minedBlock = if (isMined(block)) block else mine(block)
        blocks.add(minedBlock)

        if (!isValid()) {
            blocks.removeLast()
            return false
        }

        lastHash = minedBlock.hash
        money += 1
        return true
    }

    private fun constructBlock(trans: Transaction) = Block(
        previousHash = lastHash,
        isHealthy = isHealthy
    ).apply { addTransaction(trans) }

    private fun isMined(block: Block): Boolean {
        return block.hash.startsWith(validPrefix)
    }

    private fun mine(block: Block, ignoreLog: Boolean = false): Block {
        if (!ignoreLog) log.info("Mining: ${block.hash}")

        var minedBlock = block.copy()
        while (!isMined(minedBlock)) {
            minedBlock = minedBlock.copy(nonce = minedBlock.nonce + 1)
        }

        if (!ignoreLog) log.info("Mined : ${minedBlock.hash}")

        return minedBlock
    }

    private fun isValid(): Boolean {
        when {
            blocks.isEmpty() -> return true
            blocks.size == 1 -> return blocks[0].hash == blocks[0].calculateHash()
            else -> {
                for (i in 1 until blocks.size) {
                    val previousBlock = blocks[i - 1]
                    val currentBlock = blocks[i]

                    when {
                        currentBlock.hash != currentBlock.calculateHash() -> return false
                        currentBlock.previousHash != previousBlock.calculateHash() -> return false
                        !(isMined(previousBlock) && isMined(currentBlock)) -> return false
                    }
                }
                return true
            }
        }
    }
}
