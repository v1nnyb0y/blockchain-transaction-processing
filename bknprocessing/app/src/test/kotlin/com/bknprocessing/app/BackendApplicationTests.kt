package com.bknprocessing.app

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun contextLoads() {
        assertNotNull(objectMapper)
    }
}
