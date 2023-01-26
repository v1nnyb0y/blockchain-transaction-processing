package com.bknprocessing.backend.controllers

import com.bknprocessing.backend.utils.logger
import org.slf4j.Logger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BackendApplicationController {

    val log: Logger by logger()

    @GetMapping
    fun healthCheck(): String {
        log.info("Start GET query for HEALTH_CHECK");
        return "Ok"
    }
}
