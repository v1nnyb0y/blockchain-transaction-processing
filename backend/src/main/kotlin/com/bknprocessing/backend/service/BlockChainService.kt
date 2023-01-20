package com.bknprocessing.backend.service

import com.bknprocessing.backend.controllers.models.StateTransferApproach
import com.bknprocessing.backend.controllers.models.ValidatorAlgorithm
import com.bknprocessing.backend.service.blockchain.Block
import com.bknprocessing.backend.service.blockchain.Node
import com.bknprocessing.backend.service.blockchain.Transaction
import com.bknprocessing.backend.utils.logger
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class BlockChainService {

    val log: Logger by logger()

    var numberOfInstances: Int = 1
    var numberOfTransactions: Int = 1
    var validatorAlgorithm: ValidatorAlgorithm = ValidatorAlgorithm.ProofOfWork
    var stateTransferApproach: StateTransferApproach = StateTransferApproach.REST

    private fun createBlock(lastHash: String): Block {
        val block = Block(lastHash)
        block.addTransaction(Transaction())
        return block
    }

    fun startExperiment(): Boolean {
        val nodes = ArrayList<Node>()
        for (i in 1..numberOfInstances) {
            nodes.add(Node())
        }

        for (i in 1..numberOfTransactions) {
            for (j in 0 until nodes.size) {
                System.out.println(j)
                nodes[j].add(createBlock(nodes[j].lastHash))
                if (!nodes[j].isValid()) {
                    log.info("Node isn't valid")
                }
            }
        }
        return true
    }
}
