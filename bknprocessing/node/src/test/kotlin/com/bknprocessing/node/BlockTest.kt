package com.bknprocessing.node

import com.bknprocessing.node.data.Transaction
import com.bknprocessing.node.dto.Block
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class BlockTest {

    private lateinit var block: Block

    @BeforeEach
    fun setUp() {
        block = Block(previousHash = "", generatedBy = UUID.randomUUID())
    }

    @Test
    fun adding_transaction_to_block_success() {
        Assertions.assertEquals(block.transactions.size, 0)
        block.addTransaction(Transaction())
        Assertions.assertEquals(block.transactions.size, 1)
    }
}
