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
import java.util.UUID
import kotlin.random.Random

open class Node(
    final override val index: Int,
    final override val id: UUID = UUID.randomUUID(),
    final override var amount: Int = Random.nextInt(MIN_MONEY, MAX_MONEY),
    override val isHealthy: Boolean,
    protected val createdAt: Long,
) : INode {

    private val log: Logger by logger()
    private var ignoreLog: Boolean = true

    protected var chain: MutableList<Block> = mutableListOf()
    protected var lastAddedIntoChainBlockHash: String = ""

    init {
        // One miner and one verifier should be 100%
        if (index == 0) amount = MAX_MONEY
        if (index == 1) amount = 0

        // TODO Vitalii look on it ??
        if (index > 0) {
            amount = 0
        }

        addBlockToChain(
            block = mineBlock(
                Block(
                    previousHash = StringBuilder(lastAddedIntoChainBlockHash).toString(),
                    timestamp = createdAt,
                    generatedBy = this.id,
                ).calculateAndSetCurrentHash(),
            )!!, // TODO FIX NPE (Cover Test)
        )
        ignoreLog = false
    }

    private fun Block.calculateHash() = "$previousHash$transactions$timestamp$nonce".hash()

    private fun Block.calculateAndSetCurrentHash() = apply {
        currentHash = calculateHash()
    }

    private fun Block.isMined() = currentHash.startsWith(validPrefix)

    private fun Block.nonceIncrement() = copy(nonce = nonce + 1)

    override fun constructBlock(tx: Transaction): Block {
        if (!ignoreLog) log.constructBlock(isHealthy, index)
        return Block(previousHash = StringBuilder(lastAddedIntoChainBlockHash).toString(), generatedBy = this.id)
            .apply { addTransaction(tx) }
            .calculateAndSetCurrentHash()
    }

    final override fun mineBlock(block: Block): Block? {
        if (block.isMined()) {
            if (!ignoreLog) log.blockAlreadyMined(isHealthy, index, block.currentHash)
            return null
        }
        if (!ignoreLog) log.startMining(isHealthy, index, block.currentHash)

        var minedBlock = block.copy()
        while (!minedBlock.isMined()) {
            minedBlock = minedBlock
                .nonceIncrement()
                .calculateAndSetCurrentHash()
        }

        if (!ignoreLog) log.endMining(isHealthy, index, block.currentHash)
        return minedBlock
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
        amount += 1
        return true
    }

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

    override fun isMiner(): Boolean = amount > (MAX_MONEY / 20)

    /* Verify node actions */

    /* Miner node actions */

    final override fun addBlockToChain(block: Block) {
        chain.add(block)
        lastAddedIntoChainBlockHash = StringBuilder(block.currentHash).toString()
    }

    override fun removeBlockFromChain() {
        // chain.removeLast()
        // lastAddedIntoChainBlockHash = chain.last().currentHash
    }

    override fun countBlocksCreatedByNodeInChain(nodeId: UUID): Long {
        // TODO Vadim impl run for all chain and calculate blocks count by current node
        return 0
    }

    companion object {
//        protected const val MONEY_INT: Int = 4
        protected const val MAX_MONEY: Int = 10000
        protected const val MIN_MONEY: Int = 100

        protected const val difficulty = 2
        protected val validPrefix = "0".repeat(difficulty)
    }
}
