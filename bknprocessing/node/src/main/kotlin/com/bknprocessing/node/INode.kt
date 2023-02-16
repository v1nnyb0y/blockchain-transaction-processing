package com.bknprocessing.node

import java.util.UUID

interface INode : INodeVerifier, INodeMiner {

    val id: UUID
    val index: Int
    var amount: Int
    val isHealthy: Boolean
}
