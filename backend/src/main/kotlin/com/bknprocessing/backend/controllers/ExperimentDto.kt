package com.bknprocessing.backend.controllers

import com.bknprocessing.backend.type.StateTransferApproach
import com.bknprocessing.backend.type.ValidatorAlgorithm

@Suppress("unused")
data class ExperimentDto(
    val numberOfInstances: Int,
    val numberOfTransactions: Int,
    val numberOfUnhealthyNodes: Int,
    val validatorAlgo: ValidatorAlgorithm,
    val stateTransferApproach: StateTransferApproach
)
