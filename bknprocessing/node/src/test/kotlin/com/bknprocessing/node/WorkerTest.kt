package com.bknprocessing.node

import com.bknprocessing.common.data.Transaction
import com.bknprocessing.node.dto.Block
import com.bknprocessing.node.dto.NodeInfoSubBlock
import com.bknprocessing.node.dto.VerificationDto
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID

class WorkerTest : AbstractTest<NodeApplication>(
    clazz = NodeApplication::class.java,
    constructor = { NodeApplication() },
) {

    private val objectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    @Test
    fun `should cast to json and to verificationDto`() {
        val block = Block(
            previousHash = "somePreviousHash",
            nodeInfo = NodeInfoSubBlock(12, 1, UUID.randomUUID()),
            currentHash = "someCurrentHash",
            objs = mutableListOf(Transaction()),
            nonce = 1230,
            processingTime = 0,
        )

        val contentJson = objectMapper.writeValueAsString(VerificationDto(block = block, nodeId = UUID.randomUUID()))
        val verificationDto = objectMapper.readValue(contentJson, VerificationDto::class.java)
        Assertions.assertEquals(block.nonce, verificationDto.block.nonce)
    }
}
