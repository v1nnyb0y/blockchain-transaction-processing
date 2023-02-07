package com.bknprocessing.backend.util

import com.bknprocessing.backend.controllers.ExperimentDto
import com.bknprocessing.backend.type.StateTransferApproach
import com.bknprocessing.backend.type.ValidatorAlgorithm

class Predefined {
    companion object {
        /* Integration Tests */
        const val INTEGRATION_NUMBER_OF_INSTANCES = 100
        const val INTEGRATION_NUMBER_OF_UNHEALTHY_NODES = 10
        const val INTEGRATION_NUMBER_OF_TRANSACTIONS = 10000

        val COROUTINE_WITH_POS = ExperimentDto(
            numberOfInstances = INTEGRATION_NUMBER_OF_INSTANCES,
            numberOfUnhealthyNodes = INTEGRATION_NUMBER_OF_UNHEALTHY_NODES,
            numberOfTransactions = INTEGRATION_NUMBER_OF_TRANSACTIONS,
            validatorAlgo = ValidatorAlgorithm.ProofOfState,
            stateTransferApproach = StateTransferApproach.Coroutine
        )
        /* Integration Tests */

        /* Functional Tests */

        const val FUNCTIONAL_NUMBER_OF_INSTANCES = 10
        const val FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES = 2
        const val FUNCTIONAL_NUMBER_OF_TRANSACTIONS = 10

        /* Functional Test */
    }
}
