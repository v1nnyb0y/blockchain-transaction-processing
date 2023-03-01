package com.bknprocessing.app.service.upper.remoteupper

data class NodeConfiguration(
    val totalNodesCount: Int,
    val isHealthy: Boolean,
    val nodeIndex: Int,
    val createdAt: Long,
)

class RestJsonRemoteUpper<T>(
    getNodeConfiguration: (networkSize: Int, isHealthy: Boolean, nodeIndex: Int, createdAt: Long) -> Any =
        { networkSize: Int, isHealthy: Boolean, nodeIndex: Int, createdAt: Long ->
            NodeConfiguration(
                totalNodesCount = networkSize,
                isHealthy = isHealthy,
                nodeIndex = nodeIndex,
                createdAt = createdAt,
            )
        },
) : RemoteUpper<T>(getNodeConfiguration)
