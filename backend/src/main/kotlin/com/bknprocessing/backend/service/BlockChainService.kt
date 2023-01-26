package com.bknprocessing.backend.service

import com.bknprocessing.backend.type.StateTransferApproach
import com.bknprocessing.backend.type.ValidatorAlgorithm
import org.springframework.stereotype.Service

@Service
class BlockChainService(
    var numberOfInstances: Int = 1,
    var numberOfTransactions: Int = 1,
    var numberOfUnhealthyNodes: Int = 1,
    var validatorAlgorithm: ValidatorAlgorithm = ValidatorAlgorithm.ProofOfState,
    var stateTransferApproach: StateTransferApproach = StateTransferApproach.Coroutine
) {

    fun startExperiment(): Boolean {
        when (stateTransferApproach) {
            StateTransferApproach.Coroutine -> {
                with(PoolService()) {
                    this.createExpPool(numberOfInstances, numberOfUnhealthyNodes, validatorAlgorithm)
                    this.startExpPool(numberOfTransactions)
                }
            }
            else -> throw NotImplementedError()
        }
        return true
    }
}
