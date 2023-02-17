package com.bknprocessing.backend.service

import com.bknprocessing.backend.type.ValidatorAlgorithm
import com.bknprocessing.backend.util.Predefined
import com.bknprocessing.backend.util.Predefined.Companion.FUNCTIONAL_NUMBER_OF_TRANSACTIONS
import com.bknprocessing.backend.util.Predefined.Companion.FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PoolServiceTest {

    class TestPoolService(nodesCount: Int, unhealthyNodesCount: Int, validatorAlgorithm: ValidatorAlgorithm) :
        PoolService(nodesCount, unhealthyNodesCount, validatorAlgorithm) {
        fun getInstances() = nodes
        fun getNumberOfHandledTrans() = numberOfHandledTransactions
        fun getNumberOfHandledVerifies() = numberOfHandledVerification
        fun getNumberOfHandledVerifiesResult() = numberOfHandledVerificationResult
        fun getNumberOfSuccessVerifications() = numberOfSuccessVerifiedTransactions
    }

    private lateinit var posPoolService: TestPoolService

    @BeforeEach
    fun setup() {
        posPoolService = TestPoolService(
            Predefined.FUNCTIONAL_NUMBER_OF_INSTANCES,
            FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES,
            ValidatorAlgorithm.ProofOfState,
        )
    }

    @Test
    fun created_correct_number_of_nodes() {
        var numberOfHealthy = 0
        var numberOfUnhealthy = 0
        for (node in posPoolService.getInstances()) {
            if (node.isHealthy) {
                numberOfHealthy += 1
            } else {
                numberOfUnhealthy += 1
            }
        }

        Assertions.assertEquals(
            numberOfHealthy,
            Predefined.FUNCTIONAL_NUMBER_OF_INSTANCES - FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES,
        )
        Assertions.assertEquals(numberOfUnhealthy, FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES)
    }

    @Test
    fun all_trans_are_handled() = runBlocking {
        posPoolService.run(FUNCTIONAL_NUMBER_OF_TRANSACTIONS)
        while (!posPoolService.isFinished) {
            delay(100)
        }

        delay(500)
        Assertions.assertEquals(
            FUNCTIONAL_NUMBER_OF_TRANSACTIONS,
            posPoolService.getNumberOfHandledTrans(),
        )
    }

    @Test
    fun all_blocks_verification_are_handled_in_network() = runBlocking {
        posPoolService.run(FUNCTIONAL_NUMBER_OF_TRANSACTIONS)
        while (!posPoolService.isFinished) {
            delay(100)
        }

        delay(500)
        Assertions.assertEquals(
            FUNCTIONAL_NUMBER_OF_TRANSACTIONS * (Predefined.FUNCTIONAL_NUMBER_OF_INSTANCES - 1),
            posPoolService.getNumberOfHandledVerifies(),
        )
    }

    @Test
    fun all_blocks_verification_result_are_handled_in_network() = runBlocking {
        posPoolService.run(FUNCTIONAL_NUMBER_OF_TRANSACTIONS)
        while (!posPoolService.isFinished) {
            delay(100)
        }

        delay(500)
        Assertions.assertEquals(
            Predefined.FUNCTIONAL_NUMBER_OF_INSTANCES * (Predefined.FUNCTIONAL_NUMBER_OF_INSTANCES - 1),
            posPoolService.getNumberOfHandledVerifiesResult(),
        )
    }

    @Test // should be failed, cause isHealthy functionality is not implemented
    fun number_of_failed_verifications_should_less_or_equal_than_unhealthy_nodes() = runBlocking {
        posPoolService.run(FUNCTIONAL_NUMBER_OF_TRANSACTIONS)
        while (!posPoolService.isFinished) {
            delay(100)
        }

        delay(500)
        Assertions.assertTrue(
            FUNCTIONAL_NUMBER_OF_TRANSACTIONS - posPoolService.getNumberOfSuccessVerifications() <= FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES,
        )
    }

    @Test
    fun all_nodes_has_unique_index() {
        val indexes: MutableMap<Int, Boolean> = HashMap()
        var result = true
        for (node in posPoolService.getInstances()) {
            if (indexes.containsKey(node.index)) {
                result = false
                break
            }

            indexes[node.index] = true
        }

        Assertions.assertTrue(result)
    }

    @Test
    fun at_least_one_miner_and_one_verifier() {
        var oneMiner = false
        var oneVerifier = false

        for (node in posPoolService.getInstances()) {
            oneMiner = oneMiner || node.isMiner()
            oneVerifier = oneVerifier || !node.isMiner()

            if (oneMiner && oneVerifier) break
        }

        Assertions.assertTrue(oneMiner)
        Assertions.assertTrue(oneVerifier)
    }
}
