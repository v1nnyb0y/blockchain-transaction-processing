package com.bknprocessing.node.utils

import com.bknprocessing.node.nodeimpl.Node
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class MathematicsTest {

    @Test
    fun `should return minus one`() {
        val nodesMap = emptyMap<UUID, Node<Any>.VerifiedBlocksAndAmountInfo>()
        assertThrows<IllegalStateException> {
            determineNextIterationMinerIndex(nodesMap = nodesMap)
        }
    }

    //    @Test TODO
    fun `should return zero`() {
        val expected = 0
//        val nodesMap = mapOf<Int, PoolService.VerifiedBlocksAndAmountInfo>{
//            0 to PoolService.VerifiedBlocksAndAmountInfo
//        }
        val actual = determineNextIterationMinerIndex<Any>(nodesMap = emptyMap())
        Assertions.assertEquals(expected, actual)
    }

    //    @Test TODO Vitalis
    fun `should return one`() {
        val expected = 0
        val nodesMap = emptyMap<UUID, Node<Any>.VerifiedBlocksAndAmountInfo>()
        val actual = determineNextIterationMinerIndex(nodesMap = nodesMap)
        Assertions.assertEquals(expected, actual)
    }
}
