package com.bknprocessing.app.service

import com.bknprocessing.app.data.Transaction
import com.bknprocessing.app.service.worker.IWorker
import com.bknprocessing.app.service.worker.KafkaWorker
import com.bknprocessing.app.service.wrapper.TestedPoolService
import com.bknprocessing.app.service.wrapper.uppper.ITestedUpper
import com.bknprocessing.app.service.wrapper.uppper.TestedKafkaUpper
import com.bknprocessing.app.utils.Predefined.Companion.FUNCTIONAL_NUMBER_OF_INSTANCES
import com.bknprocessing.app.utils.Predefined.Companion.FUNCTIONAL_NUMBER_OF_TRANSACTIONS
import com.bknprocessing.app.utils.Predefined.Companion.FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES
import com.bknprocessing.node.nodeimpl.Node
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/*abstract*/ class PoolServiceTest {

    companion object {
        private lateinit var poolService: TestedPoolService
        private lateinit var upper: ITestedUpper<Transaction>
        private lateinit var worker: IWorker<Transaction>

        @JvmStatic
        @BeforeAll
        fun setUp(): Unit = runBlocking {
            upper = TestedKafkaUpper()
            worker = KafkaWorker() // TODO Vitalii please look
            // I want to share that constructors from super class (for example: CoroutinePoolService)
            // and this class should be abstract

            poolService = TestedPoolService(worker, upper).apply {
                this.run(
                    FUNCTIONAL_NUMBER_OF_INSTANCES,
                    FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES,
                    FUNCTIONAL_NUMBER_OF_TRANSACTIONS,
                )
            }
        }
    }

    @Test
    fun `created correct number of nodes`() {
        Assertions.assertEquals(FUNCTIONAL_NUMBER_OF_INSTANCES, upper.getListNodes().size)
    }

    @Test
    fun `create correct number of healthy and unhealthy nodes`() {
        val countOfHealthy = upper.getListNodes().count { it.isHealthy }
        val countOfUnhealthy = upper.getListNodes().count { !it.isHealthy }

        Assertions.assertEquals(FUNCTIONAL_NUMBER_OF_INSTANCES - FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES, countOfHealthy)
        Assertions.assertEquals(FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES, countOfUnhealthy)
    }

    @Test
    fun `all nodes has unique id`() {
        val countOfUnique = upper.getListNodes().distinctBy { it.id }.count()
        Assertions.assertEquals(FUNCTIONAL_NUMBER_OF_INSTANCES, countOfUnique)
    }

    @Test
    fun `all nodes has unique index`() {
        val countOfUnique = upper.getListNodes().distinctBy { it.index }.count()
        Assertions.assertEquals(FUNCTIONAL_NUMBER_OF_INSTANCES, countOfUnique)
    }

    @Test
    fun `at least one miner and one verifier`() {
        val countOfMiners = upper.getListNodes().count { it.isMiner }
        val countOfVerifiers = upper.getListNodes().count { !it.isMiner }

        Assertions.assertTrue(countOfMiners > 0)
        Assertions.assertTrue(countOfVerifiers > 0)
    }

    @Test
    fun `count of sent trans_correctness`() {
        Assertions.assertEquals(
            FUNCTIONAL_NUMBER_OF_TRANSACTIONS,
            poolService.getPoolServiceUnitData().numberOfSentTransactions,
        )
    }

    @Test
    fun `count of received trans_correctness`() {
        Assertions.assertEquals(
            FUNCTIONAL_NUMBER_OF_INSTANCES,
            Node.unitTestingData.numberOfHandledObjs,
        )
    }

    @Test
    fun `count of received blocks for verify_correctness`() {
        Assertions.assertEquals(
            FUNCTIONAL_NUMBER_OF_TRANSACTIONS * FUNCTIONAL_NUMBER_OF_INSTANCES,
            Node.unitTestingData.numberOfHandledVerificationBlocks,
        )
    }

    @Test
    fun `count of received verification results_correctness`() {
        Assertions.assertEquals(
            FUNCTIONAL_NUMBER_OF_TRANSACTIONS * (FUNCTIONAL_NUMBER_OF_INSTANCES - 1),
            Node.unitTestingData.numberOfHandledVerificationResult,
        )
    }

    @Test
    fun `count of success verifications_correctness`() {
        Assertions.assertEquals(
            FUNCTIONAL_NUMBER_OF_TRANSACTIONS,
            Node.unitTestingData.numberOfSuccessVerifiedObjs,
        )
    }

    @Test
    fun `all casts are correct`() {
        Assertions.assertEquals(
            FUNCTIONAL_NUMBER_OF_TRANSACTIONS,
            Node.unitTestingData.numberOfCastedObjs,
        )
        Assertions.assertEquals(
            FUNCTIONAL_NUMBER_OF_TRANSACTIONS * FUNCTIONAL_NUMBER_OF_INSTANCES,
            Node.unitTestingData.numberOfCastedVerificationBlocks,
        )
    }
}
