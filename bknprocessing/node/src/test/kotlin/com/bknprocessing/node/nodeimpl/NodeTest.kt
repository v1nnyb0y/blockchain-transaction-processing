package com.bknprocessing.node.nodeimpl

import com.bknprocessing.common.coroutine.CoroutineClient
import com.bknprocessing.common.coroutine.CoroutineServer
import com.bknprocessing.node.AbstractTest
import com.bknprocessing.node.dto.Block
import com.bknprocessing.node.dto.NodeInfoSubBlock
import com.bknprocessing.node.nodeimpl.wrapper.TestedNode
import com.bknprocessing.node.utils.hash
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

class NodeTest : AbstractTest<Node<Any>>(
    clazz = Node::class.java,
    constructor = { Node(index = 0, isHealthy = true, networkSize = 1, createdAt = Instant.now().toEpochMilli(), client = CoroutineClient.INSTANCE, server = CoroutineServer.INSTANCE) },
) {

    private lateinit var goodNode: TestedNode<Any>
    private lateinit var badNode: TestedNode<Any>
    private lateinit var allNodes: List<TestedNode<Any>>

    @BeforeEach
    fun setUpInner() {
        val createdAt = Instant.now().toEpochMilli()
        goodNode = TestedNode(index = 0, isHealthy = true, networkSize = 2, createdAt = createdAt, client = CoroutineClient.INSTANCE, server = CoroutineServer.INSTANCE)
        badNode = TestedNode(index = 1, isHealthy = false, networkSize = 2, createdAt = createdAt, client = CoroutineClient.INSTANCE, server = CoroutineServer.INSTANCE)
        allNodes = listOf(goodNode, badNode)
    }

    @Test
    fun `init nodes_correctness`() {
        allNodes.forEach {
            val chain = it.getBlockChain()
            val amount = it.getMoney()

            Assertions.assertEquals(1, chain.size)
            Assertions.assertEquals(null, chain[0].nodeInfo)
            Assertions.assertEquals("", chain[0].previousHash)
            Assertions.assertEquals(1, it.getNetworkInfo().keys.size)

            val networkAmount = it.getNetworkInfo()[it.id]!!
            Assertions.assertEquals(amount, networkAmount)

            if (it.index == 0) {
                Assertions.assertTrue(amount > 0)
            }
            if (it.index == 1) {
                Assertions.assertEquals(0, amount)
            }
        }
    }

    @Test
    fun `calculate hash_correctness`() {
        allNodes.forEach {
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null)
            val previousHash = someBlock.previousHash
            val objs = someBlock.objs
            val timestamp = someBlock.timestamp
            val nonce = someBlock.nonce

            Assertions.assertTrue(it.getCalculatedHash(someBlock).isNotBlank())
            Assertions.assertEquals(
                //"$previousHash$objs$timestamp$nonce".hash(),
                "$previousHash$timestamp$nonce".hash(),
                it.getCalculatedHash(someBlock),
            )
        }
    }

    @Test
    fun `is mined block_correctness`() {
        allNodes.forEach {
            val someBlockTrue = Block<Any>(previousHash = "", nodeInfo = null, currentHash = "00123")
            val someBlockFalse = Block<Any>(previousHash = "", nodeInfo = null, currentHash = "12300123")

            Assertions.assertTrue(it.getIsMined(someBlockTrue))
            Assertions.assertFalse(it.getIsMined(someBlockFalse))
        }
    }

    @Test
    fun `last hash in chain_correctness`() {
        allNodes.forEach {
            val chain = it.getBlockChain()
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null)
                .apply { currentHash = "justtesthash" }

            chain.add(someBlock)
            Assertions.assertEquals(
                "justtesthash",
                it.getLastHashInChain(),
            )
        }
    }

    @Test
    fun `accept new block_exists`() {
        allNodes.forEach {
            val chain = it.getBlockChain()
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null)
                .apply { currentHash = "justtesthash" }

            chain.add(someBlock)
            it.getSmAcceptNewBlock(someBlock)
            Assertions.assertEquals(
                2,
                chain.size,
            )
            Assertions.assertEquals(someBlock.currentHash, it.getLastHashInChain())
        }
    }

    @Test
    fun `accept new block_accept`() {
        allNodes.forEach {
            val chain = it.getBlockChain()
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null)
                .apply { currentHash = "justtesthash" }

            it.getSmAcceptNewBlock(someBlock)
            Assertions.assertEquals(
                2,
                chain.size,
            )
            Assertions.assertEquals(
                someBlock.currentHash,
                it.getLastHashInChain(),
            )
        }
    }

    @Test
    fun `sync blocks_without remove`() {
        allNodes.forEach {
            val chain = it.getBlockChain()
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null)
                .apply { currentHash = "justtesthash" }

            chain.add(someBlock)
            it.getSmSyncBlocks(someBlock)
            Assertions.assertEquals(2, chain.size)
            Assertions.assertEquals(someBlock.currentHash, it.getLastHashInChain())
        }
    }

    @Test
    fun `sync blocks_with remove`() {
        allNodes.forEach {
            val chain = it.getBlockChain()
            val someBlock = Block<Any>(previousHash = "", nodeInfo = null)
                .apply { currentHash = "justtesthash" }
            val anotherSomeBlock = Block<Any>(previousHash = "", nodeInfo = null)
                .apply { currentHash = "justtesthashanother" }

            chain.add(someBlock)
            chain.add(anotherSomeBlock)
            it.getSmSyncBlocks(someBlock)

            Assertions.assertEquals(2, chain.size)
            Assertions.assertEquals(someBlock.currentHash, it.getLastHashInChain())
        }
    }

    @Test
    fun `find map of nodes_correctness`() {
        allNodes.forEach {
            val chain = it.getBlockChain()
            val someBlock = Block<Any>(
                previousHash = "",
                nodeInfo = NodeInfoSubBlock(
                    amount = goodNode.getMoney() - 100,
                    id = goodNode.id,
                    index = goodNode.index,
                ),
            )
            val anotherBlock = Block<Any>(
                previousHash = "",
                nodeInfo = NodeInfoSubBlock(
                    amount = goodNode.getMoney(),
                    id = goodNode.id,
                    index = goodNode.index,
                ),
                timestamp = Instant.now().toEpochMilli() + 10,
            )
            val someAnotherBlock = Block<Any>(
                previousHash = "",
                nodeInfo = NodeInfoSubBlock(
                    amount = badNode.getMoney(),
                    id = badNode.id,
                    index = badNode.index,
                ),
                timestamp = Instant.now().toEpochMilli() + 12,
            )

            chain.add(someBlock)
            chain.add(anotherBlock)
            chain.add(someAnotherBlock)

            val map = it.getFindMapOfNodesInfo()

            Assertions.assertEquals(2, map.keys.size)
            Assertions.assertTrue(map.containsKey(goodNode.id))
            Assertions.assertTrue(map.containsKey(badNode.id))
            Assertions.assertEquals(goodNode.getMoney(), map[goodNode.id]!!.amount)
            Assertions.assertEquals(2, map[goodNode.id]!!.blocksCount)
            Assertions.assertEquals(badNode.getMoney(), map[badNode.id]!!.amount)
            Assertions.assertEquals(1, map[badNode.id]!!.blocksCount)
        }
    }

    @Test
    fun `set new miner_change miner`() {
        Assertions.assertTrue(goodNode.getIsNodeMiner())

        goodNode.getSmSetNewMiner(badNode.id)
        Assertions.assertFalse(goodNode.getIsNodeMiner())

        badNode.getSmSetNewMiner(badNode.id)
        Assertions.assertTrue(badNode.getIsNodeMiner())
    }

    @Test
    fun `set new miner_not change miner`() {
        Assertions.assertTrue(goodNode.getIsNodeMiner())

        goodNode.getSmSetNewMiner(goodNode.id)
        Assertions.assertTrue(goodNode.getIsNodeMiner())

        badNode.getSmSetNewMiner(goodNode.id)
        Assertions.assertFalse(badNode.getIsNodeMiner())
    }
}
