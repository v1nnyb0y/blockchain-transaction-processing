package com.bknprocessing.backend

import com.bknprocessing.backend.service.ExperimentRunnerService
import com.bknprocessing.backend.type.StateTransferApproach
import com.bknprocessing.backend.type.ValidatorAlgorithm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BackendApplication

fun main(args: Array<String>) {
    val job = CoroutineScope(Dispatchers.Default).launch {
        ExperimentRunnerService().start(
            100,
            10000,
            5,
            StateTransferApproach.Coroutine,
            ValidatorAlgorithm.ProofOfState
        )
    }
//    job.cancel()
    runApplication<BackendApplication>(*args)
}
