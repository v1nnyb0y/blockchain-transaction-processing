package com.bknprocessing.blockchain.data

import java.util.UUID

data class Transaction(
    val transId: UUID = UUID.randomUUID(),
)
