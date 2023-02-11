package com.bknprocessing.backend.models

import com.bknprocessing.backend.utils.constructBlock
import com.bknprocessing.backend.utils.endMining
import com.bknprocessing.backend.utils.endVerify
import com.bknprocessing.backend.utils.hash
import com.bknprocessing.backend.utils.logger
import com.bknprocessing.backend.utils.startMining
import com.bknprocessing.backend.utils.startVerify
import org.slf4j.Logger
import kotlin.random.Random

class Node(
    override val index: Int,
    override val isHealthy: Boolean
) : INode {

    private val log: Logger by logger()
    private var ignoreLog: Boolean = true

    private var amount = Random.nextInt(MIN_MONEY, MAX_MONEY)

    private var chain: MutableList<Block> = mutableListOf()
    private var lastAddedIntoChainBlockHash: String = ""

    init {
        mineBlock(
            Block(previousHash = lastAddedIntoChainBlockHash)
                .apply { addTransaction(Transaction()) }
                .calculateAndAssignHash()
        )
        // mineBlock(constructBlock(Transaction()))
        ignoreLog = false
    }

    override fun isMiner(): Boolean = amount > (MAX_MONEY / 20)

    /* Miner node actions */

    private fun Block.calculateHash() = "$previousHash$transactions$timestamp$nonce".hash()

    private fun Block.calculateAndAssignHash() = apply {
        currentHash = calculateHash()
    }

    private fun Block.isMined() = currentHash.startsWith(validPrefix)

    private fun Block.nonceIncrement() = copy(nonce = nonce + 1)

    override fun constructBlock(tx: Transaction): Block {
        if (!ignoreLog) log.constructBlock(isHealthy, index)
        return Block(previousHash = lastAddedIntoChainBlockHash)
            .apply { addTransaction(tx) }
            .calculateAndAssignHash()
    }

    override fun mineBlock(block: Block): Block {
        /*if (block.isMined()) {
            if (!ignoreLog) log.blockAlreadyMined(isHealthy, index, block.currentHash)
            return block
        }*/
        if (!ignoreLog) log.startMining(isHealthy, index, block.currentHash)

        var minedBlock = block.copy()
        while (!minedBlock.isMined()) {
            minedBlock = minedBlock
                .nonceIncrement()
                .calculateAndAssignHash()
        }

        if (!ignoreLog) log.endMining(isHealthy, index, block.currentHash)

        chain.add(minedBlock)
        lastAddedIntoChainBlockHash = minedBlock.currentHash
        return minedBlock
    }

    override fun removeBlockFromChain() {
        chain.removeLast()
        lastAddedIntoChainBlockHash = chain.last().currentHash
    }

    /* Miner node actions */

    /* Verify node actions */

    private fun isChainValid(): Boolean {
        when {
            chain.isEmpty() -> return true
            chain.size == 1 -> return chain[0].currentHash == chain[0].calculateHash()
            else -> {
                for (i in 1 until chain.size) {
                    val previousBlock = chain[i - 1]
                    val currentBlock = chain[i]

                    when {
                        currentBlock.currentHash != currentBlock.calculateHash() -> return false
                        currentBlock.previousHash != previousBlock.calculateHash() -> return false
                        !(previousBlock.isMined() && currentBlock.isMined()) -> return false
                    }
                }
                return true
            }
        }
    }

    override fun verifyBlock(block: Block): Boolean {
        if (!ignoreLog) log.startVerify(isHealthy, index, block.currentHash)
        chain.add(block)

        if (!isChainValid()) {
            if (!ignoreLog) log.endVerify(isHealthy, index, block.currentHash, false)
            chain.removeLast()
            return false
        }

        if (!ignoreLog) log.endVerify(isHealthy, index, block.currentHash, true)
        lastAddedIntoChainBlockHash = block.currentHash
        amount += 1
        return true
    }

    /* Verify node actions */

    companion object {
        private val MONEY_INT: Int = 4
        private val MAX_MONEY: Int = 10000
        private val MIN_MONEY: Int = 100

        private val difficulty = 2
        private val validPrefix = "0".repeat(difficulty)
    }
}
