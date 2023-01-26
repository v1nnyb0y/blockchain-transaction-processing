package com.bknprocessing.backend.service

import com.bknprocessing.backend.controllers.models.StateTransferApproach
import com.bknprocessing.backend.controllers.models.ValidatorAlgorithm
import org.springframework.stereotype.Service

@Service
class ExperimentService(
    val blockChainService: BlockChainService
) {

    fun startExperiment(
        numberOfInstances: Int,
        numberOfTransactions: Int,
        numberOfUnhealthyNodes: Int,
        stateTransferApproach: StateTransferApproach,
        validatorAlgorithm: ValidatorAlgorithm
    ): Boolean {
        blockChainService.let {
            it.numberOfInstances = numberOfInstances
            it.numberOfUnhealthyNodes = numberOfUnhealthyNodes
            it.numberOfTransactions = numberOfTransactions
            it.stateTransferApproach = stateTransferApproach
            it.validatorAlgorithm = validatorAlgorithm
        }
        return blockChainService.startExperiment()
    }
}
