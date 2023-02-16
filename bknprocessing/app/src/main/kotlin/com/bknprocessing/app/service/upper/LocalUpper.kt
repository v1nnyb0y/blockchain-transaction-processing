package com.bknprocessing.app.service.upper

import com.bknprocessing.node.nodeimpl.INode
import com.bknprocessing.node.nodeimpl.Node
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.time.Instant

class LocalUpper : IUpper {

    protected val nodes = mutableListOf<INode>()

    private fun constructNodeCollection(count: Int, isHealthy: Boolean, createdAt: Long) {
        for (idx in 0 until count) {
            nodes.add(Node(index = nodes.size, isHealthy = isHealthy, createdAt = createdAt))
        }
    }

    override suspend fun startNodes(nodesCount: Int, unhealthyNodesCount: Int) {
        val createdAt = Instant.now().toEpochMilli()
        constructNodeCollection(nodesCount - unhealthyNodesCount, true, createdAt)
        constructNodeCollection(unhealthyNodesCount, false, createdAt)

        supervisorScope {
            for (i in 0 until nodesCount) {
                launch { nodes[i].runMiner() }
                launch { nodes[i].runVerifier() }
            }
        }
    }
}
