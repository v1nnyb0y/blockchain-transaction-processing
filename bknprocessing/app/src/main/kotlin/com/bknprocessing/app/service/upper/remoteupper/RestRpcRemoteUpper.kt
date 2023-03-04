package com.bknprocessing.app.service.upper.remoteupper

import com.bknprocessing.common.globals.RpcServerConfiguration
import com.bknprocessing.common.globals.ServerConfiguration
import com.bknprocessing.common.grpc.BaseProtoFile
import com.bknprocessing.common.grpc.RpcServer

class RestRpcRemoteUpper<T>(
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
            RpcServer.INSTANCE.initNode(idx, conf as BaseProtoFile)
        },
    getServerConfiguration: (networkSize: Int) -> ServerConfiguration =
        { networkSize: Int ->
            RpcServerConfiguration(capacity = networkSize)
        },
) : RemoteUpper<T>(getNodeConfiguration, startNode, RpcServer.INSTANCE, getServerConfiguration)
