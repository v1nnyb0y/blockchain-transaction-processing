package com.bknprocessing.node.dto

import java.util.UUID

data class NodeInfo(
    val id: UUID,
    val amount: Int,
)

data class VerificationResultDto(
    val blockHash: String,
    val nodeId: UUID,
    val nodeInfo: NodeInfo,
    val verificationResult: Boolean, // TODO rnm
)
