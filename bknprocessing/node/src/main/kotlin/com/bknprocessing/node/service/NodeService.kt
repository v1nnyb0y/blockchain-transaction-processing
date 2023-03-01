package com.bknprocessing.node.service

import com.bknprocessing.common.IClient
import com.bknprocessing.common.IServer
import com.bknprocessing.common.coroutine.CoroutineTransferStorage
import com.bknprocessing.common.data.Transaction
import com.bknprocessing.common.globals.ClientConfiguration
import com.bknprocessing.common.globals.ServerConfiguration
import com.bknprocessing.node.nodeimpl.INode
import com.bknprocessing.node.nodeimpl.Node
import com.bknprocessing.node.utils.logger
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class NodeService {

    private val log: Logger by logger()

    private lateinit var node: INode

    fun init(totalNodesCount: Int, unhealthyNodesCount: Int, nodeIndex: Int) {
        node = Node<Transaction>(
            index = nodeIndex,
            isHealthy = nodeIndex < totalNodesCount - unhealthyNodesCount, // 2 < 7 - 3, 4 < 7 - 3
            createdAt = Instant.now().toEpochMilli(),
            networkSize = 1,

            client = RestClient(),
            server = RestServer(),
        )

        runBlocking {
            supervisorScope {
                for (i in 0 until totalNodesCount) {
                    launch { node.runMiner() }
                    launch { node.runVerifier() }
                    launch { node.waitStateChangeAction() }
                }
            }
        }
    }

    fun verify(obj: Any) {
        // TODO
    }

    fun verifyResult(obj: Any) {
        // TODO
    }

    fun smartContract(obj: Any) {
        // TODO
    }
}

class RestServer : IServer {

    override fun setup(configuration: ServerConfiguration) {}

    @OptIn(ObsoleteCoroutinesApi::class)
    override fun sendObj(obj: Any, to: String): Boolean {
        return when (to) {
            Topics.ObjQueue.name -> CoroutineTransferStorage.objChannel.trySend(obj).isSuccess
            Topics.VerificationBlockQueue.name -> CoroutineTransferStorage.blockVerificationChannel.trySend(obj).isSuccess
            Topics.VerificationResultBlockQueue.name -> CoroutineTransferStorage.blockVerificationResultChannel.trySend(
                obj,
            ).isSuccess
            Topics.StateChange.name -> CoroutineTransferStorage.smartContractChannel.trySend(obj).isSuccess
            else -> false
        }
    }
}

@OptIn(ObsoleteCoroutinesApi::class)
class RestClient : IClient {

    companion object {
        val objChannel = Channel<Any>(capacity = 1)
        var blockVerificationChannel = BroadcastChannel<Any>(capacity = 1)
        val blockVerificationResultChannel = Channel<Any>(capacity = Channel.UNLIMITED)
        var smartContractChannel = BroadcastChannel<Any>(capacity = 1)
    }

    private val blockVerificationReceiveChannel: ReceiveChannel<Any> = blockVerificationChannel.openSubscription()
    private val smartContractReceiveChannel: ReceiveChannel<Any> = smartContractChannel.openSubscription()

    @OptIn(ObsoleteCoroutinesApi::class)
    override fun setup(configuration: ClientConfiguration) {
        blockVerificationChannel = BroadcastChannel(capacity = 1)
        smartContractChannel = BroadcastChannel(capacity = 1)
    }

    override fun getObj(from: String): Any? {
        return getObj(from, -1)
    }

    override fun getObj(from: String, who: Int): Any? {
        return when (from) {
            Topics.ObjQueue.name -> objChannel.tryReceive().getOrNull()
            Topics.VerificationBlockQueue.name -> {
                if (who == -1) throw IllegalStateException("Impossible to read subscription of -1 invoker")
                blockVerificationReceiveChannel.tryReceive().getOrNull()
            }
            Topics.VerificationResultBlockQueue.name -> blockVerificationResultChannel.tryReceive().getOrNull()
            Topics.StateChange.name -> {
                if (who == -1) throw IllegalStateException("Impossible to read subscription of -1 invoker")
                smartContractReceiveChannel.tryReceive().getOrNull()
            }
            else -> null
        }
    }
}

private enum class Topics { ObjQueue, VerificationBlockQueue, VerificationResultBlockQueue, StateChange, }
