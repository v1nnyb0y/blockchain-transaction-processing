package com.bknprocessing.backend.models

import com.bknprocessing.backend.utils.blockAlreadyMined
import com.bknprocessing.backend.utils.constructBlock
import com.bknprocessing.backend.utils.endMining
import com.bknprocessing.backend.utils.endVerify
import com.bknprocessing.backend.utils.hash
import com.bknprocessing.backend.utils.logger
import com.bknprocessing.backend.utils.startMining
import com.bknprocessing.backend.utils.startVerify
import org.slf4j.Logger
import java.lang.StringBuilder
import kotlin.random.Random

open class Node(
    override val index: Int,
    override val isHealthy: Boolean,
    protected val createdAt: Long
) : INode {

    protected val log: Logger by logger()
    protected var ignoreLog: Boolean = true

    protected var amount = Random.nextInt(MIN_MONEY, MAX_MONEY)

    protected var chain: MutableList<Block> = mutableListOf()
    protected var lastAddedIntoChainBlockHash: String = ""

    init {
        amount = if (index == 0) MAX_MONEY else 0
        val minedBlock = mineBlock(
            Block(previousHash = StringBuilder(lastAddedIntoChainBlockHash).toString(), timestamp = createdAt)
                .calculateAndAssignHash()
        )
        lastAddedIntoChainBlockHash = StringBuilder(minedBlock!!.currentHash).toString()
        ignoreLog = false
    }

    override fun isMiner(): Boolean = amount > (MAX_MONEY / 20)

    /* Miner node actions */

    protected fun Block.calculateHash() = "$previousHash$transactions$timestamp$nonce".hash()

    protected fun Block.calculateAndAssignHash() = apply {
        currentHash = calculateHash()
    }

    protected fun Block.isMined() = currentHash.startsWith(validPrefix)

    protected fun Block.nonceIncrement() = copy(nonce = nonce + 1)

    override fun constructBlock(tx: Transaction): Block {
        if (!ignoreLog) log.constructBlock(isHealthy, index)
        return Block(previousHash = StringBuilder(lastAddedIntoChainBlockHash).toString())
            .apply { addTransaction(tx) }
            .calculateAndAssignHash()
    }

    override fun mineBlock(block: Block): Block? {
        if (block.isMined()) {
            if (!ignoreLog) log.blockAlreadyMined(isHealthy, index, block.currentHash)
            return null
        }
        if (!ignoreLog) log.startMining(isHealthy, index, block.currentHash)

        var minedBlock = block.copy()
        while (!minedBlock.isMined()) {
            minedBlock = minedBlock
                .nonceIncrement()
                .calculateAndAssignHash()
        }

        if (!ignoreLog) log.endMining(isHealthy, index, block.currentHash)
        return minedBlock
    }

    override fun removeBlockFromChain() {
        // chain.removeLast()
        // lastAddedIntoChainBlockHash = chain.last().currentHash
    }

    override fun addBlockToChain(block: Block) {
        chain.add(block)
        lastAddedIntoChainBlockHash = StringBuilder(block.currentHash).toString()
    }

    /* Miner node actions */

    /* Verify node actions */

    protected fun isChainValid(): Boolean {
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
        lastAddedIntoChainBlockHash = StringBuilder(block.currentHash).toString()
        // amount += 1
        return true
    }

    /* Verify node actions */

    companion object {
        protected val MONEY_INT: Int = 4
        protected val MAX_MONEY: Int = 10000
        protected val MIN_MONEY: Int = 100

        protected val difficulty = 2
        protected val validPrefix = "0".repeat(difficulty)
    }
}
