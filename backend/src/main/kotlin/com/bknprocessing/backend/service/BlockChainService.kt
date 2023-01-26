package com.bknprocessing.backend.service

import com.bknprocessing.backend.controllers.models.StateTransferApproach
import com.bknprocessing.backend.controllers.models.ValidatorAlgorithm
import com.bknprocessing.backend.pool.NodePoolService
import org.springframework.stereotype.Service

@Service
class BlockChainService(
    val nodePoolService: NodePoolService
) {

    var numberOfInstances: Int = 1
    var numberOfTransactions: Int = 1
    var numberOfUnhealthyNodes: Int = 1
    var validatorAlgorithm: ValidatorAlgorithm = ValidatorAlgorithm.ProofOfState
    var stateTransferApproach: StateTransferApproach = StateTransferApproach.Coroutine

    fun startExperiment(): Boolean {
        when (stateTransferApproach) {
            StateTransferApproach.Coroutine -> {
                nodePoolService.createExpPool(numberOfInstances, numberOfUnhealthyNodes, validatorAlgorithm)
                nodePoolService.startExpPool(numberOfTransactions)
            }

            else -> throw NotImplementedError()
        }

        return true
    }
}
