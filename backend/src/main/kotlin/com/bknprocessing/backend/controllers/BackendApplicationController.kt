package com.bknprocessing.backend.controllers

import com.bknprocessing.backend.service.BlockChainService
import com.bknprocessing.backend.utils.logger
import org.slf4j.Logger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class BackendApplicationController(
    val blockChainService: BlockChainService,
) {

    val log: Logger by logger()

    @GetMapping("/healthCheck")
    fun healthCheck(): String {
        log.info("Start GET query for HEALTH_CHECK")
        return "Ok"
    }

    @PostMapping("/asyncExperiment")
    suspend fun startExperiment(@RequestBody configuration: ExperimentDto) {
        log.info("Start POST query for START_EXPERIMENT with data: $configuration")
        blockChainService.createPoolAndRun(
            numberOfInstances = configuration.numberOfInstances,
            numberOfTransactions = configuration.numberOfTransactions,
            numberOfUnhealthyNodes = configuration.numberOfUnhealthyNodes,
            stateTransferApproach = configuration.stateTransferApproach,
            validatorAlgorithm = configuration.validatorAlgo,
        )
    }
}
