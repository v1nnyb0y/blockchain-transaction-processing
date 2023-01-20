package com.bknprocessing.backend.service.blockchain

import java.util.UUID

data class Transaction(
    val transId: UUID = UUID.randomUUID()
)