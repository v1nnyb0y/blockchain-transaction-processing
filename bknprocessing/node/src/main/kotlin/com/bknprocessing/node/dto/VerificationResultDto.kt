package com.bknprocessing.node.dto

import java.util.UUID

data class VerificationResultDto(
    val blockHash: String,
    val nodeId: UUID,
    val verificationResult: Boolean,
)
