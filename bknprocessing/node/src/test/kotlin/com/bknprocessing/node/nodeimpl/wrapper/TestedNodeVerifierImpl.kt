package com.bknprocessing.node.nodeimpl.wrapper

import com.bknprocessing.node.dto.Block
import com.bknprocessing.node.nodeimpl.verifier.NodeVerifierImpl

class TestedNodeVerifierImpl<T>(
    calculateHash: (Block<T>) -> String,
    isMined: (Block<T>) -> Boolean,
) : NodeVerifierImpl<T>(calculateHash, isMined) {

    fun getIsChainValidation(chain: List<Block<T>>) = isChainValid(chain)
}
