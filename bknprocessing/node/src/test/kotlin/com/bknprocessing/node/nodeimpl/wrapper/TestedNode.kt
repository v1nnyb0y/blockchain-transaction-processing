package com.bknprocessing.node.nodeimpl.wrapper

import com.bknprocessing.common.IClient
import com.bknprocessing.common.IServer
import com.bknprocessing.node.dto.Block
import com.bknprocessing.node.nodeimpl.Node
import java.util.UUID

class TestedNode<T>(
    index: Int,
    isHealthy: Boolean,
    networkSize: Int,
    createdAt: Long,

    client: IClient,
    server: IServer,
) : Node<T>(
    index = index,
    isHealthy = isHealthy,
    networkSize = networkSize,
    createdAt = createdAt,
    client = client,
    server = server,
) {
    fun getBlockChain() = chain
    fun getMoney() = amount
    fun getNodeMiner() = miner
    fun getLastHashInChain() = lastBlockHashInChain
    fun getIsNodeMiner() = isMiner
    fun getNetworkInfo() = nodeInfos
    fun getSmAcceptNewBlock(block: Block<T>) = handleAcceptNewBlock(block)
    fun getSmSyncBlocks(block: Block<T>) = handleRemoveUnhealthyBlocks(block)
    fun getSmSetNewMiner(minerId: UUID) = handleSetNewMiner(minerId)
    fun getFindMapOfNodesInfo() = findMapOfNodes()

    val getCalculatedHash = { block: Block<T> -> calculateHash(block) }
    val getIsMined = { block: Block<T> -> isMined(block) }
}
