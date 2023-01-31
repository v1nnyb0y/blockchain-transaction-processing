package com.bknprocessing.backend.util

import com.bknprocessing.backend.controllers.ExperimentDto
import com.bknprocessing.backend.type.StateTransferApproach
import com.bknprocessing.backend.type.ValidatorAlgorithm

class Predefined {
    companion object {
        const val NUMBER_OF_INSTANCES = 100
        const val NUMBER_OF_UNHEALTHY_NODES = 10
        const val NUMBER_OF_TRANSACTIONS = 10000

        val COROUTINE_WITH_POS = ExperimentDto(
            numberOfInstances = NUMBER_OF_INSTANCES,
            numberOfUnhealthyNodes = NUMBER_OF_UNHEALTHY_NODES,
            numberOfTransactions = NUMBER_OF_TRANSACTIONS,
            validatorAlgo = ValidatorAlgorithm.ProofOfState,
            stateTransferApproach = StateTransferApproach.Coroutine
        )
    }
}
