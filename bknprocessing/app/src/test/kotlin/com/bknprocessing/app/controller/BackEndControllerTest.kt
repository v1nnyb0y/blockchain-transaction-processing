package com.bknprocessing.app.controller

import com.bknprocessing.app.controllers.ExperimentDto
import com.bknprocessing.app.utils.Predefined
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BackEndControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    private fun doAsyncTest(input: ExperimentDto) {
        mvc.post("/asyncExperiment") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(input)
            accept = MediaType.APPLICATION_JSON
        }
            // .asyncDispatch()
            // .andExpect { request { asyncStarted() } }
            .andExpect { status { isOk() } }
    }

    @Test
    fun `POS and Coroutine`() {
        doAsyncTest(Predefined.COROUTINE_WITH_POS)
    }
}
