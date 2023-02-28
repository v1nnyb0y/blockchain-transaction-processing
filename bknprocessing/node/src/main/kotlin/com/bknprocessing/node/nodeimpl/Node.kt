package com.bknprocessing.node.nodeimpl

import com.bknprocessing.common.IClient
import com.bknprocessing.common.IServer
import com.bknprocessing.node.dto.Block
import com.bknprocessing.node.dto.NodeInfo
import com.bknprocessing.node.dto.StateAction
import com.bknprocessing.node.dto.StateChangeDto
import com.bknprocessing.node.dto.VerificationDto
import com.bknprocessing.node.dto.VerificationResultDto
import com.bknprocessing.node.nodeimpl.miner.INodeMiner
import com.bknprocessing.node.nodeimpl.miner.NodeMinerImpl
import com.bknprocessing.node.nodeimpl.verifier.INodeVerifier
import com.bknprocessing.node.nodeimpl.verifier.NodeVerifierImpl
import com.bknprocessing.node.utils.constructedBlock
import com.bknprocessing.node.utils.determineNextIterationMinerIndex
import com.bknprocessing.node.utils.endVerify
import com.bknprocessing.node.utils.finishMiner
import com.bknprocessing.node.utils.finishSmartContractListener
import com.bknprocessing.node.utils.finishVerifier
import com.bknprocessing.node.utils.hash
import com.bknprocessing.node.utils.logger
import com.bknprocessing.node.utils.minedBlock
import com.bknprocessing.node.utils.setNewMiner
import com.bknprocessing.node.utils.startMiner
import com.bknprocessing.node.utils.startNetworkVerify
import com.bknprocessing.node.utils.startSmAcceptNewBlock
import com.bknprocessing.node.utils.startSmActualizeChain
import com.bknprocessing.node.utils.startSmFinishProcess
import com.bknprocessing.node.utils.startSmartContractListener
import com.bknprocessing.node.utils.startVerifier
import com.bknprocessing.node.utils.startVerify
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import java.util.UUID
import kotlin.random.Random

@Suppress("UNCHECKED_CAST")
open class Node<T>(
    val id: UUID = UUID.randomUUID(),

    val index: Int,
    val isHealthy: Boolean,
    val networkSize: Int,
    createdAt: Long,

    val client: IClient,
    val server: IServer,
) : INode {

    val log: Logger by logger()
    protected enum class TopicsList {
        ObjQueue, VerificationBlockQueue, VerificationResultBlockQueue,
        StateChange,
    }

    protected var nodeInfos: MutableMap<UUID, Int> = mutableMapOf()
    protected var chain: MutableList<Block<T>> = mutableListOf()
    protected val lastBlockHashInChain get() = chain.last().currentHash
    protected var amount: Int = Random.nextInt(MIN_MONEY, MAX_MONEY)

    private var isFinished: Boolean = false
    private var stopOnStateChanging: Boolean = false

    protected val calculateHash = { block: Block<T> ->
        with(block) {
            // "$previousHash$objs$timestamp$nonce".hash()
            "$previousHash$timestamp$nonce".hash()
        }
    }
    protected val isMined = { block: Block<T> -> with(block) { currentHash.startsWith(validPrefix) } }
    var isMiner = false

    protected val miner: INodeMiner<T> = NodeMinerImpl(calculateHash, isMined)
    private val verifier: INodeVerifier<T> = NodeVerifierImpl(calculateHash, isMined)

    init {
        if (index == 0) {
            amount = MAX_MONEY
            isMiner = true
        }
        if (index > 0) amount = 0

        // generate genesis block
        val block: Block<T> = miner.mineBlock(
            Block(
                previousHash = "",
                timestamp = createdAt,
                nodeInfo = null,
            ),
        )!!
        chain.add(block)
        nodeInfos[id] = amount
    }

    override suspend fun waitStateChangeAction() = supervisorScope {
        log.startSmartContractListener(isHealthy, index)
        while (!isFinished) {
            delay(DELAY_MILSEC)
            val stateChangeDto = client.getObj(TopicsList.StateChange.name, index) ?: continue
            var castedStateChangeDto = (stateChangeDto as? StateChangeDto<T>)

            var action: StateAction
            var data: Any?

            if (castedStateChangeDto == null) {
                // for finish process only (can be removed for real project)
                data = (stateChangeDto as? Int)

                if (data == null) {
                    data = (stateChangeDto as? UUID) ?: throw IllegalStateException("Wrong DTO in state change queue")
                    action = StateAction.SET_NEW_MINER
                } else {
                    action = StateAction.FINISH
                }
            } else {
                action = castedStateChangeDto.action
                data = castedStateChangeDto.data
            }

            stopOnStateChanging = true
            when (action) {
                StateAction.ACCEPT_NEW_BLOCK -> handleAcceptNewBlock(data as Block<T>)
                StateAction.ACTUALIZE -> handleRemoveUnhealthyBlocks(data as Block<T>)
                StateAction.FINISH -> {
                    stopOnStateChanging = false
                    launch { handleFinishNodeExperiment(data as Int) }
                }
                StateAction.SET_NEW_MINER -> {
                    handleSetNewMiner(data as UUID)
                }
            }
            stopOnStateChanging = false
        }
        log.finishSmartContractListener(isHealthy, index)
    }

    override suspend fun runMiner() {
        log.startMiner(isHealthy, index)
        while (!isFinished) {
            delay(DELAY_MILSEC)
            // TODO support multi miner
            if (/*miner.isMiner(amount) || */isMiner) {
                val obj = client.getObj(TopicsList.ObjQueue.name) ?: continue
                unitTestingData.numberOfHandledObjs += 1

                val castedObj = (obj as? T) ?: continue
                unitTestingData.numberOfCastedObjs += 1

                lateinit var constructedBlock: Block<T>
                lateinit var minedBlock: Block<T>
                do {
                    delay(DELAY_MILSEC)
                    constructedBlock = miner.constructBlock(
                        castedObj,
                        lastBlockHashInChain,
                        id,
                        index,
                        amount + 1,
                    )
                    log.constructedBlock(isHealthy, index)
                    minedBlock = miner.mineBlock(constructedBlock) ?: continue
                    log.minedBlock(isHealthy, index, minedBlock.currentHash)
                } while (stopOnStateChanging)

                server.sendObj(
                    VerificationDto(
                        nodeId = id,
                        block = minedBlock,
                    ),
                    TopicsList.VerificationBlockQueue.name,
                )

                var countOfFinishedNodes = 0
                var countOfSuccessNodes = 0

                log.startNetworkVerify(isHealthy, index)
                while (countOfFinishedNodes != networkSize - 1) {
                    // TODO add checking on 80% of the success nodes
                    delay(DELAY_MILSEC)

                    val verificationResult = client.getObj(TopicsList.VerificationResultBlockQueue.name) ?: continue
                    val castedVerificationResult = (verificationResult as? VerificationResultDto) ?: throw IllegalStateException("Wrong DTO in verification result queue")

                    if (castedVerificationResult.blockHash == minedBlock.currentHash) {
                        unitTestingData.numberOfHandledVerificationResult += 1
                        countOfFinishedNodes += 1

                        if (castedVerificationResult.verificationResult) {
                            countOfSuccessNodes += 1
                        }

                        nodeInfos[castedVerificationResult.nodeInfo.id] = castedVerificationResult.nodeInfo.amount
                    } else {
                        server.sendObj(
                            castedVerificationResult,
                            TopicsList.VerificationResultBlockQueue.name,
                        )
                    }
                }

                amount += 1
                nodeInfos[id] = amount
                if (countOfSuccessNodes / (networkSize - 1) - 0.8 >= EPSILON) {
                    unitTestingData.numberOfSuccessVerifiedObjs += 1
                    chain.add(minedBlock)
                    distributeBlockToNetwork(minedBlock)

                    val nextIterationMinerIndex = determineNextIterationMinerIndex(findMapOfNodes())
                    setNewMiner(nextIterationMinerIndex)
                    log.endVerify(isHealthy, index, minedBlock.currentHash, true)
                } else {
                    actualizeNetworkDueWrongVerification(chain.last())
                    log.endVerify(isHealthy, index, minedBlock.currentHash, false)
                }
            }
        }
        log.finishMiner(isHealthy, index)
    }

    override suspend fun runVerifier() {
        log.startVerifier(isHealthy, index)
        while (!isFinished) {
            delay(DELAY_MILSEC)
            val verificationDto = client.getObj(TopicsList.VerificationBlockQueue.name, index) ?: continue
            unitTestingData.numberOfHandledVerificationBlocks += 1

            val castedVerificationDto = (verificationDto as? VerificationDto<T>) ?: throw IllegalStateException("Wrong DTO in verification queue")
            unitTestingData.numberOfCastedVerificationBlocks += 1

            if (castedVerificationDto.nodeId == id) continue

            while (stopOnStateChanging) delay(DELAY_MILSEC)
            amount += 1
            log.startVerify(isHealthy, index, castedVerificationDto.block.currentHash)
            server.sendObj(
                VerificationResultDto(
                    blockHash = castedVerificationDto.block.currentHash,
                    nodeId = verificationDto.nodeId,
                    verificationResult = verifier.verifyBlock(castedVerificationDto.block, chain),
                    nodeInfo = NodeInfo(id = id, amount = amount),
                ),
                TopicsList.VerificationResultBlockQueue.name,
            )
        }
        log.finishVerifier(isHealthy, index)
    }

    protected fun findMapOfNodes(): Map<UUID, VerifiedBlocksAndAmountInfo> {
        val map: MutableMap<UUID, VerifiedBlocksAndAmountInfo> = chain
            .filter { it.nodeInfo != null }
            .groupBy { it.nodeInfo!!.id }
            .map {
                it.key to VerifiedBlocksAndAmountInfo(
                    it.value.count().toLong(),
                    it.value.maxBy { it.timestamp }.nodeInfo!!.amount,
                )
            }
            .toMap()
            .toMutableMap()

        nodeInfos.entries.forEach {
            map.putIfAbsent(it.key, VerifiedBlocksAndAmountInfo(0, it.value))
        }

        return map
    }

    protected fun handleAcceptNewBlock(newBlock: Block<T>) {
        log.startSmAcceptNewBlock(isHealthy, index)
        if (newBlock.currentHash == lastBlockHashInChain) return
        chain.add(newBlock)
    }
    protected fun handleRemoveUnhealthyBlocks(lastSuccessBlock: Block<T>) {
        log.startSmActualizeChain(isHealthy, index)
        while (lastSuccessBlock.currentHash != lastBlockHashInChain) {
            chain.removeLast()
        }
    }
    protected fun handleSetNewMiner(minerId: UUID) {
        isMiner = minerId == id
        if (isMiner) {
            log.setNewMiner(isHealthy, index)
        }
    }
    private suspend fun handleFinishNodeExperiment(transCount: Int) {
        log.startSmFinishProcess(isHealthy, index)
        while (chain.size != transCount + 1) { // + 1 cause of generic block
            delay(DELAY_MILSEC)
        }
        isFinished = true
    }

    private fun distributeBlockToNetwork(newBlock: Block<T>) {
        server.sendObj(
            StateChangeDto(
                data = newBlock,
                action = StateAction.ACCEPT_NEW_BLOCK,
            ),
            TopicsList.StateChange.name,
        )
    }
    private fun actualizeNetworkDueWrongVerification(lastSuccessBlock: Block<T>) {
        server.sendObj(
            StateChangeDto(
                data = lastSuccessBlock,
                action = StateAction.ACTUALIZE,
            ),
            TopicsList.StateChange.name,
        )
    }
    private fun setNewMiner(minerId: UUID) {
        if (minerId == id) return

        isMiner = false
        server.sendObj(
            minerId,
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

        data class UnitTestingData(
            var numberOfHandledObjs: Int = 0,
            var numberOfCastedObjs: Int = 0,

            var numberOfHandledVerificationBlocks: Int = 0,
            var numberOfCastedVerificationBlocks: Int = 0,

            var numberOfHandledVerificationResult: Int = 0,
            var numberOfSuccessVerifiedObjs: Int = 0,
        )
        val unitTestingData = UnitTestingData()
    }
}
