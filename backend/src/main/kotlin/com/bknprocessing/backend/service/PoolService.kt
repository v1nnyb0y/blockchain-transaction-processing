package com.bknprocessing.backend.service

import com.bknprocessing.backend.models.INode
import com.bknprocessing.backend.models.Node
import com.bknprocessing.backend.models.Transaction
import com.bknprocessing.backend.service.dto.VerificationDto
import com.bknprocessing.backend.service.dto.VerificationResultDto
import com.bknprocessing.backend.type.ValidatorAlgorithm
import com.bknprocessing.backend.utils.endNetworkVerify
import com.bknprocessing.backend.utils.logger
import com.bknprocessing.backend.utils.startNetworkVerify
import com.bknprocessing.backend.utils.startNode
import com.bknprocessing.backend.utils.waitAllChannelEmpty
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import java.time.Instant

open class PoolService(
    val nodesCount: Int,
    val unhealthyNodesCount: Int,
    val validatorAlgorithm: ValidatorAlgorithm,
) {
    /* Test data (unit-testing) */
    protected var numberOfHandledTransactions: Int = 0
    protected var numberOfHandledVerification: Int = 0
    protected var numberOfHandledVerificationResult: Int = 0
    protected var numberOfSuccessVerifiedTransactions: Int = 0
    protected var numberOfResendVerificationResult: Int = 0
    /* Test data */

    private val log: Logger by logger()

    var isFinished: Boolean = false
    protected val nodes = mutableListOf<INode>()

    @OptIn(ObsoleteCoroutinesApi::class)
    private val blockVerificationChannel = BroadcastChannel<VerificationDto>(capacity = nodesCount * nodesCount)
    private val blockVerificationResultChannel = Channel<VerificationResultDto>(capacity = UNLIMITED)
    private val transactionChannel = Channel<Transaction>(capacity = 1)

    init {
        val createdAt = Instant.now().toEpochMilli()
        var nodeIndex = 0
        for (idx in 0 until nodesCount - unhealthyNodesCount) {
            nodes.add(Node(index = nodeIndex, isHealthy = true, createdAt = createdAt))
            nodeIndex += 1
        }
        for (idx in 0 until unhealthyNodesCount) {
            nodes.add(Node(index = nodeIndex, isHealthy = false, createdAt = createdAt))
            nodeIndex += 1
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
    suspend fun run(numberOfTransactions: Int) = supervisorScope {
        for (i in 0 until nodes.size) {
            launch { doMine(nodes[i]) }
            launch { doVerify(nodes[i]) }
            log.startNode(isNodeHealthy = nodes[i].isHealthy, index = nodes[i].index)
        }

        launch {
            doSendTransactions(numberOfTransactions)
            while (
                numberOfHandledTransactions != numberOfTransactions ||
                numberOfHandledVerification < numberOfTransactions * (nodesCount - 1) ||
                numberOfHandledVerificationResult < numberOfTransactions * (nodesCount - 1)
            ) {
                delay(DELAY_MILSECS)
                log.waitAllChannelEmpty(
                    isTransactionChannelEmpty = transactionChannel.isEmpty,
                    isBlockVerificationChannelEmpty = blockVerificationChannel.openSubscription().isEmpty,
                    isBlockVerificationResultChannelEmpty = blockVerificationResultChannel.isEmpty,
                    numberOfHandledTransactions = numberOfHandledTransactions,
                    numberOfHandledBlocksForVerification = numberOfHandledVerification,
                    numberOfHandledResultsOfVerification = numberOfHandledVerificationResult,
                    numberOfResendResultsOfVerification = numberOfResendVerificationResult,
                )
            }

            isFinished = true
        }
    }

    private suspend fun doSendTransactions(numberOfTransactions: Int) {
        var i = 1
        while (i <= numberOfTransactions) {
            delay(DELAY_MILSECS)
            val result = transactionChannel.trySend(Transaction())
            if (result.isSuccess) {
                i += 1
            }
        }
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private suspend fun doMine(node: INode) {
        if (node.isMiner()) {
            while (!isFinished) {
                delay(DELAY_MILSECS)
                val trans = transactionChannel.tryReceive().getOrNull() ?: continue
                numberOfHandledTransactions += 1

                val constructedBlock = node.constructBlock(trans)
                val minedBlock = node.mineBlock(constructedBlock) ?: continue

                blockVerificationChannel.send(
                    VerificationDto(
                        block = minedBlock,
                        nodeIndex = node.index,
                    ),
                )

                // Start verification by nodes in network
                var countOfFinishedNodes = 0
                var countOfSuccessVerificationNodes = 0

                log.startNetworkVerify(node.isHealthy, node.index, minedBlock.currentHash)
                while (countOfFinishedNodes != nodesCount - 1) {
                    // TODO add checking on 80% of the success nodes
                    delay(DELAY_MILSECS)

                    val verificationResult =
                        blockVerificationResultChannel.tryReceive().getOrNull() ?: continue
                    if (minedBlock.currentHash == verificationResult.blockHash && // TODO complicated logic fix
                        node.index == verificationResult.nodeIndex
                    ) {
                        numberOfHandledVerificationResult += 1
                        countOfFinishedNodes += 1

                        if (verificationResult.verificationResult) {
                            countOfSuccessVerificationNodes += 1
                        }
                    } else {
                        numberOfResendVerificationResult += 1
                        blockVerificationResultChannel.send(verificationResult)
                    }
                }

                if (countOfSuccessVerificationNodes / (nodesCount - 1) - 0.8 >= EPSILON) {
                    numberOfSuccessVerifiedTransactions += 1
                    log.endNetworkVerify(node.isHealthy, node.index, minedBlock.currentHash, true)
                    node.addBlockToChain(minedBlock)
                    // TODO Vadim: where is the synchronization chain for all others nodes?

                    // TODO Do after synchronization:
                    val indexesToCountVerifiedBlocks: Map<Int, VerifiedBlocksAndAmountInfo> =
                        nodes.associate {
                            it.index to VerifiedBlocksAndAmountInfo(
                                blocksCount = node.countBlocksCreatedByNodeInChain(it.id),
                                amount = it.amount,
                            )
                        }
                    val nextIterationMinerIndex = determineNextIterationMinerIndex(indexesToCountVerifiedBlocks)
                    // TODO set nextIterationMiner
                } else {
                    log.endNetworkVerify(node.isHealthy, node.index, minedBlock.currentHash, false)
                    // node.removeBlockFromChain()
                }
            }
        }
    }

    private fun determineNextIterationMinerIndex(nodesMap: Map<Int, VerifiedBlocksAndAmountInfo>): Int {
        if (nodesMap.size < 2) {
            throw IllegalStateException("Determine index process: Node size is incorrect")
        }
        var leaderValue = -1
        var leaderIndex = -1
        nodesMap.entries.forEach {
            if (leaderValue < it.value.amount) { // TODO improve formula according blocksCount value
                leaderValue = it.value.amount
                leaderIndex = it.key
            }
        }
        return leaderIndex
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private suspend fun doVerify(node: INode) {
        val blockVerificationReceiveChannel = blockVerificationChannel.openSubscription()
        while (!isFinished) {
            delay(DELAY_MILSECS)
            val verificationDto = blockVerificationReceiveChannel.tryReceive().getOrNull() ?: continue
            if (verificationDto.nodeIndex == node.index) continue

            numberOfHandledVerification += 1
            blockVerificationResultChannel.send(
                VerificationResultDto(
                    blockHash = verificationDto.block.currentHash,
                    nodeIndex = verificationDto.nodeIndex,
                    verificationResult = node.verifyBlock(verificationDto.block),
                ),
            )
        }
    }

    inner class VerifiedBlocksAndAmountInfo(
        val blocksCount: Long,
        val amount: Int,
    )

    companion object {
        private const val EPSILON = 0.0000000001
        private const val DELAY_MILSECS: Long = 100
    }
}
