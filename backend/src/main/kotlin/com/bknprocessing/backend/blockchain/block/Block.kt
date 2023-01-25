package com.bknprocessing.backend.service.blockchain.block

import com.bknprocessing.backend.service.blockchain.Transaction
import com.bknprocessing.backend.utils.hash
import java.time.Instant

data class Block(
    val previousHash: String,
    val transactions: MutableList<Transaction> = mutableListOf(),
    val timestamp: Long = Instant.now().toEpochMilli(),
    val nonce: Long = 0,
    var hash: String = ""
) {

    init {
        hash = calculateHash()
    }

    fun calculateHash(): String {
        return "$previousHash$transactions$timestamp$nonce".hash()
    }

    fun addTransaction(trans: Transaction) {
        transactions.add(trans)
    }
}
