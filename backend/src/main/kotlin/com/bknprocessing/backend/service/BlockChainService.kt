package com.bknprocessing.backend.service

import com.bknprocessing.backend.type.StateTransferApproach
import com.bknprocessing.backend.type.ValidatorAlgorithm
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import org.springframework.stereotype.Service

@Service
class BlockChainService(
    var nodesCount: Int = 3,
    var numberOfTransactions: Int = 100,
    var unhealthyNodesCount: Int = 0,
    var validatorAlgorithm: ValidatorAlgorithm = ValidatorAlgorithm.ProofOfState,
    var stateTransferApproach: StateTransferApproach = StateTransferApproach.Coroutine
) {

    @OptIn(ObsoleteCoroutinesApi::class)
    suspend fun createPoolAndRun() {
        when (stateTransferApproach) {
            StateTransferApproach.Coroutine -> {
                with(
                    PoolService(
                        txChannel = ConflatedBroadcastChannel(),
                        blockChannel = Channel(),
                        resultChannel = Channel(),
                        nodesCount = nodesCount,
                        unhealthyNodesCount = unhealthyNodesCount,
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
