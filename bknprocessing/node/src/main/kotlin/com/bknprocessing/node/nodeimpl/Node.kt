package com.bknprocessing.node.nodeimpl

import com.bknprocessing.node.dto.Block
import com.bknprocessing.node.nodeimpl.miner.INodeMiner
import com.bknprocessing.node.nodeimpl.miner.NodeMinerImpl
import com.bknprocessing.node.nodeimpl.verifier.INodeVerifier
import com.bknprocessing.node.nodeimpl.verifier.NodeVerifierImpl
import com.bknprocessing.node.utils.hash
import java.util.UUID
import kotlin.random.Random

class Node<T>(
    val id: UUID = UUID.randomUUID(),
    val index: Int,
    val isHealthy: Boolean,
    createdAt: Long
) : INode {

    protected var chain: MutableList<Block<T>> = mutableListOf()
    protected var amount: Int = Random.nextInt(MIN_MONEY, MAX_MONEY)

    private val calculateHash = { block: Block<T> -> with(block) { "$previousHash$objs$timestamp$nonce".hash() }}
    private val isMined = { block: Block<T> -> with(block) { currentHash.startsWith(validPrefix) }}

    protected val miner: INodeMiner<T> = NodeMinerImpl(calculateHash, isMined)
    protected val verifier: INodeVerifier<T> = NodeVerifierImpl(calculateHash, isMined)

    init {
        if (index == 0) amount = MAX_MONEY
        if (index > 0) amount = 0

        // generate genesis block
        val block: Block<T> = miner.mineBlock(
            Block(
                previousHash = "",
                timestamp = createdAt,
                generatedBy = null
            )
        )!!
        chain.add(block)
    }

    companion object {
        const val MAX_MONEY: Int = 10000
        const val MIN_MONEY: Int = 100

        protected const val difficulty = 2
        protected val validPrefix = "0".repeat(difficulty)
    }
}