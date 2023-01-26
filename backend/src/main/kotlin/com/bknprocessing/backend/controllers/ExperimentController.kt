package com.bknprocessing.backend.controllers

import com.bknprocessing.backend.service.ExperimentRunnerService
import com.bknprocessing.backend.utils.logger
import org.slf4j.Logger
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/experiment")
class ExperimentController(
    val experimentRunnerService: ExperimentRunnerService
) {

    val log: Logger by logger()

    @PostMapping
    fun startExperiment(@RequestBody configuration: ExperimentDto): Boolean {
        log.info("Start POST query for START_EXPERIMENT with data: $configuration")
        return experimentRunnerService.startExperiment(
            configuration.numberOfInstances,
            configuration.numberOfTransactions,
            configuration.numberOfUnhealthyNodes,
            configuration.stateTransferApproach,
            configuration.validatorAlgo
        )
    }
}
