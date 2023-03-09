package com.bknprocessing.node

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
abstract class AbstractTest<T : Any>(
    val clazz: Class<in T>,
    val constructor: () -> T,
) {

    lateinit var testedObj: T

    @BeforeEach
    fun setUp() {
        testedObj = constructor()
    }

    @Test
    fun contextTest() {
        Assertions.assertEquals(clazz.name, testedObj::class.java.name)
    }
}
