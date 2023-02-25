package com.bknprocessing.app.utils

import com.bknprocessing.app.controllers.ExperimentDto
import com.bknprocessing.app.type.StateTransferApproach
import com.bknprocessing.app.type.ValidatorAlgorithm

class Predefined {
    companion object {
        /* Integration Tests */
        private const val INTEGRATION_NUMBER_OF_INSTANCES = 100
        private const val INTEGRATION_NUMBER_OF_UNHEALTHY_NODES = 20
        private const val INTEGRATION_NUMBER_OF_TRANSACTIONS = 100

        val COROUTINE_WITH_POS = ExperimentDto(
            numberOfInstances = INTEGRATION_NUMBER_OF_INSTANCES,
            numberOfUnhealthyNodes = INTEGRATION_NUMBER_OF_UNHEALTHY_NODES,
            numberOfTransactions = INTEGRATION_NUMBER_OF_TRANSACTIONS,
            validatorAlgo = ValidatorAlgorithm.ProofOfState,
            stateTransferApproach = StateTransferApproach.Coroutine,
        )
        /* Integration Tests */

        /* Functional Tests */

        const val FUNCTIONAL_NUMBER_OF_INSTANCES = 10
        const val FUNCTIONAL_NUMBER_OF_UNHEALTHY_NODES = 2
        const val FUNCTIONAL_NUMBER_OF_TRANSACTIONS = 10
    }
}
