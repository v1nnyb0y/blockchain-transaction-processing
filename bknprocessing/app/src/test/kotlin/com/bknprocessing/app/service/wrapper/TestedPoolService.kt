package com.bknprocessing.app.service.wrapper

import com.bknprocessing.app.service.PoolService
import com.bknprocessing.app.service.worker.IWorker
import com.bknprocessing.app.service.wrapper.uppper.ITestedUpper
import com.bknprocessing.common.data.Transaction

class TestedPoolService(
    worker: IWorker<Transaction>,
    val upper: ITestedUpper<Transaction>,
) : PoolService(worker, upper) {

    fun getPoolServiceUnitData() = unitTestingData
}
