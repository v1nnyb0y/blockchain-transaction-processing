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

data class CustomMetrics(val totalValue: AtomicLong, val avgValue: AtomicLong, val count: AtomicInteger)

@RestController
class NodeController(
    private val nodeService: NodeService,
    private val meterRegistry: MeterRegistry,
) {

    private val log: Logger by logger()

    var healthCheckGauge: CustomMetrics = CustomMetrics(
        totalValue = meterRegistry.gauge("health_check_gauge_total_val", AtomicLong(0))!!,
        avgValue = meterRegistry.gauge("health_check_gauge_avg_val", AtomicLong(0))!!,
        count = meterRegistry.gauge("health_check_gauge_count", AtomicInteger(0))!!, // TODO counter metric
    )
    var verifyObjMetricAvg: CustomMetrics = CustomMetrics(
        totalValue = meterRegistry.gauge("verify_obj_gauge_total_val", AtomicLong(0))!!,
        avgValue = meterRegistry.gauge("verify_obj_gauge_avg_val", AtomicLong(0))!!,
        count = meterRegistry.gauge("verify_obj_gauge_count", AtomicInteger(0))!!, // TODO counter metric
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
                verifyObjMetricAvg.totalValue.addAndGet(it)
                verifyObjMetricAvg.avgValue.set((verifyObjMetricAvg.totalValue.get() + it) / 1)
            } else {
                verifyObjMetricAvg.totalValue.addAndGet(it)
                verifyObjMetricAvg.avgValue.set((verifyObjMetricAvg.totalValue.get()) / oldCount)
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
    var modifyList = mutableListOf(25, 50, 75, 100, 0, 0) // 25, 37, 50, 62, 50, 41
    @Timed(description = "healthCheck_metric_timed", histogram = true)
    @GetMapping("/healthCheck")
    fun healthCheck(): String {
        val count = healthCheckGauge.count.incrementAndGet()
        healthCheckGauge.totalValue.addAndGet(modifyList[index].toLong())
        healthCheckGauge.avgValue.set((healthCheckGauge.totalValue.get()) / count)
        log.info("After: hsCount: ${healthCheckGauge.count}, hsAvg: ${healthCheckGauge.avgValue}, hsTotal: ${healthCheckGauge.totalValue}")
        index++
        return "Ok"
    }
}
