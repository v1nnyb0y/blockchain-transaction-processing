package com.bknprocessing.node

import com.bknprocessing.node.dto.Block

interface INodeVerifier {

    fun verifyBlock(block: Block): Boolean
}
