package com.bknprocessing.blockchain.dto

import com.bknprocessing.blockchain.data.Transaction
import java.time.Instant
import java.util.UUID

data class Block(
    var currentHash: String = "",
    val timestamp: Long = Instant.now().toEpochMilli(),
    val previousHash: String,
    var nonce: Long = 0,
    val transactions: MutableList<Transaction> = mutableListOf(),
    val generatedBy: UUID,
) {

    fun addTransaction(tx: Transaction) {
        transactions.add(tx)
    }
}
