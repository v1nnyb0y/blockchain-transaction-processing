package com.bknprocessing.node.service

import com.bknprocessing.common.IClient
import com.bknprocessing.common.IServer
import com.bknprocessing.common.data.Transaction
import com.bknprocessing.common.rest.RestClient
import com.bknprocessing.node.nodeimpl.INode
import com.bknprocessing.node.nodeimpl.Node
import com.bknprocessing.node.utils.logger
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger

abstract class BaseService {

    private val log: Logger by logger()

    fun initNode(
        nodeIndex: Int,
        isHealthy: Boolean,
        createdAt: Long,
        totalNodesCount: Int,
        client: IClient,
        server: IServer
    ) {
        val node: INode = Node<Transaction>(
            index = nodeIndex,
            // isHealthy = nodeIndex < totalNodesCount - unhealthyNodesCount, // 2 < 7 - 3, 4 < 7 - 3
            isHealthy = isHealthy,
            createdAt = createdAt,
            networkSize = totalNodesCount,

            client = client,
            server = server,
        )

        runBlocking {
            supervisorScope {
                launch { node.runMiner() }
                launch { node.runVerifier() }
                launch { node.waitStateChangeAction() }
            }
        }
    }

    fun verifyObj(obj: Any, client: RestClient) {
        log.info("NodeService: verify obj")
        client.appendObjQueue(obj)
    }

    fun verify(obj: Any, client: RestClient) {
        log.info("NodeService: verifying")
        client.appendVerificationBlockQueue(obj)
    }

    fun verifyResult(obj: Any, client: RestClient) {
        log.info("NodeService: verifyResult")
        client.appendVerificationResultQueue(obj)
    }

    fun smartContract(obj: Any, client: RestClient) {
        log.info("NodeService: smartContract")
        client.appendStateChangeQueue(obj)
    }
}