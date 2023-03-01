package com.bknprocessing.common.data

import java.util.UUID

data class Transaction(
    val transId: UUID = UUID.randomUUID(),
)
