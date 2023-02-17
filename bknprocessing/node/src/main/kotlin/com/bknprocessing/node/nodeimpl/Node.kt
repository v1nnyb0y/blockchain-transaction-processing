package com.bknprocessing.node.nodeimpl

import com.bknprocessing.common.IClient
import com.bknprocessing.common.IServer
import com.bknprocessing.node.dto.Block
import com.bknprocessing.node.dto.StateAction
import com.bknprocessing.node.dto.StateChangeDto
import com.bknprocessing.node.dto.VerificationDto
import com.bknprocessing.node.dto.VerificationResultDto
import com.bknprocessing.node.nodeimpl.miner.INodeMiner
import com.bknprocessing.node.nodeimpl.miner.NodeMinerImpl
import com.bknprocessing.node.nodeimpl.verifier.INodeVerifier
import com.bknprocessing.node.nodeimpl.verifier.NodeVerifierImpl
import com.bknprocessing.node.utils.determineNextIterationMinerIndex
import com.bknprocessing.node.utils.hash
import kotlinx.coroutines.delay
import java.util.UUID
import kotlin.random.Random

@Suppress("UNCHECKED_CAST")
class Node<T>(
    val id: UUID = UUID.randomUUID(),

    val index: Int,
    val isHealthy: Boolean,
    val networkSize: Int,
    createdAt: Long,

    val client: IClient,
    val server: IServer,
) : INode {

    private enum class TopicsList {
        ObjQueue, VerificationBlockQueue, VerificationResultBlockQueue,
        StateChange,
    }

    protected data class UnitTestingData(
        var numberOfHandledObjs: Int = 0,
        var numberOfCastedObjs: Int = 0,

        var numberOfHandledVerificationBlocks: Int = 0,
        var numberOfCastedVerificationBlocks: Int = 0,

        var numberOfHandledVerificationResult: Int = 0,
        var numberOfSuccessVerifiedObjs: Int = 0,
    )
    protected val unitTestingData = UnitTestingData()

    protected var chain: MutableList<Block<T>> = mutableListOf()
    protected val lastBlockHashInChain
        get() = chain.last().currentHash
    protected var amount: Int = Random.nextInt(MIN_MONEY, MAX_MONEY)

    protected var isFinished: Boolean = false
    protected var stopOnStateChanging: Boolean = false

    private val calculateHash = { block: Block<T> -> with(block) { "$previousHash$objs$timestamp$nonce".hash() } }
    private val isMined = { block: Block<T> -> with(block) { currentHash.startsWith(validPrefix) } }

    protected val miner: INodeMiner<T> = NodeMinerImpl(calculateHash, isMined)
    protected val verifier: INodeVerifier<T> = NodeVerifierImpl(calculateHash, isMined)

    init {
        if (index == 0) amount = MAX_MONEY
        if (index > 0) amount = 0

        // generate genesis block
        val block: Block<T> = miner.mineBlock(
            Block(
                previousHash = "",
                timestamp = createdAt,
                generatedBy = null,
            ),
        )!!
        chain.add(block)
    }

    override suspend fun waitStateChangeAction() {
        while (!isFinished) {
            delay(DELAY_MILSEC)
            val stateChangeDto = client.getObj(TopicsList.StateChange.name) ?: continue
            val castedStateChangeDto = (stateChangeDto as? StateChangeDto<T>) ?: throw IllegalStateException("Wrong DTO in state change queue")

            stopOnStateChanging = true
            when (castedStateChangeDto.action) {
                StateAction.ACCEPT_NEW_BLOCK -> acceptNewBlock(castedStateChangeDto.block!!)
                StateAction.ACTUALIZE -> removeUnhealthyBlocks(castedStateChangeDto.block!!)
            }
            stopOnStateChanging = false
        }
    }

    override suspend fun runMiner() {
        if (miner.isMiner(amount)) {
            while (!isFinished) {
                delay(DELAY_MILSEC)
                val obj = client.getObj(TopicsList.ObjQueue.name) ?: continue
                unitTestingData.numberOfHandledObjs += 1

                val castedObj = (obj as? T) ?: continue
                unitTestingData.numberOfCastedObjs += 1

                while (stopOnStateChanging) delay(DELAY_MILSEC)
                val constructedBlock: Block<T> = miner.constructBlock(castedObj, lastBlockHashInChain, id)
                val minedBlock: Block<T> = miner.mineBlock(constructedBlock) ?: continue

                server.sendObj(
                    VerificationDto(
                        nodeId = id,
                        block = minedBlock,
                    ),
                    TopicsList.VerificationBlockQueue.name,
                )

                var countOfFinishedNodes = 0
                var countOfSuccessNodes = 0

                while (countOfFinishedNodes != networkSize - 1) {
                    // TODO add checking on 80% of the success nodes
                    delay(DELAY_MILSEC)

                    val verificationResult = client.getObj(TopicsList.VerificationBlockQueue.name) ?: continue
                    val castedVerificationResult = (verificationResult as? VerificationResultDto) ?: throw IllegalStateException("Wrong DTO in verification result queue")

                    if (castedVerificationResult.blockHash == minedBlock.currentHash) {
                        unitTestingData.numberOfHandledVerificationResult += 1
                        countOfFinishedNodes += 1

                        if (castedVerificationResult.verificationResult) {
                            countOfSuccessNodes += 1
                        }
                    } else {
                        server.sendObj(
                            castedVerificationResult,
                            TopicsList.VerificationResultBlockQueue.name,
                        )
                    }
                }

                if (countOfSuccessNodes / (networkSize - 1) - 0.8 >= EPSILON) {
                    unitTestingData.numberOfSuccessVerifiedObjs += 1
                    chain.add(minedBlock)
                    distributeBlockToNetwork(minedBlock)

                    val indexesToCountVerifiedBlocks: Map<UUID, VerifiedBlocksAndAmountInfo> =
                        chain
                            .groupBy { it.generatedBy }
                            .map { it.key!! to VerifiedBlocksAndAmountInfo(it.value.count().toLong(), 12) } // TODO fix money
                            .toMap()
                    val nextIterationMinerIndex = determineNextIterationMinerIndex(indexesToCountVerifiedBlocks) // TODO use next miner
                } else {
                    actualizeNetworkDueWrongVerification(chain.last())
                }
            }
        }
    }

    override suspend fun runVerifier() {
        while (!isFinished) {
            delay(DELAY_MILSEC)
            val verificationDto = client.getObj(TopicsList.VerificationBlockQueue.name) ?: continue
            unitTestingData.numberOfHandledVerificationBlocks += 1

            val castedVerificationDto = (verificationDto as? VerificationDto<T>) ?: throw IllegalStateException("Wrong DTO in verification queue")
            unitTestingData.numberOfCastedVerificationBlocks += 1

            while (stopOnStateChanging) delay(DELAY_MILSEC)
            server.sendObj(
                VerificationResultDto(
                    blockHash = castedVerificationDto.block.currentHash,
                    nodeId = verificationDto.nodeId,
                    verificationResult = verifier.verifyBlock(castedVerificationDto.block, chain),
                ),
                TopicsList.VerificationResultBlockQueue.name,
            )
        }
    }

    private fun acceptNewBlock(newBLock: Block<T>) {
        // TODO
    }

    private fun removeUnhealthyBlocks(lastSuccessBlock: Block<T>) {
        // TODO
    }

    private fun distributeBlockToNetwork(newBlock: Block<T>) {
        server.sendObj(
            StateChangeDto(
                block = newBlock,
                action = StateAction.ACCEPT_NEW_BLOCK,
            ),
            TopicsList.StateChange.name,
        )
    }

    private fun actualizeNetworkDueWrongVerification(lastSuccessBlock: Block<T>) {
        server.sendObj(
            StateChangeDto(
                block = lastSuccessBlock,
                action = StateAction.ACTUALIZE,
            ),
            TopicsList.StateChange.name,
        )
    }

    inner class VerifiedBlocksAndAmountInfo(
        val blocksCount: Long,
        val amount: Int,
    )

    companion object {
        const val MAX_MONEY: Int = 10000
        const val MIN_MONEY: Int = 100

        private const val DELAY_MILSEC: Long = 100
        private const val EPSILON = 0.0000000001

        protected const val difficulty = 2
        protected val validPrefix = "0".repeat(difficulty)
    }
}
