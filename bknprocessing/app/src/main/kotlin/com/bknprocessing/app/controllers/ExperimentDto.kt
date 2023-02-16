package com.bknprocessing.app.controllers

import com.bknprocessing.app.type.StateTransferApproach
import com.bknprocessing.app.type.ValidatorAlgorithm

@Suppress("unused")
data class ExperimentDto(
    val numberOfInstances: Int,
    val numberOfTransactions: Int,
    val numberOfUnhealthyNodes: Int,
    val validatorAlgo: ValidatorAlgorithm,
    val stateTransferApproach: StateTransferApproach,
)
