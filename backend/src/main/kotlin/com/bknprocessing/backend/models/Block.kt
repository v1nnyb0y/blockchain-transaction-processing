package com.bknprocessing.backend.models

import com.bknprocessing.backend.utils.hash
import java.time.Instant

data class Block(
    var currentHash: String = "",
    val timestamp: Long = Instant.now().toEpochMilli(),
    val previousHash: String,
    var nonce: Long = 0,
    val transactions: MutableList<Transaction> = mutableListOf()
) {

    init {
        currentHash = calculateBlockHash()
    }

    fun calculateBlockHash(): String {
        val errorHash = previousHash.hash()

        return "$previousHash$transactions$timestamp$nonce".hash()
    }

    fun addTransaction(tx: Transaction) {
        transactions.add(tx)
    }
}
