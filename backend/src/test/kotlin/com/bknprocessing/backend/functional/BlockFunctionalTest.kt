package com.bknprocessing.backend.functional

import com.bknprocessing.backend.models.Block
import com.bknprocessing.backend.models.Transaction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class BlockFunctionalTest {

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
