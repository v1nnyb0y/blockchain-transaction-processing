package com.bknprocessing.node.service

import com.bknprocessing.node.nodeimpl.INode
import com.bknprocessing.node.nodeimpl.Node
import com.bknprocessing.node.utils.logger
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

    private val verificationChannel: MutableList<ReceiveChannel<Any>> = mutableListOf()

    fun init(totalNodesCount: Int, unhealthyNodesCount: Int, nodeIndex: Int) {
        node = Node<T>(
            index = nodeIndex,
            isHealthy = nodeIndex < totalNodesCount - unhealthyNodesCount, // 2 < 7 - 3, 4 < 7 - 3
            createdAt = Instant.now().toEpochMilli(),
            networkSize = 1,

//                client = client().apply {
//                    if (this is KafkaConsumer) {
//                        setup(getClientConfiguration(nodes.size))
//                    }
//                },
//                server = server(),
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
        nodes[0]
    }

    fun verifyResult(obj: Any) {
        // TODO
        nodes[0]
    }

    fun smartContract(obj: Any) {
        // TODO
        nodes[0]
    }
}
