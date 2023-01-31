package com.bknprocessing.backend.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BackendWebClientTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun healthCheck() {
        mvc.get("/healthCheck")
            .andExpect {
                status { isOk() }
                content { string("Ok") }
            }
    }
}
