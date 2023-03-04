package com.bknprocessing.node.service

import com.bknprocessing.common.globals.RpcServerConfiguration
import com.bknprocessing.common.grpc.RpcClient
import com.bknprocessing.common.grpc.RpcServer
import org.springframework.stereotype.Service

@Service
class RpcService : BaseService() {

    private lateinit var client: RpcClient
    private lateinit var server: RpcServer

    fun init(totalNodesCount: Int, isHealthy: Boolean, nodeIndex: Int, createdAt: Long) {
        client = RpcClient.INSTANCE
        server = RpcServer.INSTANCE

        server.setup(RpcServerConfiguration(capacity = totalNodesCount))
        initNode(nodeIndex, isHealthy, createdAt, totalNodesCount, client, server)
    }

    fun verifyObj(obj: Any) {
        super.verifyObj(obj, client)
    }

    fun verify(obj: Any) {
        super.verify(obj, client)
    }

    fun verifyResult(obj: Any) {
        super.verifyResult(obj, client)
    }

    fun smartContract(obj: Any) {
        super.smartContract(obj, client)
    }
}
