package com.bknprocessing.backend.controllers

import com.bknprocessing.backend.controllers.models.ExperimentConfigurationDto
import com.bknprocessing.backend.service.ExperimentService
import com.bknprocessing.backend.utils.logger
import org.slf4j.Logger
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/experiment")
class ExperimentController(
    val experimentService: ExperimentService
) {

    val log: Logger by logger()

    @PostMapping
    fun startExperiment(@RequestBody configuration: ExperimentConfigurationDto): Boolean {
        log.info("Start POST query for START_EXPERIMENT with data: $configuration")
        return experimentService.startExperiment(
            configuration.numberOfInstances,
            configuration.numberOfTransactions,
            configuration.numberOfUnhealthyNodes,
            configuration.stateTransferApproach,
            configuration.validatorAlgo
        )
    }
}
