package com.bknprocessing.backend.models

import com.bknprocessing.backend.models.Block
import com.bknprocessing.backend.models.Transaction
import com.bknprocessing.backend.utils.hash
import java.time.Instant

class UnhealthyBlock(
    previousHash: String,
    transactions: MutableList<Transaction> = mutableListOf(),
    timestamp: Long = Instant.now().toEpochMilli(),
    nonce: Long = 0,
    hash: String = ""
) : Block(previousHash, transactions, timestamp, nonce, hash) {

    override fun calculateHash(): String {
        val errorHash = previousHash.hash()
        return "$errorHash$transactions$timestamp$nonce".hash()
    }
}
