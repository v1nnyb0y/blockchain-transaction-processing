package com.bknprocessing.backend.models

import java.time.Instant
import java.util.UUID

data class Block(
    var currentHash: String = "",
    val timestamp: Long = Instant.now().toEpochMilli(),
    val previousHash: String,
    var nonce: Long = 0,
    val transactions: MutableList<Transaction> = mutableListOf(),
    val generatedBy: UUID
) {

    fun addTransaction(tx: Transaction) {
        transactions.add(tx)
    }
}
