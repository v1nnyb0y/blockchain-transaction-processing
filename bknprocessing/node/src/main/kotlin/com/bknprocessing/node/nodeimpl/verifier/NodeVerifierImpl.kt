package com.bknprocessing.node.nodeimpl.verifier

import com.bknprocessing.node.dto.Block

class NodeVerifierImpl<T>(
    val calculateHash: (Block<T>) -> String,
    val isMined: (Block<T>) -> Boolean,
) : INodeVerifier<T> {

    protected fun isChainValid(chain: List<Block<T>>): Boolean {
        when {
            chain.isEmpty() -> return true
            chain.size == 1 -> return chain[0].currentHash == calculateHash(chain[0])
            else -> {
                for (i in 1 until chain.size) {
                    val previousBlock = chain[i - 1]
                    val currentBlock = chain[i]

                    when {
                        currentBlock.currentHash != calculateHash(currentBlock) -> return false
                        currentBlock.previousHash != calculateHash(previousBlock) -> return false
                        !(isMined(previousBlock) && isMined(currentBlock)) -> return false
                    }
                }
                return true
            }
        }
    }

    override fun verifyBlock(block: Block<T>, chain: MutableList<Block<T>>): Boolean {
        chain.add(block)

        if (!isChainValid(chain)) {
            chain.removeLast()
            return false
        }

        return true
    }
}
