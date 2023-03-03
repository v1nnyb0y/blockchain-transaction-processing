package com.bknprocessing.node.service

import com.bknprocessing.common.globals.RestJsonServerConfiguration
import com.bknprocessing.common.rest.RestClient
import com.bknprocessing.common.rest.RestServer
import org.springframework.stereotype.Service

@Service
class NodeService : BaseService() {

    private lateinit var client: RestClient
    private lateinit var server: RestServer

    fun init(totalNodesCount: Int, isHealthy: Boolean, nodeIndex: Int, createdAt: Long) {
        client = RestClient.INSTANCE
        server = RestServer.INSTANCE

        server.setup(RestJsonServerConfiguration(capacity = totalNodesCount))
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
