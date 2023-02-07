package com.bknprocessing.backend.service.dto

data class VerificationResultDto(
    val blockHash: String,
    val verificationResult: Boolean,
    val nodeIndex: Int
)
