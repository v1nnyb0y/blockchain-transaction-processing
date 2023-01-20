package com.bknprocessing.backend.controllers.models

enum class ValidatorAlgorithm {
    ProofOfWork, ProofOfState
}

enum class StateTransferApproach {
    Kafka, gRPC, socket, REST // ktlint-disable enum-entry-name-case
}

@Suppress("*")
data class ExperimentConfigurationDto(
    val numberOfInstances: Int,
    val numberOfTransactions: Int,
    val validatorAlgo: ValidatorAlgorithm,
    val stateTransferApproach: StateTransferApproach
)
