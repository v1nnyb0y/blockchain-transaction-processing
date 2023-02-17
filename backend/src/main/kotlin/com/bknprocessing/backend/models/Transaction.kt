package com.bknprocessing.backend.models

import java.util.UUID

data class Transaction(
    val transId: UUID = UUID.randomUUID(),
)
