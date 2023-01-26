package com.bknprocessing.backend.entity

import java.util.UUID

data class Transaction(
    val transId: UUID = UUID.randomUUID()
)
