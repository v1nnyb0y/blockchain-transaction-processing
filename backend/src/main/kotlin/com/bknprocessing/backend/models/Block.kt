package com.bknprocessing.backend.models

import java.time.Instant

data class Block(
    var currentHash: String = "",
    val timestamp: Long = Instant.now().toEpochMilli(),
    val previousHash: String,
    var nonce: Long = 0,
    val transactions: MutableList<Transaction> = mutableListOf()
) {

    fun addTransaction(tx: Transaction) {
        transactions.add(tx)
    }
}
