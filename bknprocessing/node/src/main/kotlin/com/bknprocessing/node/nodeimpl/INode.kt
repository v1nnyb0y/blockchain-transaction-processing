package com.bknprocessing.node.nodeimpl

interface INode {

    suspend fun runVerifier()
    suspend fun runMiner()
    suspend fun waitStateChangeAction()
}
