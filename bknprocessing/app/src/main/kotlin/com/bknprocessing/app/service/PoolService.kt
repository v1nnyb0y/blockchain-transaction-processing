package com.bknprocessing.app.service

import com.bknprocessing.app.data.Transaction
import com.bknprocessing.app.service.upper.IUpper
import com.bknprocessing.app.service.worker.IWorker
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class PoolService(
    private val worker: IWorker<Transaction>,
    private val upper: IUpper,
) {

    protected data class UnitTestingData(
        var numberOfSentTransactions: Int = 0,
    )
    protected val unitTestingData: UnitTestingData = UnitTestingData()

    suspend fun run(nodesCount: Int, unhealthyNodesCount: Int, numberOfTransactions: Int) = supervisorScope {
        upper.startNodes(nodesCount, unhealthyNodesCount)

        launch {
            for (i in 0 until numberOfTransactions) {
                worker.verifyObject(Transaction())
                unitTestingData.numberOfSentTransactions += 1
            }
        }
    }
}
