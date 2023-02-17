package com.bknprocessing.app.service.upper.localupper

import com.bknprocessing.app.service.upper.IUpper
import com.bknprocessing.common.ClientConfiguration
import com.bknprocessing.common.IClient
import com.bknprocessing.common.IServer
import com.bknprocessing.common.ServerConfiguration
import com.bknprocessing.node.nodeimpl.INode
import com.bknprocessing.node.nodeimpl.Node
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.time.Instant

abstract class LocalUpper<T>(
    private val client: IClient,
    private val server: IServer,
) : IUpper<T> {

    protected val nodes = mutableListOf<INode>()

    private fun constructNodeCollection(count: Int, isHealthy: Boolean, createdAt: Long, networkSize: Int) {
        for (idx in 0 until count) {
            nodes.add(
                Node<T>(
                    index = nodes.size,
                    isHealthy = isHealthy,
                    createdAt = createdAt,

                    networkSize = networkSize,

                    client = client,
                    server = server,
                ),
            )
        }
    }

    override suspend fun startNodes(nodesCount: Int, unhealthyNodesCount: Int) {
        val createdAt = Instant.now().toEpochMilli()
        constructNodeCollection(nodesCount - unhealthyNodesCount, true, createdAt, nodesCount)
        constructNodeCollection(unhealthyNodesCount, false, createdAt, nodesCount)

        client.setup(ClientConfiguration(capacity = nodes.size))
        server.setup(ServerConfiguration(capacity = nodes.size))

        supervisorScope {
            for (i in 0 until nodesCount) {
                launch { nodes[i].runMiner() }
                launch { nodes[i].runVerifier() }
                launch { nodes[i].waitStateChangeAction() }
            }
        }
    }
}
