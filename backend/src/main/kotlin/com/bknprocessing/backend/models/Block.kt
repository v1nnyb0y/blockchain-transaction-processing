package com.bknprocessing.backend.models

import com.bknprocessing.backend.models.Transaction
import com.bknprocessing.backend.utils.hash
import java.time.Instant

open class Block(
    val previousHash: String,
    val transactions: MutableList<Transaction> = mutableListOf(),
    val timestamp: Long = Instant.now().toEpochMilli(),
    var nonce: Long = 0,
    var hash: String = ""
) {

    init {
        hash = calculateHash()
    }

    open fun calculateHash(): String {
        return "$previousHash$transactions$timestamp$nonce".hash()
    }

    fun addTransaction(trans: Transaction) {
        transactions.add(trans)
    }
}
