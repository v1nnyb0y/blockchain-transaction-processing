package com.bknprocessing.backend.service.dto

import com.bknprocessing.backend.models.Block

data class VerificationDto(
    val block: Block,
    val nodeIndex: Int
)
