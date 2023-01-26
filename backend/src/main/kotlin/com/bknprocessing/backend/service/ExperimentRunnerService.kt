package com.bknprocessing.backend.service

import com.bknprocessing.backend.type.StateTransferApproach
import com.bknprocessing.backend.type.ValidatorAlgorithm
import org.springframework.stereotype.Service

@Service
class ExperimentRunnerService {

    fun startExperiment(
        numberOfInstances: Int,
        numberOfTransactions: Int,
        numberOfUnhealthyNodes: Int,
        stateTransferApproach: StateTransferApproach,
        validatorAlgorithm: ValidatorAlgorithm
    ): Boolean = BlockChainService(
        numberOfInstances = numberOfInstances,
        numberOfUnhealthyNodes = numberOfUnhealthyNodes,
        numberOfTransactions = numberOfTransactions,
        stateTransferApproach = stateTransferApproach,
        validatorAlgorithm = validatorAlgorithm
    ).startExperiment()
}
