package com.bknprocessing.app.service.wrapper

import com.bknprocessing.app.data.Transaction
import com.bknprocessing.app.service.PoolService
import com.bknprocessing.app.service.worker.IWorker
import com.bknprocessing.app.service.wrapper.uppper.ITestedUpper

class TestedPoolService(
    worker: IWorker<Transaction>,
    upper: ITestedUpper<Transaction>,
) : PoolService(worker, upper) {

    fun getPoolServiceUnitData() = unitTestingData
}
