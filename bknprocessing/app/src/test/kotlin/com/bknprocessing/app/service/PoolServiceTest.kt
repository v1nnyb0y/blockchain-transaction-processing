package com.bknprocessing.app.service

import com.bknprocessing.app.service.worker.IWorker
import com.bknprocessing.app.service.wrapper.TestedPoolService
import com.bknprocessing.app.service.wrapper.uppper.ITestedUpper
import com.bknprocessing.app.utils.Predefined.Companion.FUNCTIONAL_NUMBER_OF_INSTANCES
import com.bknprocessing.app.utils.Predefined.Companion.FUNCTIONAL_NUMBER_OF_TRANSACTIONS
import com.bknprocessing.app.utils.Predefined.Companion.FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES
import com.bknprocessing.common.data.Transaction
import com.bknprocessing.node.nodeimpl.Node
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

abstract class PoolServiceTest(
    private val upper: ITestedUpper<Transaction>,
    private val worker: IWorker<Transaction>,
) {

    companion object {
        private var poolService: TestedPoolService? = null
        private var setuping: Boolean = true
    }

    init {
        runBlocking {
            if (poolService == null) {
                poolService = TestedPoolService(worker, upper).apply {
                    this.run(
                        FUNCTIONAL_NUMBER_OF_INSTANCES,
                        FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES,
                        FUNCTIONAL_NUMBER_OF_TRANSACTIONS,
                    )
                }
                setuping = false
            }
        }
    }

    private fun runBlockingUntilSetuping() = runBlocking {
        while (setuping) { delay(100) }
    }

    @Test
    fun `created correct number of nodes`() {
        runBlockingUntilSetuping()
        Assertions.assertEquals(FUNCTIONAL_NUMBER_OF_INSTANCES, poolService!!.upper.getListNodes().size)
    }

    @Test
    fun `create correct number of healthy and unhealthy nodes`() {
        val countOfHealthy = poolService!!.upper.getListNodes().count { it.isHealthy }
        val countOfUnhealthy = poolService!!.upper.getListNodes().count { !it.isHealthy }

        Assertions.assertEquals(FUNCTIONAL_NUMBER_OF_INSTANCES - FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES, countOfHealthy)
        Assertions.assertEquals(FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES, countOfUnhealthy)
    }

    @Test
    fun `all nodes has unique id`() {
        runBlockingUntilSetuping()
        val countOfUnique = poolService!!.upper.getListNodes().distinctBy { it.id }.count()
        Assertions.assertEquals(FUNCTIONAL_NUMBER_OF_INSTANCES, countOfUnique)
    }

    @Test
    fun `all nodes has unique index`() {
        runBlockingUntilSetuping()
        val countOfUnique = poolService!!.upper.getListNodes().distinctBy { it.index }.count()
        Assertions.assertEquals(FUNCTIONAL_NUMBER_OF_INSTANCES, countOfUnique)
    }

    @Test
    fun `at least one miner and one verifier`() {
        runBlockingUntilSetuping()
        val countOfMiners = poolService!!.upper.getListNodes().count { it.isMiner }
        val countOfVerifiers = poolService!!.upper.getListNodes().count { !it.isMiner }

        Assertions.assertTrue(countOfMiners > 0)
        Assertions.assertTrue(countOfVerifiers > 0)
    }

    @Test
    fun `count of sent trans_correctness`() {
        runBlockingUntilSetuping()
        Assertions.assertEquals(
            FUNCTIONAL_NUMBER_OF_TRANSACTIONS,
            poolService!!.getPoolServiceUnitData().numberOfSentTransactions,
        )
    }

    @Test
    fun `count of received trans_correctness`() {
        runBlockingUntilSetuping()
        Assertions.assertEquals(
            FUNCTIONAL_NUMBER_OF_INSTANCES,
            Node.unitTestingData.numberOfHandledObjs,
        )
    }

    @Test
    fun `count of received blocks for verify_correctness`() {
        runBlockingUntilSetuping()
        Assertions.assertEquals(
            FUNCTIONAL_NUMBER_OF_TRANSACTIONS * FUNCTIONAL_NUMBER_OF_INSTANCES,
            Node.unitTestingData.numberOfHandledVerificationBlocks,
        )
    }

    @Test
    fun `count of received verification results_correctness`() {
        runBlockingUntilSetuping()
        Assertions.assertEquals(
            FUNCTIONAL_NUMBER_OF_TRANSACTIONS * (FUNCTIONAL_NUMBER_OF_INSTANCES - 1),
            Node.unitTestingData.numberOfHandledVerificationResult,
        )
    }

    @Test
    fun `count of success verifications_correctness`() {
        runBlockingUntilSetuping()
        Assertions.assertEquals(
            FUNCTIONAL_NUMBER_OF_TRANSACTIONS,
            Node.unitTestingData.numberOfSuccessVerifiedObjs,
        )
    }

    @Test
    fun `all casts are correct`() {
        runBlockingUntilSetuping()
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
