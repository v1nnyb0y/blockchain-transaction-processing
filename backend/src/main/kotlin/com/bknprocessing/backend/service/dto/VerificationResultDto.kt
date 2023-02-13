package com.bknprocessing.backend.service.dto

data class VerificationResultDto(
    val blockHash: String,
    val nodeIndex: Int,
    val verificationResult: Boolean,
)
