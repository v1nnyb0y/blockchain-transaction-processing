package com.bknprocessing.node.nodeimpl

import com.bknprocessing.common.data.NodeMetricsData

interface INode {

    suspend fun runVerifier()
    suspend fun runMiner()
    suspend fun waitStateChangeAction()
    fun getNodeMetrics(): NodeMetricsData
}
