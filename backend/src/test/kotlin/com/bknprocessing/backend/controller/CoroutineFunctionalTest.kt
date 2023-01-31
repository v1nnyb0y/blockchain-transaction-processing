package com.bknprocessing.backend.controller

import com.bknprocessing.backend.service.PoolService
import com.bknprocessing.backend.type.ValidatorAlgorithm
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CoroutineFunctionalTest {

    private lateinit var posPoolService: PoolService

    @BeforeEach
    fun setup() {
        posPoolService = PoolService(
            10,
            2,
            ValidatorAlgorithm.ProofOfState
        )
    }

    @Test
    fun all_trans_are_handled() = runBlocking {
        val numberOfTransactions = 100

        posPoolService.run(numberOfTransactions)
        delay(5000)

        var numberOfHandledTransactions = 0
        for (node in posPoolService.nodes) {
            numberOfHandledTransactions += node.countOfHandledTrans
        }
        Assertions.assertEquals(numberOfTransactions, numberOfHandledTransactions)
    }
}
