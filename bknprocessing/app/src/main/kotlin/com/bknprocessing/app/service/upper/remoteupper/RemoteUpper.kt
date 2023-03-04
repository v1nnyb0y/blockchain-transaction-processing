package com.bknprocessing.app.service.upper.remoteupper

import com.bknprocessing.app.service.upper.IUpper
import com.bknprocessing.app.utils.logger
import com.bknprocessing.common.grpc.BaseProtoFile
import com.bknprocessing.common.grpc.BaseProtoFileCompanion
import com.bknprocessing.common.protoClasses.NodeInit
import com.google.protobuf.BoolValue
import com.google.protobuf.Int32Value
import com.google.protobuf.Int64Value
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import java.time.Instant

data class NodeConfiguration(
    val totalNodesCount: Int,
    val isHealthy: Boolean,
    val nodeIndex: Int,
    val createdAt: Long,
) : BaseProtoFile() {

    override fun toProto(): Any {
        val builder = NodeInit.newBuilder()

        builder.totalNodesCount = Int32Value.of(totalNodesCount)
        builder.isHealthy = BoolValue.of(isHealthy)
        builder.nodeIndex = Int32Value.of(nodeIndex)
        builder.createdAt = Int64Value.of(createdAt)

        return builder.build()
    }

    companion object : BaseProtoFileCompanion() {
        override fun fromProto(obj: Any): Any {
            val init = (obj as? NodeInit)
                ?: throw IllegalStateException("Impossible to cast proto to class")
            return NodeConfiguration(
                totalNodesCount = init.totalNodesCount.value,
                isHealthy = init.isHealthy.value,
                nodeIndex = init.nodeIndex.value,
                createdAt = init.createdAt.value,
            )
        }
    }
}

// TODO Vitalii for implementation (docker instances)
abstract class RemoteUpper<T>(
    private val getNodeConfiguration: (networkSize: Int, isHealthy: Boolean, nodeIndex: Int, createdAt: Long) -> NodeConfiguration,
    private val startNode: suspend (idx: Int, conf: NodeConfiguration) -> String?,
) : IUpper<T> {

    val log: Logger by logger()
    val nodes: MutableList<NodeConfiguration> = mutableListOf()

    private fun constructNodeCollection(count: Int, isHealthy: Boolean, createdAt: Long, networkSize: Int) = runBlocking {
        for (idx in 0 until count) {
            nodes.add(getNodeConfiguration(networkSize, isHealthy, nodes.size, createdAt))
        }
    }

    override suspend fun startNodes(nodesCount: Int, unhealthyNodesCount: Int) {
        val createdAt = Instant.now().toEpochMilli()
        constructNodeCollection(nodesCount - unhealthyNodesCount, true, createdAt, nodesCount)
        constructNodeCollection(unhealthyNodesCount, false, createdAt, nodesCount)

        supervisorScope {
            nodes.forEachIndexed { idx, conf ->
                // log.constructedNode(, nodes.size - 1)
                launch {
                    startNode(idx, conf)
                }
            }
        }
    }
}
