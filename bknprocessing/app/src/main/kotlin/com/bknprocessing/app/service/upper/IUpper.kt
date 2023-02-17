package com.bknprocessing.app.service.upper

interface IUpper<T> {

    suspend fun startNodes(nodesCount: Int, unhealthyNodesCount: Int)
}
