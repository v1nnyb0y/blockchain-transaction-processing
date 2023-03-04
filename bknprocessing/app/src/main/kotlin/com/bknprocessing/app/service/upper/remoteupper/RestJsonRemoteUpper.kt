package com.bknprocessing.app.service.upper.remoteupper

import com.bknprocessing.common.globals.RestJsonServerConfiguration
import com.bknprocessing.common.globals.ServerConfiguration
import com.bknprocessing.common.rest.RestServer

class RestJsonRemoteUpper<T>(
    getNodeConfiguration: (networkSize: Int, isHealthy: Boolean, nodeIndex: Int, createdAt: Long) -> NodeConfiguration =
        { networkSize: Int, isHealthy: Boolean, nodeIndex: Int, createdAt: Long ->
            NodeConfiguration(
                totalNodesCount = networkSize,
                isHealthy = isHealthy,
                nodeIndex = nodeIndex,
                createdAt = createdAt,
            )
        },
    startNode: suspend (idx: Int, conf: NodeConfiguration) -> String? =
        { idx: Int, conf: NodeConfiguration ->
            RestServer.INSTANCE.initNode(idx, conf)
        },
    getServerConfiguration: (networkSize: Int) -> ServerConfiguration =
        { networkSize: Int ->
            RestJsonServerConfiguration(capacity = networkSize)
        },
) : RemoteUpper<T>(getNodeConfiguration, startNode, RestServer.INSTANCE, getServerConfiguration)
