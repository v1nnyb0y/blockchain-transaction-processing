package com.bknprocessing.node.dto

import com.bknprocessing.node.AbstractTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID

class BlockTest : AbstractTest<Block<Any>>(
    clazz = Block::class.java,
    constructor = { Block(previousHash = "", nodeInfo = NodeInfoSubBlock(amount = 0, id = UUID.randomUUID(), index = 0)) },
) {

    @Test
    fun `add object to block`() {
        Assertions.assertEquals(0, testedObj.objs.size)

        val obj = UUID.randomUUID()
        testedObj.addObj(obj)

        Assertions.assertEquals(1, testedObj.objs.size)
        Assertions.assertEquals(obj, testedObj.objs[0])
    }
}
