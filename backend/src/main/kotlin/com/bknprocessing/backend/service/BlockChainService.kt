package com.bknprocessing.backend.service

import com.bknprocessing.backend.type.StateTransferApproach
import com.bknprocessing.backend.type.ValidatorAlgorithm
import org.springframework.stereotype.Service

@Service
class BlockChainService {

    suspend fun createPoolAndRun(
        numberOfInstances: Int = 3,
        numberOfTransactions: Int = 100,
        numberOfUnhealthyNodes: Int = 0,
        validatorAlgorithm: ValidatorAlgorithm = ValidatorAlgorithm.ProofOfState,
        stateTransferApproach: StateTransferApproach = StateTransferApproach.Coroutine
    ) {
        when (stateTransferApproach) {
            StateTransferApproach.Coroutine -> {
                with(
                    PoolService(
                        nodesCount = numberOfInstances,
                        unhealthyNodesCount = numberOfUnhealthyNodes,
                        validatorAlgorithm = validatorAlgorithm
                    )
                ) {
                    this.run(numberOfTransactions)
                }
            }

            else -> throw NotImplementedError()
        }
    }
}
