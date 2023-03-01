package com.bknprocessing.app.service

import com.bknprocessing.app.service.upper.localupper.CoroutineLocalUpper
import com.bknprocessing.app.service.upper.localupper.KafkaLocalUpper
import com.bknprocessing.app.service.upper.remoteupper.RestJsonRemoteUpper
import com.bknprocessing.app.service.worker.CoroutineWorker
import com.bknprocessing.app.service.worker.KafkaWorker
import com.bknprocessing.app.service.worker.RestJsonWorker
import com.bknprocessing.app.type.StateTransferApproach
import com.bknprocessing.app.type.ValidatorAlgorithm
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service

@Service
class BlockChainService {

    fun createPoolAndRun(
        numberOfInstances: Int = 3,
        numberOfTransactions: Int = 100,
        numberOfUnhealthyNodes: Int = 0,
        validatorAlgorithm: ValidatorAlgorithm = ValidatorAlgorithm.ProofOfState,
        stateTransferApproach: StateTransferApproach = StateTransferApproach.Coroutine,
    ) = runBlocking {
        when (stateTransferApproach) {
            StateTransferApproach.Coroutine -> {
                with(PoolService(CoroutineWorker(), CoroutineLocalUpper())) {
                    this.run(numberOfInstances, numberOfUnhealthyNodes, numberOfTransactions)
                }
            }
            StateTransferApproach.Kafka -> {
                with(PoolService(KafkaWorker(), KafkaLocalUpper())) {
                    this.run(numberOfInstances, numberOfUnhealthyNodes, numberOfTransactions)
                }
            }
            StateTransferApproach.REST -> {
                with(PoolService(RestJsonWorker(), RestJsonRemoteUpper())) {
                    this.run(numberOfInstances, numberOfUnhealthyNodes, numberOfTransactions)
                }
            }

            else -> throw NotImplementedError()
        }
    }
}
