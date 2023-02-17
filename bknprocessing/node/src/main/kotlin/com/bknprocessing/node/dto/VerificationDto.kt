package com.bknprocessing.node.dto

import java.util.UUID

data class VerificationDto<T>(
    val nodeId: UUID,
    val block: Block<T>,
)
