package com.bknprocessing.backend.functional

import com.bknprocessing.backend.service.PoolService
import com.bknprocessing.backend.type.ValidatorAlgorithm
import com.bknprocessing.backend.util.Predefined
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
            Predefined.FUNCTIONAL_NUMBER_OF_INSTANCES,
            Predefined.FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES,
            ValidatorAlgorithm.ProofOfState
        )
    }

    @Test
    fun all_trans_are_handled() = runBlocking {
        posPoolService.run(Predefined.FUNCTIONAL_NUMBER_OF_TRANSACTIONS)
        while (!posPoolService.isFinished) {
            delay(100)
        }

        delay(500)
        Assertions.assertEquals(
            Predefined.FUNCTIONAL_NUMBER_OF_TRANSACTIONS,
            posPoolService.numberOfHandledTransactions
        )
    }

    @Test
    fun all_blocks_verification_are_handled_in_network() = runBlocking {
        posPoolService.run(Predefined.FUNCTIONAL_NUMBER_OF_TRANSACTIONS)
        while (!posPoolService.isFinished) {
            delay(100)
        }

        delay(500)
        Assertions.assertEquals(
            Predefined.FUNCTIONAL_NUMBER_OF_TRANSACTIONS * (Predefined.FUNCTIONAL_NUMBER_OF_INSTANCES - 1),
            posPoolService.numberOfHandledVerification
        )
    }

    @Test
    fun all_blocks_verification_result_are_handled_in_network() = runBlocking {
        posPoolService.run(Predefined.FUNCTIONAL_NUMBER_OF_TRANSACTIONS)
        while (!posPoolService.isFinished) {
            delay(100)
        }

        delay(500)
        Assertions.assertEquals(
            Predefined.FUNCTIONAL_NUMBER_OF_INSTANCES * (Predefined.FUNCTIONAL_NUMBER_OF_INSTANCES - 1),
            posPoolService.numberOfHandledVerificationResult
        )
    }

    @Test
    fun number_of_failed_verifications_should_less_or_equal_than_unhealthy_nodes() = runBlocking {
        posPoolService.run(Predefined.FUNCTIONAL_NUMBER_OF_TRANSACTIONS)
        while (!posPoolService.isFinished) {
            delay(100)
        }

        delay(500)
        Assertions.assertTrue(
            Predefined.FUNCTIONAL_NUMBER_OF_TRANSACTIONS -
                posPoolService.numberOfSuccessVerifiedTransactions <= Predefined.FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES
        )
    }
}
