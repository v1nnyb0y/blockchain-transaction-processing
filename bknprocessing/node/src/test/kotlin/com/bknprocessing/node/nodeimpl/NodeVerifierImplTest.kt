package com.bknprocessing.node.nodeimpl

import com.bknprocessing.common.coroutine.CoroutineClient
import com.bknprocessing.common.coroutine.CoroutineServer
import com.bknprocessing.node.AbstractTest
import com.bknprocessing.node.dto.Block
import com.bknprocessing.node.nodeimpl.verifier.NodeVerifierImpl
import com.bknprocessing.node.nodeimpl.wrapper.TestedNode
import com.bknprocessing.node.nodeimpl.wrapper.TestedNodeVerifierImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class NodeVerifierImplTest : AbstractTest<NodeVerifierImpl<Any>>(
    clazz = NodeVerifierImpl::class.java,
    constructor = {
        val node = TestedNode<Any>(index = 0, isHealthy = true, networkSize = 0, createdAt = Instant.now().toEpochMilli(), client = CoroutineClient.INSTANCE, server = CoroutineServer.INSTANCE)
        NodeVerifierImpl(calculateHash = node.getCalculatedHash, isMined = node.getIsMined)
    },
) {

    private lateinit var goodNodeVerifierImpl: TestedNodeVerifierImpl<Any>
    private lateinit var badNodeVerifierImpl: TestedNodeVerifierImpl<Any>
    private lateinit var allNodesVerifierImpl: List<TestedNodeVerifierImpl<Any>>
    private lateinit var allNodes: List<TestedNode<Any>>

    @BeforeEach
    fun setUpInner() {
        val goodNode = TestedNode<Any>(index = 0, isHealthy = true, networkSize = 0, createdAt = Instant.now().toEpochMilli(), client = CoroutineClient.INSTANCE, server = CoroutineServer.INSTANCE)
        val badNode = TestedNode<Any>(index = 1, isHealthy = false, networkSize = 0, createdAt = Instant.now().toEpochMilli(), client = CoroutineClient.INSTANCE, server = CoroutineServer.INSTANCE)

        goodNodeVerifierImpl = TestedNodeVerifierImpl(calculateHash = goodNode.getCalculatedHash, isMined = goodNode.getIsMined)
        badNodeVerifierImpl = TestedNodeVerifierImpl(calculateHash = badNode.getCalculatedHash, isMined = badNode.getIsMined)
        allNodesVerifierImpl = listOf(goodNodeVerifierImpl, badNodeVerifierImpl)
        allNodes = listOf(goodNode, badNode)
    }

    @Test
    fun `is chain valid_chain empty`() {
        allNodesVerifierImpl.forEach {
            Assertions.assertTrue(it.getIsChainValidation(listOf()))
        }
    }

    @Test
    fun `is chain valid_single size_correctness`() {
        allNodesVerifierImpl.forEachIndexed { ind, it ->
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null, processingTime = 0).apply {
                currentHash = allNodes[ind].getCalculatedHash(this)
            }

            Assertions.assertTrue(it.getIsChainValidation(listOf(someBlock)))
        }
    }

    @Test
    fun `is chain valid_single size_failed`() {
        allNodesVerifierImpl.forEachIndexed { ind, it ->
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null, processingTime = 0)

            Assertions.assertFalse(it.getIsChainValidation(listOf(someBlock)))
        }
    }

    @Test
    fun `is chain valid_multiple size_correctness`() {
        allNodesVerifierImpl.forEachIndexed { ind, it ->
            var someBlock = Block<Any>(previousHash = "", nodeInfo = null, processingTime = 0).apply {
                currentHash = allNodes[ind].getCalculatedHash(this)
            }
            someBlock = allNodes[ind].getNodeMiner().mineBlock(someBlock)!!

            var anotherSomeBlock =
                Block<Any>(previousHash = someBlock.currentHash, nodeInfo = null, processingTime = 0).apply {
                    currentHash = allNodes[ind].getCalculatedHash(this)
                }
            anotherSomeBlock = allNodes[ind].getNodeMiner().mineBlock(anotherSomeBlock)!!

            Assertions.assertTrue(it.getIsChainValidation(listOf(someBlock, anotherSomeBlock)))
        }
    }

    @Test
    fun `is chain valid_multiple size_failed_is mined`() {
        allNodesVerifierImpl.forEachIndexed { ind, it ->
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null, processingTime = 0).apply {
                currentHash = allNodes[ind].getCalculatedHash(this)
            }
            val anotherSomeBlock =
                Block<Any>(previousHash = someBlock.currentHash, nodeInfo = null, processingTime = 0).apply {
                    currentHash = allNodes[ind].getCalculatedHash(this)
                }

            Assertions.assertFalse(it.getIsChainValidation(listOf(someBlock, anotherSomeBlock)))
        }
    }

    @Test
    fun `is chain valid_multiple size_failed_last block`() {
        allNodesVerifierImpl.forEachIndexed { ind, it ->
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null, processingTime = 0).apply {
                currentHash = allNodes[ind].getCalculatedHash(this)
            }
            val anotherSomeBlock = Block<Any>(previousHash = someBlock.currentHash, nodeInfo = null, processingTime = 0)

            Assertions.assertFalse(it.getIsChainValidation(listOf(someBlock, anotherSomeBlock)))
        }
    }

    @Test
    fun `is chain valid_multiple size_failed_first block`() {
        allNodesVerifierImpl.forEachIndexed { ind, it ->
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null, processingTime = 0)
            val anotherSomeBlock =
                Block<Any>(previousHash = someBlock.currentHash, nodeInfo = null, processingTime = 0).apply {
                    currentHash = allNodes[ind].getCalculatedHash(this)
                }

            Assertions.assertFalse(it.getIsChainValidation(listOf(someBlock, anotherSomeBlock)))
        }
    }

    @Test
    fun `is chain valid_multiple size_failed_previous hash`() {
        allNodesVerifierImpl.forEachIndexed { ind, it ->
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null, processingTime = 0).apply {
                currentHash = allNodes[ind].getCalculatedHash(this)
            }
            val anotherSomeBlock = Block<Any>(previousHash = "", nodeInfo = null, processingTime = 0).apply {
                currentHash = allNodes[ind].getCalculatedHash(this)
            }

            Assertions.assertFalse(it.getIsChainValidation(listOf(someBlock, anotherSomeBlock)))
        }
    }

    @Test
    fun `verify block_correctness`() {
        allNodesVerifierImpl.forEachIndexed { ind, it ->
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null, processingTime = 0).apply {
                currentHash = allNodes[ind].getCalculatedHash(this)
            }
            val chain: MutableList<Block<Any>> = mutableListOf()

            Assertions.assertTrue(it.verifyBlock(someBlock, chain))
            Assertions.assertEquals(1, chain.size)
            Assertions.assertEquals(someBlock, chain[0])
        }
    }

    @Test
    fun `verify block_failed`() {
        allNodesVerifierImpl.forEachIndexed { ind, it ->
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null, processingTime = 0)
            val chain: MutableList<Block<Any>> = mutableListOf()

            Assertions.assertFalse(it.verifyBlock(someBlock, chain))
            Assertions.assertEquals(0, chain.size)
        }
    }
}
