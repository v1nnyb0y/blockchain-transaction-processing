package com.bknprocessing.backend.service.blockchain

// Proof of Work
// TODO: implemenet abstract class and then implementation of each validation algorithm
class Node {

    private var blocks: MutableList<Block> = mutableListOf()
    var lastHash: String = ""

    private val difficulty = 5
    private val validPrefix = "0".repeat(difficulty)

    init {
        val genesisBlock = Block(previousHash = "0")
        genesisBlock.addTransaction(Transaction())
        add(genesisBlock)
    }

    fun add(block: Block): Block {
        val minedBlock = if (isMined(block)) block else mine(block)
        blocks.add(minedBlock)
        lastHash = minedBlock.hash
        return minedBlock
    }

    private fun isMined(block: Block): Boolean {
        return block.hash.startsWith(validPrefix)
    }

    private fun mine(block: Block): Block {
        println("Mining: $block")

        var minedBlock = block.copy()
        while (!isMined(minedBlock)) {
            minedBlock = minedBlock.copy(nonce = minedBlock.nonce + 1)
        }

        println("Mined : $minedBlock")

        return minedBlock
    }

    fun isValid(): Boolean {
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
