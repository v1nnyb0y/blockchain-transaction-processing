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
import java.util.concurrent.atomic.AtomicInteger
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

    var healthCheckGauge: AvgValue = AvgValue(
        value = meterRegistry.gauge("health_check_gauge_val", AtomicLong(0))!!,
        count = meterRegistry.gauge("health_check_gauge_count", AtomicInteger(0))!!,
    )
    var verifyObjMetricAvg: AvgValue = AvgValue(
        value = meterRegistry.gauge("verify_obj_gauge_val", AtomicLong(0))!!,
        count = meterRegistry.gauge("verify_obj_gauge_count", AtomicInteger(0))!!,
    )

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
        }.also {
            val oldCount = verifyObjMetricAvg.count.getAndIncrement()
            if (oldCount == 0) {
                verifyObjMetricAvg.value.set((verifyObjMetricAvg.value.get() + it) / 1)
            } else {
                verifyObjMetricAvg.value.set((verifyObjMetricAvg.value.get() + it) / oldCount)
            }
        }
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

    var index = 0
    var modifyList = mutableListOf(25, 50, 75, 100, 0, 0)
    // 25  37  50  62  50  41

    @Timed(description = "healthCheck_metric_timed", histogram = true)
    @GetMapping("/healthCheck")
    fun healthCheck(): String {
        val oldCount = healthCheckGauge.count.getAndIncrement()
        log.info("OldCount: $oldCount, healthCheckGauge.value: ${healthCheckGauge.value}")
        if (oldCount == 0) {
            healthCheckGauge.value.set((healthCheckGauge.value.get() + modifyList[0] + modifyList[index]) / 1)
        } else {
            healthCheckGauge.value.set((modifyList[0].toLong() + modifyList[index++]) / oldCount)
        }
        return "Ok"
    }
}

data class AvgValue(val value: AtomicLong, val count: AtomicInteger)
