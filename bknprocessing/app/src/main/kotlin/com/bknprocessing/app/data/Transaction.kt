package com.bknprocessing.app.data

import java.util.UUID

data class Transaction(
    val transId: UUID = UUID.randomUUID(),
)
