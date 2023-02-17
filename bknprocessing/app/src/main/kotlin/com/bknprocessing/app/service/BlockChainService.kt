package com.bknprocessing.app.service

import com.bknprocessing.app.service.upper.localupper.CoroutineLocalUpper
import com.bknprocessing.app.service.worker.CoroutineWorker
import com.bknprocessing.app.type.StateTransferApproach
import com.bknprocessing.app.type.ValidatorAlgorithm
import org.springframework.stereotype.Service

@Service
class BlockChainService {

    suspend fun createPoolAndRun(
        numberOfInstances: Int = 3,
        numberOfTransactions: Int = 100,
        numberOfUnhealthyNodes: Int = 0,
        validatorAlgorithm: ValidatorAlgorithm = ValidatorAlgorithm.ProofOfState,
        stateTransferApproach: StateTransferApproach = StateTransferApproach.Coroutine,
    ) {
        when (stateTransferApproach) {
            StateTransferApproach.Coroutine -> {
                with(PoolService(CoroutineWorker(), CoroutineLocalUpper())) {
                    this.run(numberOfInstances, numberOfUnhealthyNodes, numberOfTransactions)
                }
            }

            else -> throw NotImplementedError()
        }
    }
}
