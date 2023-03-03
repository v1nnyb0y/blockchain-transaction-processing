package com.bknprocessing.node

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.metrics.export.prometheus.EnablePrometheusMetrics

@EnablePrometheusMetrics
@SpringBootApplication
class NodeApplication

fun main(args: Array<String>) {
    runApplication<NodeApplication>(*args)
}
