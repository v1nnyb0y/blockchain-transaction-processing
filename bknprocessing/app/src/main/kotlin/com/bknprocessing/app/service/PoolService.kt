package com.bknprocessing.app.service

import com.bknprocessing.app.data.Transaction
import com.bknprocessing.app.service.upper.IUpper
import com.bknprocessing.app.service.worker.IWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

open class PoolService(
    private val worker: IWorker<Transaction>,
    private val upper: IUpper<Transaction>,
) {

    data class UnitTestingData(
        var numberOfSentTransactions: Int = 0,
    )
    protected val unitTestingData: UnitTestingData = UnitTestingData()

    suspend fun run(nodesCount: Int, unhealthyNodesCount: Int, numberOfTransactions: Int) = supervisorScope {
        launch { upper.startNodes(nodesCount, unhealthyNodesCount) }

        launch {
            var sentTransactions = 0
            while (sentTransactions < numberOfTransactions) {
                delay(DELAY_MILSEC)
                if (worker.verifyObject(Transaction())) {
                    sentTransactions += 1
                    unitTestingData.numberOfSentTransactions += 1
                }
            }
            worker.finishNodes(numberOfTransactions)
        }
    }

    companion object {
        private const val DELAY_MILSEC: Long = 100
    }
}
