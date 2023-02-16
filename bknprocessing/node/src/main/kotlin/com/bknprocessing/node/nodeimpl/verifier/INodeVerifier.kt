package com.bknprocessing.node.nodeimpl.verifier

import com.bknprocessing.node.dto.Block

interface INodeVerifier<T> {

    fun verifyBlock(block: Block<T>, chain: MutableList<Block<T>>): Boolean
}
