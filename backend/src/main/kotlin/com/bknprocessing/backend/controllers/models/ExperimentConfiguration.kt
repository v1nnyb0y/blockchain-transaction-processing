package com.bknprocessing.backend.controllers.models

enum class ValidatorAlgorithm {
    ProofOfWork, ProofOfState
}

enum class StateTransferApproach {
    Kafka, gRPC, socket, REST, Coroutine // ktlint-disable enum-entry-name-case
}

@Suppress("unused")
data class ExperimentConfigurationDto(
    val numberOfInstances: Int,
    val numberOfTransactions: Int,
    val numberOfUnhealthyNodes: Int,
    val validatorAlgo: ValidatorAlgorithm,
    val stateTransferApproach: StateTransferApproach
)
