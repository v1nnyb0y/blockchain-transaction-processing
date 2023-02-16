package com.bknprocessing.app.service.upper

interface IUpper {

    suspend fun startNodes(nodesCount: Int, unhealthyNodesCount: Int)
}
