package com.bknprocessing.backend.functional

import com.bknprocessing.backend.models.Block
import com.bknprocessing.backend.models.Node
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class NodeFunctionalTest {

    class TestNode(index: Int, isHealthy: Boolean, createdAt: Long) : Node(index, isHealthy, createdAt) {
        fun getCreatedTime(): Long = createdAt
        fun getBlockChain(): MutableList<Block> = chain
        fun getLastAddedBlockHash(): String = lastAddedIntoChainBlockHash
    }

    private lateinit var healthyNode: TestNode
    private lateinit var unhealthyNode: TestNode

    @BeforeEach
    fun setUp() {
        val createdAt = Instant.now().toEpochMilli()
        healthyNode = TestNode(1, true, createdAt)
        unhealthyNode = TestNode(2, false, createdAt)
    }

    @Test
    fun all_nodes_created_at_same_time() {
        Assertions.assertEquals(healthyNode.getCreatedTime(), unhealthyNode.getCreatedTime())
    }

    @Test
    fun genesis_block_created_at_node_creation_time() {
        Assertions.assertEquals(healthyNode.getCreatedTime(), healthyNode.getBlockChain()[0].timestamp)
        Assertions.assertEquals(unhealthyNode.getCreatedTime(), unhealthyNode.getBlockChain()[0].timestamp)
    }

    @Test
    fun genesis_block_hash_same_for_all_nodes() {
        Assertions.assertEquals(
            healthyNode.getBlockChain()[0].currentHash,
            unhealthyNode.getBlockChain()[0].currentHash
        )
    }

    @Test
    fun last_added_block_hash_same_as_genesis_block() {
        Assertions.assertEquals(
            healthyNode.getLastAddedBlockHash(),
            healthyNode.getBlockChain()[0].currentHash
        )
        Assertions.assertEquals(
            unhealthyNode.getLastAddedBlockHash(),
            unhealthyNode.getBlockChain()[0].currentHash
        )
    }
}
