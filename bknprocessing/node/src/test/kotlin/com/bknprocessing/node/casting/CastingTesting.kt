package com.bknprocessing.node.casting

import com.bknprocessing.node.dto.Block
import com.bknprocessing.node.dto.VerificationDto
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class CastingTesting {

    data class Transaction(
        val transId: UUID = UUID.randomUUID(),
    )

    data class NodeInfoSubBlock(
        val amount: Int,
        val index: Int,
        val id: UUID,
    )

    data class Block(
        val previousHash: String,
        val nodeInfo: NodeInfoSubBlock?,

        var currentHash: String = "",
        val timestamp: Long = Instant.now().toEpochMilli(),

        val objs: MutableList<Transaction> = mutableListOf(),

        var nonce: Long = 0,
    ) {

        fun addObj(tx: Transaction) {
            objs.add(tx)
        }
    }

    data class VerificationDto(
        val nodeId: UUID,
        val block: Block,
    )

    private val objectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(JavaTimeModule())

    @Test
    fun castToJsonAndToObject() {
        val block = Block(
            previousHash = "somePreviousHash",
            nodeInfo = NodeInfoSubBlock(12, 1, UUID.randomUUID()),
            currentHash = "someCurrentHash",
            objs = mutableListOf(Transaction()),
            nonce = 1230,
        )

        val verificationDtoJson = objectMapper.writeValueAsString(VerificationDto(block = block, nodeId = UUID.randomUUID()))
        val castedVerificationDto = objectMapper.readValue(verificationDtoJson, VerificationDto::class.java)

        Assertions.assertTrue(true)
    }
}
