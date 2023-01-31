package com.bknprocessing.backend.models

import com.bknprocessing.backend.utils.hash
import java.time.Instant

data class Block(
    val previousHash: String,
    val transactions: MutableList<Transaction> = mutableListOf(),
    val timestamp: Long = Instant.now().toEpochMilli(),
    var nonce: Long = 0,
    var hash: String = "",
    private var isHealthy: Boolean = true
) {

    init {
        hash = calculateHash()
    }

    fun calculateHash(): String {
        val errorHash = previousHash.hash()

        return "$previousHash$transactions$timestamp$nonce".hash()
    }

    fun addTransaction(trans: Transaction) {
        transactions.add(trans)
    }
}
