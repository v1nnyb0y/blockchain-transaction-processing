package com.bknprocessing.node.service

import com.bknprocessing.common.data.Transaction
import com.bknprocessing.common.rest.RestClient
import com.bknprocessing.common.rest.RestServer
import com.bknprocessing.node.nodeimpl.INode
import com.bknprocessing.node.nodeimpl.Node
import com.bknprocessing.node.utils.logger
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class NodeService {

    private val log: Logger by logger()

    private lateinit var node: INode
    private lateinit var client: RestClient
    private lateinit var server: RestServer

    fun init(totalNodesCount: Int, isHealthy: Boolean, nodeIndex: Int, createdAt: Long) {
        node = Node<Transaction>(
            index = nodeIndex,
            // isHealthy = nodeIndex < totalNodesCount - unhealthyNodesCount, // 2 < 7 - 3, 4 < 7 - 3
            isHealthy = isHealthy,
            createdAt = createdAt,
            networkSize = totalNodesCount,

            client = RestClient.INSTANCE,
            server = RestServer.INSTANCE,
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

    fun verifyObj(obj: Any) {
        log.info("NodeService: verify obj")
        client.appendObjQueue(obj)
    }

    fun verify(obj: Any) {
        log.info("NodeService: verifying")
        client.appendVerificationBlockQueue(obj)
    }

    fun verifyResult(obj: Any) {
        log.info("NodeService: verifyResult")
        client.appendVerificationResultQueue(obj)
    }

    fun smartContract(obj: Any) {
        log.info("NodeService: smartContract")
        client.appendStateChangeQueue(obj)
    }
}
