package com.bknprocessing.node.nodeimpl.wrapper

import com.bknprocessing.node.dto.Block
import com.bknprocessing.node.nodeimpl.miner.NodeMinerImpl

class TestedNodeMinerImpl<T>(
    calculateHash: (Block<T>) -> String,
    isMined: (Block<T>) -> Boolean,
) : NodeMinerImpl<T>(calculateHash, isMined) {

    fun getCalculationAndSettingHash(block: Block<T>) = block.calculateAndSetCurrentHash()
    fun getNonceIncrementing(block: Block<T>) = block.nonceIncrement()
}
