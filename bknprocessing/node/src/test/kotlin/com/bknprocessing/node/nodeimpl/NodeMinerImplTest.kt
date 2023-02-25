package com.bknprocessing.node.nodeimpl

import com.bknprocessing.common.coroutine.CoroutineClient
import com.bknprocessing.common.coroutine.CoroutineServer
import com.bknprocessing.node.AbstractTest
import com.bknprocessing.node.dto.Block
import com.bknprocessing.node.nodeimpl.miner.NodeMinerImpl
import com.bknprocessing.node.nodeimpl.wrapper.TestedNode
import com.bknprocessing.node.nodeimpl.wrapper.TestedNodeMinerImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class NodeMinerImplTest : AbstractTest<NodeMinerImpl<Any>>(
    clazz = NodeMinerImpl::class.java,
    constructor = {
        val node = TestedNode<Any>(index = 0, isHealthy = true, networkSize = 0, createdAt = Instant.now().toEpochMilli(), client = CoroutineClient.INSTANCE, server = CoroutineServer.INSTANCE)
        NodeMinerImpl(calculateHash = node.getCalculatedHash, isMined = node.getIsMined)
    },
) {

    private lateinit var goodNodeMinerImpl: TestedNodeMinerImpl<Any>
    private lateinit var badNodeMinerImpl: TestedNodeMinerImpl<Any>
    private lateinit var allNodesMinerImpl: List<TestedNodeMinerImpl<Any>>
    private lateinit var allNodes: List<TestedNode<Any>>

    @BeforeEach
    fun setUpInner() {
        val goodNode = TestedNode<Any>(index = 0, isHealthy = true, networkSize = 0, createdAt = Instant.now().toEpochMilli(), client = CoroutineClient.INSTANCE, server = CoroutineServer.INSTANCE)
        val badNode = TestedNode<Any>(index = 1, isHealthy = false, networkSize = 0, createdAt = Instant.now().toEpochMilli(), client = CoroutineClient.INSTANCE, server = CoroutineServer.INSTANCE)

        goodNodeMinerImpl = TestedNodeMinerImpl(calculateHash = goodNode.getCalculatedHash, isMined = goodNode.getIsMined)
        badNodeMinerImpl = TestedNodeMinerImpl(calculateHash = badNode.getCalculatedHash, isMined = badNode.getIsMined)
        allNodesMinerImpl = listOf(goodNodeMinerImpl, badNodeMinerImpl)
        allNodes = listOf(goodNode, badNode)
    }

    @Test
    fun `calculate hash and set hash_correctness`() {
        allNodesMinerImpl.forEachIndexed { ind, it ->
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null)
            val newBlock = it.getCalculationAndSettingHash(someBlock.copy())

            Assertions.assertEquals(
                someBlock.previousHash,
                newBlock.previousHash,
            )
            Assertions.assertNotEquals(
                someBlock.currentHash,
                newBlock.currentHash,
            )
            Assertions.assertEquals(
                allNodes[ind].getCalculatedHash(someBlock),
                newBlock.currentHash,
            )
        }
    }

    @Test
    fun `nonce increment_correctness`() {
        allNodesMinerImpl.forEach {
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null)
            val newBlock = it.getNonceIncrementing(someBlock)

            Assertions.assertEquals(
                someBlock.previousHash,
                newBlock.previousHash,
            )
            Assertions.assertNotEquals(
                someBlock.nonce,
                newBlock.nonce,
            )
            Assertions.assertEquals(
                someBlock.nonce + 1,
                newBlock.nonce,
            )
        }
    }

    @Test
    fun `is miner_correctness`() {
        allNodesMinerImpl.forEachIndexed { ind, it ->
            if (ind == 0) {
                Assertions.assertTrue(it.isMiner(allNodes[ind].getMoney()))
            }
            if (ind == 1) {
                Assertions.assertFalse(it.isMiner(allNodes[ind].getMoney()))
            }
        }
    }

    @Test
    fun `mine block_already mined`() {
        allNodesMinerImpl.forEach {
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null, currentHash = "0000")
            Assertions.assertNull(it.mineBlock(someBlock))
        }
    }

    @Test
    fun `mine block_correctness`() {
        allNodesMinerImpl.forEachIndexed { ind, it ->
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null)
            val newBlock = it.mineBlock(someBlock)!!

            Assertions.assertNotEquals(
                someBlock.currentHash,
                newBlock.currentHash,
            )
            Assertions.assertNotEquals(
                someBlock.nonce,
                newBlock.nonce,
            )
            Assertions.assertTrue(it.isMined(newBlock))
            Assertions.assertTrue(allNodes[ind].getIsMined(newBlock))
            Assertions.assertNull(it.mineBlock(newBlock))
        }
    }

    @Test
    fun `construct block_correctness`() {
        allNodesMinerImpl.forEachIndexed { ind, it ->
            val obj = UUID.randomUUID()
            val someBlock = it.constructBlock(
                obj,
                previousHash = "",
                nodeIndex = allNodes[ind].index,
                nodeId = allNodes[ind].id,
                amount = allNodes[ind].getMoney(),
            )

            Assertions.assertEquals("", someBlock.previousHash)
            Assertions.assertEquals(1, someBlock.objs.size)
            Assertions.assertEquals(obj, someBlock.objs[0])
            Assertions.assertEquals(allNodes[ind].index, someBlock.nodeInfo!!.index)
            Assertions.assertEquals(allNodes[ind].id, someBlock.nodeInfo!!.id)
            Assertions.assertEquals(allNodes[ind].getMoney(), someBlock.nodeInfo!!.amount)
            Assertions.assertTrue(someBlock.currentHash.isNotEmpty())
        }
    }
}
