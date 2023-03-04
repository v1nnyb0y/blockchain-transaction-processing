package com.bknprocessing.node.controller

import com.bknprocessing.node.service.NodeService
import com.bknprocessing.node.utils.logger
import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.Logger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.measureTimeMillis

data class StarterConfig(
    val totalNodesCount: Int,
    val isHealthy: Boolean,
    val nodeIndex: Int,
    val createdAt: Long,
)

@RestController
class NodeController(
    private val nodeService: NodeService,
    private val meterRegistry: MeterRegistry,
) {

    private val log: Logger by logger()
    var healthCheckAvg: AtomicLong = meterRegistry.gauge("health_check_avg", AtomicLong(0))!!
    var verifyObjMetricGauge: AtomicLong = meterRegistry.gauge("verify_obj_metric_gauge", AtomicLong(0))!!

    @PostMapping("/init")
    fun init(@RequestBody starterConfig: StarterConfig): String {
        log.info("NodeController: init processed")
        nodeService.init(
            starterConfig.totalNodesCount,
            starterConfig.isHealthy,
            starterConfig.nodeIndex,
            starterConfig.createdAt,
        )
        return "Ok"
    }

    @Timed(description = "verify_obj_metric_timed", histogram = true)
    @PostMapping("/verifyObj")
    fun verifyObj(@RequestBody obj: Any) {
        log.info("NodeController: verifyObj processed")
        measureTimeMillis {
            nodeService.verifyObj(obj)
        }.also { verifyObjMetricGauge.set(it) }
    }

    @PostMapping("/verify")
    fun verify(@RequestBody obj: Any) {
        log.info("NodeController: verify processed")
        nodeService.verify(obj)
    }

    @PostMapping("/verifyResult")
    fun verifyResult(@RequestBody obj: Any) {
        log.info("NodeController: verifyResult processed")
        nodeService.verifyResult(obj)
    }

    @PostMapping("/smartContract")
    fun smartContract(@RequestBody obj: Any) {
        log.info("NodeController: smartContract processed")
        nodeService.smartContract(obj)
    }

    var modifyList = mutableListOf<Int>(25, 50, 75)

    @Timed(description = "healthCheck_metric_timed", histogram = true)
    @GetMapping("/healthCheck")
    fun healthCheck(): String {
        healthCheckAvg.set(modifyList[0].toLong())
        modifyList.removeAt(0)
        return "Ok"
    }
}
