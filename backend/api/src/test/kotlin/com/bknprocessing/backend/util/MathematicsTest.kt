package com.bknprocessing.backend.util

import com.bknprocessing.backend.service.PoolService
import com.bknprocessing.backend.utils.determineNextIterationMinerIndex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MathematicsTest {

    @Test
    fun `should return minus one`() {
        val nodesMap = emptyMap<Int, PoolService.VerifiedBlocksAndAmountInfo>()
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
        val actual = determineNextIterationMinerIndex(nodesMap = emptyMap())
        assertEquals(expected, actual)
    }

    //    @Test TODO Vitali
    fun `should return one`() {
        val expected = 0
        val nodesMap = emptyMap<Int, PoolService.VerifiedBlocksAndAmountInfo>()
        val actual = determineNextIterationMinerIndex(nodesMap = nodesMap)
        assertEquals(expected, actual)
    }
}
