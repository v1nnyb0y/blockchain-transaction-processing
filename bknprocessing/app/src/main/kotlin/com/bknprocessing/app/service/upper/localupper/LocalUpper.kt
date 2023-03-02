package com.bknprocessing.app.service.upper.localupper

import com.bknprocessing.app.service.upper.IUpper
import com.bknprocessing.app.utils.clientAndServerConfigured
import com.bknprocessing.app.utils.constructedNode
import com.bknprocessing.app.utils.logger
import com.bknprocessing.common.IClient
import com.bknprocessing.common.IServer
import com.bknprocessing.common.globals.ClientConfiguration
import com.bknprocessing.common.globals.ServerConfiguration
import com.bknprocessing.common.kafka.KafkaConsumer
import com.bknprocessing.node.nodeimpl.INode
import com.bknprocessing.node.nodeimpl.Node
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import java.time.Instant

abstract class LocalUpper<T>(
    private val client: () -> IClient,
    private val getClientConfiguration: (args: Any) -> ClientConfiguration,
    private val server: () -> IServer,
    private val getServerConfiguration: (args: Any) -> ServerConfiguration,
) : IUpper<T> {

    val log: Logger by logger()
    protected val nodes = mutableListOf<INode>()

    private fun constructNodeCollection(count: Int, isHealthy: Boolean, createdAt: Long, networkSize: Int) {
        for (idx in 0 until count) {
            nodes.add(
                Node<T>(
                    index = nodes.size,
                    isHealthy = isHealthy,
                    createdAt = createdAt,

                    networkSize = networkSize,

                    client = client().apply {
                        if (this is KafkaConsumer) {
                            setup(getClientConfiguration(nodes.size))
                        }
                    },
                    server = server(),
                ),
            )
            log.constructedNode(isHealthy, nodes.size - 1)
        }
    }

    override suspend fun startNodes(nodesCount: Int, unhealthyNodesCount: Int) {
        val createdAt = Instant.now().toEpochMilli()
        constructNodeCollection(nodesCount - unhealthyNodesCount, true, createdAt, nodesCount)
        constructNodeCollection(unhealthyNodesCount, false, createdAt, nodesCount)

        if (client() !is KafkaConsumer) {
            client().setup(getClientConfiguration(nodes.size))
        }
        server().setup(getServerConfiguration(nodes.size))
        log.clientAndServerConfigured()

        supervisorScope {
            for (i in 0 until nodesCount) {
                launch { nodes[i].runMiner() }
                launch { nodes[i].runVerifier() }
                launch { nodes[i].waitStateChangeAction() }
            }
        }
    }
}
