package com.bknprocessing.backend.service

import com.bknprocessing.backend.type.StateTransferApproach
import com.bknprocessing.backend.type.ValidatorAlgorithm
import org.springframework.stereotype.Service

@Service
class ExperimentRunnerService {

    suspend fun start(
        numberOfInstances: Int,
        numberOfTransactions: Int,
        numberOfUnhealthyNodes: Int,
        stateTransferApproach: StateTransferApproach,
        validatorAlgorithm: ValidatorAlgorithm
    ) = BlockChainService(
        nodesCount = numberOfInstances,
        unhealthyNodesCount = numberOfUnhealthyNodes,
        numberOfTransactions = numberOfTransactions,
        stateTransferApproach = stateTransferApproach,
        validatorAlgorithm = validatorAlgorithm
    ).createPoolAndRun()
}
