package com.bknprocessing.backend.functional

import com.bknprocessing.backend.models.Block
import com.bknprocessing.backend.models.Node
import com.bknprocessing.backend.models.Transaction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class NodeFunctionalTest {

    class TestNode(index: Int, isHealthy: Boolean, createdAt: Long) : Node(index, isHealthy, createdAt) {
        fun getCreatedTime(): Long = createdAt
        fun getBlockChain(): MutableList<Block> = chain
        fun getLastAddedBlockHash(): String = lastAddedIntoChainBlockHash
        fun isBlockChainValid() = isChainValid()

        fun clearChain() {
            chain = mutableListOf()
        }
    }

    private lateinit var healthyNode: TestNode
    private lateinit var unhealthyNode: TestNode

    private lateinit var healthyBlock: Block
    private lateinit var unhealthyBlock: Block

    @BeforeEach
    fun setUp() {
        val createdAt = Instant.now().toEpochMilli()
        healthyNode = TestNode(1, true, createdAt)
        unhealthyNode = TestNode(2, false, createdAt)

        healthyBlock = healthyNode.constructBlock(Transaction())
        unhealthyBlock = unhealthyNode.constructBlock(Transaction())
    }

    @Test
    fun all_nodes_created_at_same_time() {
        Assertions.assertEquals(healthyNode.getCreatedTime(), unhealthyNode.getCreatedTime())
    }

    @Test
    fun genesis_block_is_created() {
        Assertions.assertEquals(1, healthyNode.getBlockChain().size)
        Assertions.assertEquals(1, unhealthyNode.getBlockChain().size)
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

    @Test
    fun construct_block_with_correct_previous_hash_and_current_hash() {
        Assertions.assertEquals(healthyNode.getLastAddedBlockHash(), healthyBlock.previousHash)
        Assertions.assertTrue(healthyBlock.currentHash.isNotBlank())

        Assertions.assertEquals(unhealthyNode.getLastAddedBlockHash(), unhealthyBlock.previousHash)
        Assertions.assertTrue(unhealthyBlock.currentHash.isNotBlank())
    }

    @Test
    fun mine_block_change_hash_and_nonce() {
        val minedHealthyBlock = healthyNode.mineBlock(healthyBlock)
        Assertions.assertNotEquals(healthyBlock.currentHash, minedHealthyBlock!!.currentHash)
        Assertions.assertNotEquals(healthyBlock.nonce, minedHealthyBlock.nonce)

        val minedUnhealthyBlock = unhealthyNode.mineBlock(unhealthyBlock)
        Assertions.assertNotEquals(unhealthyBlock.currentHash, minedUnhealthyBlock!!.currentHash)
        Assertions.assertNotEquals(unhealthyBlock.nonce, minedUnhealthyBlock.nonce)
    }

    @Test
    fun mine_block_should_return_false_if_mined() {
        val minedHealthyBlock = healthyNode.mineBlock(healthyBlock)
        val secondMinedHealthyBlock = healthyNode.mineBlock(minedHealthyBlock!!)
        Assertions.assertNull(secondMinedHealthyBlock)

        val minedUnhealthyBlock = unhealthyNode.mineBlock(unhealthyBlock)
        val secondMinedUnhealthyBlock = unhealthyNode.mineBlock(minedUnhealthyBlock!!)
        Assertions.assertNull(secondMinedUnhealthyBlock)
    }

    @Test
    fun add_block_to_chain_should_change_last_hash() {
        val lastHealthyHash = healthyNode.getLastAddedBlockHash()
        val lastUnhealthyHash = unhealthyNode.getLastAddedBlockHash()

        healthyNode.addBlockToChain(healthyBlock)
        Assertions.assertEquals(2, healthyNode.getBlockChain().size)
        Assertions.assertNotEquals(lastHealthyHash, healthyNode.getLastAddedBlockHash())
        Assertions.assertEquals(healthyBlock.currentHash, healthyNode.getLastAddedBlockHash())

        unhealthyNode.addBlockToChain(unhealthyBlock)
        Assertions.assertEquals(2, unhealthyNode.getBlockChain().size)
        Assertions.assertNotEquals(lastUnhealthyHash, unhealthyNode.getLastAddedBlockHash())
        Assertions.assertEquals(unhealthyBlock.currentHash, unhealthyNode.getLastAddedBlockHash())
    }

    @Test
    fun is_empty_chain_valid_successful() {
        healthyNode.clearChain()
        Assertions.assertTrue(healthyNode.isBlockChainValid())

        unhealthyNode.clearChain()
        Assertions.assertTrue(unhealthyNode.isBlockChainValid())
    }

    @Test
    fun is_one_capacity_chain_valid_successful() {
        Assertions.assertTrue(healthyNode.isBlockChainValid())
        Assertions.assertTrue(unhealthyNode.isBlockChainValid())
    }

    @Test
    fun is_multiple_capacity_chain_valid_successful() {
        val minedHealthyBlock = healthyNode.mineBlock(healthyBlock)
        val minedUnhealthyBlock = unhealthyNode.mineBlock(unhealthyBlock)

        healthyNode.addBlockToChain(minedHealthyBlock!!)
        unhealthyNode.addBlockToChain(minedUnhealthyBlock!!)

        Assertions.assertTrue(healthyNode.isBlockChainValid())
        Assertions.assertTrue(unhealthyNode.isBlockChainValid())
    }

    @Test
    fun is_chain_invalid_cause_not_mined() {
        healthyNode.addBlockToChain(healthyBlock)
        unhealthyNode.addBlockToChain(unhealthyBlock)

        Assertions.assertFalse(healthyNode.isBlockChainValid())
        Assertions.assertFalse(unhealthyNode.isBlockChainValid())
    }

    @Test
    fun is_chain_invalid_cause_previous_block() {
        healthyBlock = Block(previousHash = "123", currentHash = "123")
        unhealthyBlock = Block(previousHash = "123", currentHash = "123")

        healthyNode.addBlockToChain(healthyBlock)
        unhealthyNode.addBlockToChain(unhealthyBlock)

        Assertions.assertFalse(healthyNode.isBlockChainValid())
        Assertions.assertFalse(unhealthyNode.isBlockChainValid())
    }

    @Test
    fun is_chain_invalid_cause_current_hash_empty() {
        healthyBlock = Block(previousHash = "123")
        unhealthyBlock = Block(previousHash = "123")

        healthyNode.addBlockToChain(healthyBlock)
        unhealthyNode.addBlockToChain(unhealthyBlock)

        Assertions.assertFalse(healthyNode.isBlockChainValid())
        Assertions.assertFalse(unhealthyNode.isBlockChainValid())
    }

    @Test
    fun verify_block_change_last_hash_and_size_of_chain() {
        healthyNode.clearChain()
        unhealthyNode.clearChain()

        val minedHealthyBlock = healthyNode.mineBlock(healthyBlock)
        val minedUnhealthyBlock = unhealthyNode.mineBlock(unhealthyBlock)

        val resultHealthy = healthyNode.verifyBlock(minedHealthyBlock!!)
        val resultUnhealthy = unhealthyNode.verifyBlock(minedUnhealthyBlock!!)

        Assertions.assertEquals(1, healthyNode.getBlockChain().size)
        Assertions.assertEquals(1, unhealthyNode.getBlockChain().size)

        Assertions.assertEquals(minedHealthyBlock.currentHash, healthyNode.getLastAddedBlockHash())
        Assertions.assertEquals(minedUnhealthyBlock.currentHash, unhealthyNode.getLastAddedBlockHash())

        Assertions.assertTrue(resultHealthy)
        Assertions.assertTrue(resultUnhealthy)
    }

    @Test
    fun verify_block_wrong_not_change_anything() {
        healthyBlock = Block(previousHash = "123")
        unhealthyBlock = Block(previousHash = "123")

        val resultHealthy = healthyNode.verifyBlock(healthyBlock)
        val resultUnhealthy = healthyNode.verifyBlock(unhealthyBlock)

        Assertions.assertEquals(1, healthyNode.getBlockChain().size)
        Assertions.assertEquals(1, unhealthyNode.getBlockChain().size)

        Assertions.assertFalse(resultHealthy)
        Assertions.assertFalse(resultUnhealthy)
    }
}
