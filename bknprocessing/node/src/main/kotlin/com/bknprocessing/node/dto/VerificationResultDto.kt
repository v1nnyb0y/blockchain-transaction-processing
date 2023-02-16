package com.bknprocessing.node.dto

data class VerificationResultDto(
    val blockHash: String,
    val nodeIndex: Int,
    val verificationResult: Boolean,
)
