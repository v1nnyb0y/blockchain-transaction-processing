package com.bknprocessing.backend.models

import com.bknprocessing.backend.utils.logger
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import org.slf4j.Logger
import java.util.UUID
import kotlin.random.Random

class Node(
    private val index: Int,
    private val nodesCount: Int,
    private val isHealthy: Boolean
) {

    val log: Logger by logger()

    private val Id: UUID = UUID.randomUUID()
    private val MONEY_INT: Int = 4
    private val MAX_MONEY: Int = 10000
    private val MIN_MONEY: Int = 100
    private var money = Random.nextInt(MIN_MONEY, MAX_MONEY)

    var lastHash: String = ""

    private var blocks: MutableList<Block> = mutableListOf()

    private val difficulty = 5
    private val validPrefix = "0".repeat(difficulty)

    init {
        val genericBlock = if (isHealthy) Block(previousHash = lastHash) else UnhealthyBlock(previousHash = lastHash)
        blocks.add(genericBlock)
    }

    fun isMiner() = money > (MAX_MONEY / 20)

    @OptIn(ObsoleteCoroutinesApi::class)
    suspend fun runMining(
        forTransChannel: ConflatedBroadcastChannel<Transaction>,
        forVerifyChannel: Channel<Block>,
        forResultChannel: Channel<Pair<Boolean, Block>>
    ) {
        forTransChannel.consumeEach { trans ->
            log.info("Node with ID: $Id is working (Mining)")
            val verifyingBlock = constructBlock(trans)

            forVerifyChannel.send(verifyingBlock)
            var countOfFinishedNodes = 0
            var countOfSuccessNodes = 0
            while (countOfFinishedNodes == nodesCount ||
                (countOfSuccessNodes / nodesCount > 0.8)
            ) {
                val objResult = forResultChannel.tryReceive()
                if (objResult.isSuccess) {
                    val result = objResult.getOrNull()!!
                    if (result.second.hash == verifyingBlock.hash) {
                        countOfFinishedNodes += 1

                        if (result.first) {
                            countOfSuccessNodes += 1
                        }
                    } else {
                        forResultChannel.send(result)
                    }
                }
                break
            }

            if (countOfSuccessNodes / nodesCount > 0.8) {
                log.info("Node with ID: $Id is verified transaction with ID: ${trans.transId}")
            } else {
                log.info("Node with ID: $Id isn't verified transaction with ID: ${trans.transId}")
            }
        }
    }

    suspend fun runVerifying(
        forVerifyChannel: Channel<Block>,
        forResultChannel: Channel<Pair<Boolean, Block>>
    ) {
        while (true) {
            val objBlock = forVerifyChannel.tryReceive()
            if (objBlock.isSuccess) {
                log.info("Node with ID: $Id is working (Verifying)")
                val block = objBlock.getOrNull()!!
                forResultChannel.send(Pair(verifyBlock(block), block))
            }
            delay(100)
        }
    }

    fun verifyBlock(block: Block): Boolean {
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

    private fun constructBlock(trans: Transaction): Block =
        (if (isHealthy) Block(previousHash = lastHash) else UnhealthyBlock(previousHash = lastHash))
            .also { it.addTransaction(trans) }

    private fun isMined(block: Block): Boolean {
        return block.hash.startsWith(validPrefix)
    }

    private fun mine(block: Block): Block {
        log.info("Mining: $block")

        var minedBlock = block
        while (!isMined(minedBlock)) {
            minedBlock = minedBlock.also {
                it.nonce = minedBlock.nonce + 1
            }
        }

        log.info("Mined : $minedBlock")

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
